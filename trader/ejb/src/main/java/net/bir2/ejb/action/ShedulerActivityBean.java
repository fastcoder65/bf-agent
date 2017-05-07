package net.bir2.ejb.action;

import com.betfair.aping.api.ApiNgJsonRpcOperations;
import com.betfair.aping.entities.*;
import com.betfair.aping.enums.*;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.HttpClientSSO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.pack.util.AccountConstants;
import net.bir2.ejb.session.market.BaseService;
import net.bir2.ejb.session.market.BaseServiceBean;
import net.bir2.ejb.session.market.MarketService;
import net.bir2.handler.GlobalAPI_bk;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Market4User;
import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir2.multitrade.util.APIContext;
import net.bir2.util.DTAction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.*;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//@Lock(LockType.WRITE)
//@AccessTimeout(value=60, unit = TimeUnit.SECONDS )

//@Lock(LockType.READ)
@Startup
@Singleton
public class ShedulerActivityBean implements ShedulerActivity {

    @EJB
    private BaseService baseService;

    public BaseService getBaseService() {
        return baseService;
    }

    @EJB
    private MarketService marketService;

    @EJB
    private ApiNgJsonRpcOperations jsonOperations;

/*
    private static final Logger log = Logger.getLogger(ShedulerActivityBean.class);
*/

    @Inject
    private Logger log;


    private Map<String, Uzer> activeUsers = new ConcurrentHashMap<String, Uzer>();

    public Map<String, Uzer> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Map<String, Uzer> activeUsers) {
        this.activeUsers = activeUsers;
    }

    private Map<String, Market> activeMarkets = new ConcurrentHashMap<String, Market>();


    public Map<String, Market> getActiveMarkets() {
        return activeMarkets;
    }

    public void setActiveMarkets(Map<String, Market> activeMarkets) {
        this.activeMarkets = activeMarkets;
    }

    public Market getActiveMarket(String marketId) {
        Market result = null;
        try {
            result = activeMarkets.get(marketId);
        } catch (Exception e) {
            log.log(Level.SEVERE, "getActiveMarket ERROR:" + e);
        }
        return result;
    }

    private List<Double> allValidOdds;

    public List<Double> getAllValidOdds() {
        if (allValidOdds == null) {
            log.warning("!! allValidOdds is null!! ");
            allValidOdds = retrieveValidOdds();
            log.info("getAllValidOdds() - found valid odds: " + allValidOdds.size());
        }
        return allValidOdds;
    }

    public List<Double> retrieveValidOdds() {

            List<Double> allValidOdds = new ArrayList<Double>();
            Double currentOdds = BaseServiceBean.MIN_ODDS;
            allValidOdds.add(currentOdds);

            while (currentOdds < BaseServiceBean.MAX_ODDS) {
                currentOdds = baseService.getUpOdds(currentOdds);
                //   log.info("get next odds: " + currentOdds);
                allValidOdds.add(currentOdds);
            }
            log.info("getAllValidOdds() - found valid odds: " + allValidOdds.size());

        return allValidOdds;
    }

    public void setActiveMarket(String marketId, Market market) {
        activeMarkets.put(marketId, market);
    }

