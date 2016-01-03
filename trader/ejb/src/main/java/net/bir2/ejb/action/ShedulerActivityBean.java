package net.bir2.ejb.action;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import net.bir2.ejb.session.market.BaseService;
import net.bir2.ejb.session.market.MarketService;
import net.bir2.handler.GlobalAPI;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Market4User;
import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir2.multitrade.util.APIContext;

import java.util.logging.*;

@Singleton
public class ShedulerActivityBean implements ShedulerActivity {

    @EJB
    private BaseService baseService;

    public BaseService getBaseService() {
        return baseService;
    }

    @EJB
    private MarketService marketService;
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

                GlobalAPI.keepAlive(currentUser.getApiContext());

        } catch (Exception e) {
            log.log(Level.SEVERE, " sendKeepAlive error: ", e);
        }

    }

    public boolean add2ActiveUsers(String login, Uzer uzer) {
        APIContext apiContext = new APIContext();
        boolean result = false;
        try {
            GlobalAPI.login(apiContext, uzer.getExLogin(), uzer
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
                        if (!isOnAir) {
                            market4User.setOnAir(true);
                            marketService.merge(market4User);
                        }
                    }
                    log.fine(new StringBuilder(100).append("### do update Market {marketId =").append(_marketId).append(", turnOnTime=").append(turnOnTime.getTime()).append(", turnOffTime=").append(turnOffTime.getTime()).append(" }").toString());
                    sendRequest(Action.UPDATE_MARKET, uzer.getLogin(),
                            market_id);
                    // baseService.sendDelayedRequest(Action.UPDATE_MARKET,
                    // user.getLogin(), market_id, i);
                }
                i++;
            }
        }
    }

    // Lifecycle methods
    public void create() {
        log.fine("ShedulerActivity() - Creating");
        activeMarkets.clear();
        // sendRequest(Action.LOAD_ACTIVE_USERS);
        // sendRequest(Action.LOAD_ACTIVE_MARKETS);
        // sendRequest(Action.UPDATE_MARKET_PRICES);
    }

    private void logoutActiveUsers() {
        for (Uzer uzer : activeUsers.values()) {
            if (uzer.getApiContext() != null) {
                try {
                    GlobalAPI.logout(uzer.getApiContext());
                    log.info(new StringBuilder(100).append("user ").append(uzer).append(" logout ok").toString());
                } catch (Exception e) {
                    log.severe(new StringBuilder(100).append("user ").append(uzer).append(" failed to log out, ").append(e.getMessage()).toString());
                }
            }
        }
        activeUsers.clear();
    }

    /**
     * ���������� ����. ������ ����� ��������� ��� ������������ ���� ���
     * <p/>
     * ������������ JBoss
     */

    public void destroy() {
        log.fine("ShedulerActivity() - Destroying");

        logoutActiveUsers();
        activeMarkets.clear();
    }

    /**
     *
     * ����������� (lookup) �������� ����
     *
     *
     *
     * @return �������
     */
/*
	public static ShedulerActivity getInstance() {
		ShedulerActivity instance;
		try {
			instance = (ShedulerActivity) MBeanProxyExt.create(
					ShedulerActivity.class, "bir:service=ShedulerActivity",
					MBeanServerLocator.locate());
			if (instance == null)
			log.info("ShedulerActivity instance is null!");
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		return instance;  
	}
*/
}