/*
	public void updatePrices() {
		log.info("start update prices..");
	}

	public void updateBets() {
		log.info("start update bets..");
	}
*/

    public void sendKeepAlive(String login) {

        Uzer currentUser = getActiveUsers().get(login);
        try {
            if (currentUser != null)

                GlobalAPI_bk.keepAlive(currentUser.getApiContext());

        } catch (Exception e) {
            log.log(Level.SEVERE, " sendKeepAlive error: ", e);
        }

    }

    public boolean add2ActiveUsers(String login, Uzer uzer) {
        APIContext apiContext = new APIContext();
        boolean result = false;
        try {
            GlobalAPI_bk.login(apiContext, uzer.getExLogin(), uzer
                    .getExPassword());
            log.info(uzer + " has log in successfully.");
            result = true;
        } catch (Exception e) {
            log.log(Level.SEVERE, "*** Failed to log in: ", e);
        }

        if (result) {
            uzer.setApiContext(apiContext);
            activeUsers.put(login, uzer);
        }
        return result;
    }

    /**
     * ������� ����������� � ���, ��� ���� ��������� ��������� �������� �����
     *
     * @param sourceSystem
     * ������� �������
     */

    public static final String priceRequestQueue = "/jms/queue/PriceRequestQueue";

    public void sendRequest(Action action, String login, String marketId) {
     //   log.info("sendRequest() - hash:");
        javax.jms.Connection jmsConnection = null;
        Session session = null;
        MessageProducer producer = null;
        try {

            InitialContext ctx = new InitialContext();

            Queue queue = (Queue) ctx.lookup(priceRequestQueue);

            QueueConnectionFactory qcf = (QueueConnectionFactory) ctx
                    // .lookup("java:JmsXA");
                    .lookup("ConnectionFactory");

            /**
             * ������������ � jms-����������
             */
            jmsConnection = qcf.createConnection();
            /**
             * ������� ������
             */
            session = jmsConnection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            /**
             * ������� ����������� (���������)
             */
            producer = session.createProducer(queue);

            /**
             * ��������� ��������� �� �������� ��������� ��� ���� �� 1 ������
             * ��������� �� ����� ���������� �� ��� ����� ������� �� �������
             */
            producer.setTimeToLive(60 * 1000); // 1 min
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            /**
             * ������� ���� ���������
             */

            MapMessage message = session.createMapMessage();
            message.setString(SheduleRequestMessageListener.ACTION_PROPERTY,
                    action.name());
            message.setString(SheduleRequestMessageListener.LOGIN_PROPERTY,
                    login);
            message.setString(SheduleRequestMessageListener.MARKET_ID_PROPERTY,
                    marketId);

            /**
             * ����������
             */
            producer.send(message);
        } catch (JMSException jmse) {
            log.log(Level.SEVERE, "Can't send message: ", jmse);
        } catch (NameNotFoundException e) {
            log
                    .severe("Queue not found: " + priceRequestQueue);
        } catch (NamingException e) {
            log.log(Level.SEVERE, "Naming exception: ", e);

        } finally {
            try {
                if (producer != null) {
                    producer.close();
                }
                if (session != null) {
                    session.close();
                }
                if (jmsConnection != null) {
                    jmsConnection.close();
                }
            } catch (JMSException jmse) {
                log.log(Level.SEVERE, "Error in finally block: ", jmse);
            }
        }
    }


    public void updateUserMarkets() {
       // log.info("updateUserMarkets() - hash:");
        Calendar now = Calendar.getInstance();

        for (Uzer uzer : activeUsers.values()) {
            @SuppressWarnings("unused")
            int i = 1;
            for (Market4User market4User : uzer.getMarket4Users()) {

                Date marketStartTime = market4User.getLinkedMarket()
                        .getMarketTime();
                log.fine(new StringBuilder(100).append("### marketStartTime = ").append(marketStartTime).toString());

                // Date marketSTime =
                // market4User.getLinkedMarket().getMarketTime();
                // log.info("@@@ marketSTime = " + marketSTime );

                Calendar turnOnTime = Calendar.getInstance();
                Calendar turnOffTime = Calendar.getInstance();

                turnOnTime.setTime(marketStartTime);
                turnOffTime.setTime(marketStartTime);

                Integer turnOnTimeOffsetHours = market4User
                        .getTurnOnTimeOffsetHours();
                Integer turnOnTimeOffsetMinutes = market4User
                        .getTurnOnTimeOffsetMinutes();

                turnOnTime.add(Calendar.HOUR, turnOnTimeOffsetHours);
                turnOnTime.add(Calendar.MINUTE, turnOnTimeOffsetMinutes);

                Integer turnOffTimeOffsetHours = market4User
                        .getTurnOffTimeOffsetHours();
                Integer turnOffTimeOffsetMinutes = market4User
                        .getTurnOffTimeOffsetMinutes();

                turnOffTime.add(Calendar.HOUR, turnOffTimeOffsetHours);
                turnOffTime.add(Calendar.MINUTE, turnOffTimeOffsetMinutes);

                String _marketId = market4User.getLinkedMarket().getMarketId();
                String marketName = new StringBuilder(100).append(market4User.getLinkedMarket().getMenuPath()).append('/').append(market4User.getLinkedMarket().getName()).toString();
                log.fine(new StringBuilder(100).append("Market Id=").append(_marketId).append(", Market Name =").append(marketName).append(", turnOnTime=").append(turnOnTime.getTime()).append(", turnOffTime=").append(turnOffTime.getTime()).toString());

                String market_id = String.valueOf(_marketId);

                if (activeMarkets.containsKey(_marketId)) {
                    if (!now.before(turnOnTime) && !now.after(turnOffTime)) {
                        boolean isOnAir = market4User.isOnAir();
                        /*
                        if (!isOnAir) {
                            market4User.setOnAir(true);
                            marketService.merge(market4User);
                        }
                        */
                    }
                    log.fine(new StringBuilder(100).append("### do update Market {marketId =").append(_marketId).append(", turnOnTime=").append(turnOnTime.getTime()).append(", turnOffTime=").append(turnOffTime.getTime()).append(" }").toString());

                    sendRequest(Action.UPDATE_MARKET, uzer.getLogin(), market_id);

                    // baseService.sendDelayedRequest(Action.UPDATE_MARKET,
                    // user.getLogin(), market_id, i);
                }
                i++;
            }
        }
    }

    // Lifecycle methods
    @PostConstruct
    public void create() {
        log.info("** ShedulerActivityBean  ** constructor, hashcode=" + this.hashCode());
        activeMarkets.clear();
        // sendRequest(Action.LOAD_ACTIVE_USERS);
        // sendRequest(Action.LOAD_ACTIVE_MARKETS);
        // sendRequest(Action.UPDATE_MARKET_PRICES);
    }

    private void logoutActiveUsers() {
        for (Uzer uzer : activeUsers.values()) {
            if (uzer.getApiContext() != null) {
                try {
                    GlobalAPI_bk.logout(uzer.getApiContext());
                    log.info(new StringBuilder(100).append("user ").append(uzer).append(" logout ok").toString());
                } catch (Exception e) {
                    log.severe(new StringBuilder(100).append("user ").append(uzer).append(" failed to log out, ").append(e.getMessage()).toString());
                }
            }
        }
        activeUsers.clear();
    }

    @PreDestroy
    public void destroy() {
        log.info("ShedulerActivity() - Destroying");

        logoutActiveUsers();
        activeMarkets.clear();
    }

    // ---------------------------------------------------------------------------------------

    private static final String SESSION_TOKEN = "sessionToken";
    private static final String LOGIN_STATUS = "loginStatus";
    private static final String STATUS = "status";

    private final Random customerRandom = new Random((new Date()).getTime());

    private final ObjectMapper om = new ObjectMapper();

    public void login(APIContext context, String userName,
                      String password) throws Exception {

        String sessionToken = getSessionToken(AccountConstants.APP_KEY,
                userName, password);
        context.setProduct(AccountConstants.APP_KEY);
        context.setToken(sessionToken);
    }

    private String getSessionToken(String appKey, String userName,
                                   String password) throws Exception {
        JsonNode jsonNode = null;
        String sessionToken = null;
        String sessionResponse = null;

        if ((sessionResponse = HttpClientSSO.getSessionTokenResponse(appKey,
                userName, password)) != null) {
            jsonNode = om.readTree(sessionResponse);

            if (!"SUCCESS".equals(jsonNode.get(LOGIN_STATUS).textValue())) {
                throw new IllegalArgumentException("Failed to log in: "
                        + jsonNode.get(LOGIN_STATUS));
            }

            if (jsonNode.get(SESSION_TOKEN) != null)
                sessionToken = jsonNode.get(SESSION_TOKEN).textValue();

            log.fine("Session token:" + sessionToken);
        } else {
            log.severe("Getting null session token from BetFair");
        }

        return sessionToken;
    }
    /*
        @EJB
    ShedulerActivity serviceBean;

     */

    //private ApiNgOperations jsonOperations = ApiNgJsonRpcOperations.getInstance();

    // Get the active event types within the system (on both exchanges)

    public  List<EventTypeResult> getActiveEventTypes(APIContext context)  {
        MarketFilter marketFilter = new MarketFilter();
        List<EventTypeResult> r = null;

        try {
         r = jsonOperations.listEventTypes(marketFilter, MarketSort.FIRST_TO_START, context.getProduct(), context.getToken());
    } catch (APINGException e) {
        log.log(Level.SEVERE, "error getting listCompetitions ", e);
    }

        return r;
    }

    public List<CompetitionResult> getCompetitions(APIContext context,  Set<String> eventTypeIds, Set<String> eventIds) {

        MarketFilter marketFilter = new MarketFilter();
        Set<String> _eventTypeIds = new HashSet<String>();

        if (eventTypeIds != null && eventTypeIds.size() > 0)
            _eventTypeIds.addAll(eventTypeIds);

        marketFilter.setEventTypeIds(_eventTypeIds);

        Set<String> _eventIds = new HashSet<String>();

        if (eventIds != null && eventIds.size() > 0)
            _eventIds.addAll(eventIds);

        marketFilter.setEventIds(_eventIds);
        TimeRange timeRange = new TimeRange();

        timeRange.setFrom(DTAction.getTimeFormBegin(new Date()));
        timeRange.setTo(DTAction.getTimeToEnd(new Date()));

        marketFilter.setMarketStartTime(timeRange);
        marketFilter.setTurnInPlayEnabled(true);
        List<CompetitionResult> r = null;

        try {
         r = jsonOperations.listCompetitions(marketFilter,
                MarketSort.FIRST_TO_START, "500", context.getProduct(),
                context.getToken());

        } catch (APINGException e) {
            log.log(Level.SEVERE, "error getting listCompetitions ", e);
        }

        return r;
    }


    public List<EventResult> getEvents(APIContext context,
                                       Set<String> eventTypeIds,
                                       Set<String> competitionIds,
                                       Set<String> eventIds)
            {

        MarketFilter marketFilter = new MarketFilter();
        Set<String> _eventTypeIds = new HashSet<String>();
        Set<String> _competitionIds = new HashSet<String>();
        Set<String> _eventIds = new HashSet<String>();

        if (eventTypeIds != null && eventTypeIds.size() > 0)
            _eventTypeIds.addAll(eventTypeIds);

        if (competitionIds != null && competitionIds.size() > 0)
            _competitionIds.addAll(competitionIds);

        if (eventIds != null && eventIds.size() > 0)
            _eventIds.addAll(eventIds);

        marketFilter.setEventTypeIds(_eventTypeIds);

        marketFilter.setCompetitionIds(_competitionIds);

        marketFilter.setEventIds(_eventIds);

        Set<String> typesCode = new HashSet<String>();
        typesCode.add("WIN");
        typesCode.add("MATCH_ODDS");
        marketFilter.setMarketTypeCodes(typesCode);

        TimeRange timeRange = new TimeRange();

        Calendar c = Calendar.getInstance();

        timeRange.setFrom(DTAction.getTimeFormBegin(c.getTime()));

        c.add(Calendar.DAY_OF_YEAR, 7);

        timeRange.setTo(DTAction.getTimeToEnd(c.getTime()));

        marketFilter.setMarketStartTime(timeRange);

        marketFilter.setTurnInPlayEnabled(true);
        List<EventResult> r = null;

    try {
         r = jsonOperations.listEvents(marketFilter,
                MarketSort.FIRST_TO_START, "500", context.getProduct(),
                context.getToken());
    } catch (APINGException e) {
        log.log(Level.SEVERE, "error getting listEvents ", e);
    }

        return r;
    }


    public List<MarketCatalogue> getMarkets(APIContext context,
                                            Set<String> eventIds, Set<String> marketIds)

    {
        MarketFilter marketFilter = new MarketFilter();

        Set<MarketProjection> marketProjection = new HashSet<MarketProjection>();
        Set<String> _eventIds = new HashSet<String>();

        if (eventIds != null && eventIds.size() > 0) {
            _eventIds.addAll(eventIds);
            marketFilter.setEventIds(_eventIds);
            //marketProjection.add(MarketProjection.COMPETITION);
            marketProjection.add(MarketProjection.EVENT);
            marketProjection.add(MarketProjection.MARKET_DESCRIPTION);
        }

        Set<String> _marketIds = new HashSet<String>();

        Set<String> typesCode = new HashSet<String>();
        typesCode.add("WIN");
        typesCode.add("MATCH_ODDS");

        if (marketIds != null && marketIds.size() > 0) {
            _marketIds.addAll(marketIds);
            marketFilter.setMarketIds(_marketIds);


//			marketProjection.add(MarketProjection.COMPETITION);
            marketProjection.add(MarketProjection.EVENT);
            marketProjection.add(MarketProjection.MARKET_DESCRIPTION);
            marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);
            marketProjection.add(MarketProjection.RUNNER_METADATA);

        }

        marketProjection.add(MarketProjection.MARKET_START_TIME);

        TimeRange timeRange = new TimeRange();
        Calendar c = Calendar.getInstance();
        timeRange.setFrom(DTAction.getTimeFormBegin(c.getTime()));

        c.add(Calendar.DAY_OF_YEAR, 7);
        timeRange.setTo(DTAction.getTimeToEnd(c.getTime()));

        marketFilter.setMarketStartTime(timeRange);
        marketFilter.setTurnInPlayEnabled(true);
        marketFilter.setMarketTypeCodes(typesCode);

        log.info("use marketFilter: " + marketFilter);
        List<MarketCatalogue> r=null;
        try{

         r = jsonOperations.listMarketCatalogue(
                marketFilter, marketProjection, MarketSort.FIRST_TO_START,
                "500", context.getProduct(), context.getToken());
    } catch (APINGException e) {
        log.log(Level.SEVERE, "error getting listMarketCatalogue ", e);
    }

        return r;
    }


    public List<MarketBook> getMarketPrices(APIContext context, String marketId, String currencyCode) {

        List<String> marketIds = new ArrayList<String>();
        marketIds.add(marketId);

        PriceProjection priceProjection = new PriceProjection();
        Set<PriceData> priceData = new HashSet<PriceData>();

        priceData.add(PriceData.EX_BEST_OFFERS);

        priceData.add(PriceData.EX_TRADED);

        priceProjection.setPriceData(priceData);

        Set<OrderProjection> orderProjection = new HashSet<OrderProjection>();
        orderProjection.add(OrderProjection.EXECUTABLE);

        Set<MatchProjection> matchProjection = new HashSet<MatchProjection>();
        matchProjection.add(MatchProjection.NO_ROLLUP);

        return listMarketBook(context, marketIds, priceProjection, OrderProjection.EXECUTABLE, MatchProjection.ROLLED_UP_BY_PRICE, currencyCode);

    }

    public List<MarketBook> getMarketStatus(APIContext context, String marketId) {

        List<String> marketIds = new ArrayList<String>();
        marketIds.add(marketId);

        return listMarketBook(context, marketIds, null, null, null, null);
    }


    public List<MarketBook> listMarketBook(APIContext context, List<String> marketIds,
                                           PriceProjection priceProjection, OrderProjection orderProjection,
                                           MatchProjection matchProjection, String currencyCode) {

        List<MarketBook> result = null;
        try {
            result = jsonOperations.listMarketBook(marketIds, priceProjection, orderProjection, matchProjection, currencyCode, context.getProduct(), context.getToken());
        } catch (APINGException e) {
            log.log(Level.SEVERE, "error getting marketBook ", e);
        }
        return result;
    }

    public List<MarketProfitAndLoss> listMarketProfitAndLoss (APIContext context, Set<String> marketIds) {

        List<MarketProfitAndLoss> result = null;
        try {

            result = jsonOperations.listMarketProfitAndLoss( marketIds, false, false, true,
                    context.getProduct(), context.getToken());

        } catch (APINGException e) {
            log.log(Level.SEVERE, "error getting market profit and loss ", e);
        }
        return result;
    }

    public List<CurrentOrderSummary> listCurrentOrders(APIContext context,
                                                       Set<String> betIds, Set<String> marketIds) {

        List<CurrentOrderSummary> result = null;
        OrderProjection orderProjection = OrderProjection.ALL;
        try {

            CurrentOrderSummaryReport res = jsonOperations.listCurrentOrders(betIds, marketIds, orderProjection, 1000, context.getProduct(), context.getToken());
            result = (res != null ? res.getCurrentOrders(): null);

        } catch (APINGException e) {
            log.log(Level.SEVERE, "error getting marketBook ", e);
        }
        return result;
    }

    public PlaceExecutionReport placeOrders(APIContext context, String marketId,
                                            List<PlaceInstruction> instructions) {

        PlaceExecutionReport result = null;
        try {
            String _customRef = String.valueOf(customerRandom.nextLong());
            log.log(Level.FINE, "_customRef: "+ _customRef);
            result = jsonOperations.placeOrders(marketId, instructions, _customRef,
                    context.getProduct(), context.getToken());
        } catch (APINGException e) {
            log.log(Level.SEVERE, "error placing orders ", e);
        }
        return result;
    }

    public ReplaceExecutionReport replaceOrders(APIContext context, String marketId,
                                                List<ReplaceInstruction> instructions ) {

        ReplaceExecutionReport result = null;
        try {
            result = jsonOperations.replaceOrders(marketId, instructions, String.valueOf(customerRandom.nextLong()),
                    context.getProduct(), context.getToken());
        } catch (APINGException e) {
            log.log(Level.SEVERE, "error replacing orders ", e);
        }
        return result;
    }

    public CancelExecutionReport cancelOrders(APIContext context, String marketId, List<CancelInstruction> instructions) {

        CancelExecutionReport result = null;
        try {
            result = jsonOperations.cancelOrders(marketId, instructions, String.valueOf(customerRandom.nextLong()),
                    context.getProduct(), context.getToken());
        } catch (APINGException e) {
            log.log(Level.SEVERE, "error on cancel orders ", e);
        }
        return result;
    }


    // @TODO Implement Keep alive for Italian jurisdiction

    public void keepAlive(APIContext context) throws Exception {
        JsonNode jsonNode = null;
        // International jurisdictions:
        // https://identitysso.betfair.com/api/keepAlive

        // Italian jurisdiction:
        // https://identitysso.betfair.it/api/keepAlive

        String sessionResponse = null;

        if ((sessionResponse = jsonOperations.keepAlive(context.getProduct(),
                context.getToken())) != null) {
            jsonNode = om.readTree(sessionResponse);

            if (!"SUCCESS".equals(jsonNode.get(STATUS).textValue())) {
                throw new IllegalArgumentException("Keep-alive request failed!");
            }
        }
    }

    public void logout(APIContext context) throws Exception {
        // https://identitysso.betfair.com/api/logout
        JsonNode jsonNode = null;
        String sessionResponse = null;

        if ((sessionResponse = jsonOperations.logout(context.getProduct(),
                context.getToken())) != null) {
            jsonNode = om.readTree(sessionResponse);

            if (!"SUCCESS".equals(jsonNode.get(STATUS).textValue())) {
                throw new IllegalArgumentException("Logout request failed!");
            }
        }
    }

// --------------------------------------------------------------------------------------------------


}
