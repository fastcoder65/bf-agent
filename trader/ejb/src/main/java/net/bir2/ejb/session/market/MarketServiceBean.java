package net.bir2.ejb.session.market;

import com.betfair.aping.api.ApiNgJsonRpcOperations;
import com.betfair.aping.entities.*;
import com.betfair.aping.enums.MatchProjection;
import com.betfair.aping.enums.OrderProjection;
import com.betfair.aping.enums.Wallet;
import com.betfair.aping.exceptions.AccountAPINGException;
import net.bir2.ejb.action.ShedulerActivity;
import net.bir2.multitrade.ejb.entity.*;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.util.APIContext;
import org.hibernate.Hibernate;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Session Bean implementation class MarketServiceBean
 */
@Stateless
@Local({MarketService.class})
public class MarketServiceBean implements MarketService {

    private static final Logger log = Logger.getLogger(MarketServiceBean.class.getName());

    /*
     * private final String DEC_QRY =
     * " SELECT  aes_decrypt( unhex(exLogin), :key) decl, " +
     * " aes_decrypt( unhex(exPassword), concat(:key, aes_decrypt( unhex(exLogin), :key ))) decp "
     * + " FROM `Uzer` WHERE  login = :login ";
     */
    @SuppressWarnings("unused")
    private static final String DEC_QRY = " SELECT  exLogin decl, "
            + " exPassword decp " + " FROM Uzer WHERE  login = :login ";

    // private final String hexNumbersAntiPattern = "[^[:xdigit:]]+";
    // private final String CHECK_ENCODED_QRY =
    // " select cast( exLogin regexp :hexNum as CHAR) encl, cast( exPassword regexp :hexNum as CHAR) encp from user WHERE  login = :login ";

    @PersistenceContext
    protected EntityManager em;

    @Resource
    private EJBContext context;


    @EJB
    ShedulerActivity serviceBean;

    @EJB
    private ApiNgJsonRpcOperations jsonOperations;

    public ShedulerActivity getServiceBean() {
        return serviceBean;
    }

    public EJBContext getContext() {
        return context;
    }

    public MarketServiceBean() {
    }

    public String getLoginName() {
        return context.getCallerPrincipal().getName();
    }


    public AccountFundsResponse  getAccountFunds  (Wallet wallet, String appKey, String ssoId ) {
        AccountFundsResponse result = null;
        try {
            result = jsonOperations.getAccountFunds( wallet, appKey, ssoId );
        } catch (AccountAPINGException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Uzer getCurrentUser() {
        return getUserByLogin(getLoginName());
    }

    public Uzer getUserByLogin(String userLogin) {
        Uzer result;

        result = (Uzer) em.createNamedQuery("UserByLogin")
                .setParameter("login", userLogin)
                .getSingleResult();

        if (result != null) {
            result.setExLoginDec(result.getExLogin());
            result.setExPasswordDec(result.getExPassword());
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    public List<Market> listMarkets() {
        return em.createQuery(
                "SELECT m FROM Market m where m.marketStatus <> :marketStatus")
                .setParameter("marketStatus", "CLOSED")
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Market> getMyActiveMarkets(int toffset) {
        log.info("enter getMyActiveMarkets( " + toffset + " )");

        List<Market> result = new ArrayList<Market>();

        List<Market> _result = (List<Market>) em.createNamedQuery("MyActiveMarkets")
                .setParameter("login", getLoginName())
                .setParameter("marketStatus", "CLOSED")
                .getResultList();
/*
        for (Market market: _result) {

            Date marketTime =  market.getMarketTime();
            Calendar c = Calendar.getInstance();
            c.setTime(marketTime);
            c.add(Calendar.MILLISECOND, toffset);
            market.setMarketTime(c.getTime());
            result.add(market);
      }
*/
        return _result;
    }

    @SuppressWarnings("unchecked")
    public List<Uzer> getActiveUsers() {
        List<Uzer> result = new ArrayList<Uzer>(10);

        List<Uzer> _list = (List<Uzer>) em.createNamedQuery("ActiveUsers")
                .getResultList();

        for (Uzer uzer : _list) {
            result.add(getUserByLogin(uzer.getLogin()));
        }
        return result;
    }


    public List<MarketRunner> listRunners(String marketId) {
        List<MarketRunner> result =
                em.createQuery("select r FROM MarketRunner r where r.market.marketId = :marketId ")
                        .setParameter("marketId", marketId)
                        .getResultList();
        log.fine(result.size() + " MarketRunner(s) loaded.");
        for (MarketRunner runner : result) {
            runner.prefetchAll();
        }
        return result;
    }

    public Feed4Runner4User merge(Feed4Runner4User arg) {
        return em.merge(arg);
    }

    public Feed4Market4User merge(Feed4Market4User arg) {
        return em.merge(arg);
    }

    public Market merge(Market market) {
        return em.merge(market);
    }

    public Market getMarket(long id) {
        Market result = em.find(Market.class, id);
        if (result != null) {
            Hibernate.initialize(result.getRunners());
            log.fine("getMarket() - result.getRunners().size()= " + result.getRunners().size());
        }
        return result;
    }

    public Market getMarketByMarketId(String marketId) {
        Market result = (Market) em.createNamedQuery("MarketByMarketId")
                .setParameter("marketId", marketId)
                .getSingleResult();
        result.prefetchAll();
        return result;
    }

    public boolean isMarketAlreadyExistsByMarketId(String marketId) {
        Long count = 0L;
        count = (Long) em.createNamedQuery("MarketCountByMarketId")
                .setParameter("marketId", marketId).getSingleResult();
        //	log.info("isMarketAlreadyExistsByMarketId - count:" + count);
        return (count > 0);
    }

    // "MarketByMarketId"

    public MarketRunner merge(MarketRunner runner) {
        return em.merge(runner);
    }

    public void persist(MarketRunner runner) {
         em.persist(runner);
    }

    public MarketRunner getRunner(long id) {
        return em.find(MarketRunner.class, id);
    }

    public MarketRunner getRunnerBySelectionId(long marketId, long selectionId) {
        return (MarketRunner) em.createQuery("select r FROM MarketRunner r where r.market.id = :marketId and r.selectionId = :selectionId")
                .setParameter("marketId", marketId).setParameter("selectionId",
                        selectionId).getSingleResult();
    }

    public void remove(Market market) {
        Uzer _currentUser = getCurrentUser();
        remove(market, _currentUser);
    }

    public void remove(Market market, Uzer currentUser) {

        Market marketRef = em.find(Market.class, market.getId());
        String marketRefName = String.valueOf(marketRef);
        Set<MarketRunner> runners = marketRef.getRunners();
        Set<Market4User> market4users = marketRef.getMarket4Users();
        Set<Feed4Market4User> feed4Market4Users = marketRef.getFeed4Market4Users();

        log.info("start removing market " + marketRefName + "runners found: " + marketRef.getRunners().size());

        int i_r =0;
/*
        for (MarketRunner runner : runners) {
            i_r++;
            log.info(i_r + ") delete runner: " + runner.toString());
*/
/*
            Set<Runner4User> runner4users = runner.getRunner4Users();
            int i_r4u = 0;

            for (Runner4User r4u : runner4users) {
                i_r4u++;
                if (r4u.getUserId() == currentUser.getId()) {
                    log.info(i_r4u+ ")) delete Runner4User: " + r4u.toString());
                    em.remove(r4u);
                }
            }
*/
       //     em.flush();

/*
            Set<Feed4Runner4User> feed4Runner4Users = runner.getFeed4Runner4Users();
            for (Feed4Runner4User r4u : feed4Runner4Users) {
                if (r4u.getUserId() == currentUser.getId()) {
                    em.remove(r4u);
                }
            }
*/

//         if (runner4users.size() == 1) {
//              em.remove(runner);
//              log.info("runner: " + runner + " removed.");
//         }
//        }
/*
        int i_m4u = 0;

        for (Market4User m4u : market4users) {
            i_m4u++;
            if (m4u.getUserId() == currentUser.getId()) {
                log.info(i_m4u+ ")) delete Runner4User: " + m4u.toString());
                em.remove(m4u);
            }
        }
*/
//        if (market4users.size() == 1) {
            //		ShedulerActivity serviceBean = ShedulerActivityBean.getInstance();
            serviceBean.getActiveMarkets().remove(marketRef.getMarketId());
            em.remove(marketRef);
            log.info("market " + marketRefName + " removed.");
//        }

        em.flush();
    }

    public void remove(MarketRunner runner) {
        em.remove(runner);
    }

    public void removeAllActiveMarkets() {
        for (Market market : listMarkets()) {
            remove(market);
        }
    }

    public Runner4User merge(Runner4User u4r) {
        return em.merge(u4r);
    }

    public void persist(Market4User market4User) {
        em.persist(market4User);
    }

    public Market4User merge(Market4User market4User) {
        return em.merge(market4User);
    }

    public Uzer merge(Uzer uzer) {

        org.hibernate.Session session = (org.hibernate.Session) em.getDelegate();
        org.hibernate.Query query = session
                .createSQLQuery("update uzer set exLogin = :login," +
                        " exPassword=:pass where id=:id")
                .setParameter("login", uzer.getExLogin())
                .setParameter("pass", uzer.getExPassword())
                .setParameter("id", uzer.getId());
        query.executeUpdate();

        /*
        org.hibernate.Query query = session
                .createSQLQuery("update user set exLogin=hex(aes_encrypt(?,?)), exPassword=hex( aes_encrypt(?,?)) where id=?");

           query.setParameter(1, uzer.getExLoginDec())
                .setParameter(1, getPassKey())
                .setParameter(2, uzer.getExPasswordDec())
                .setParameter(3, getPassKey() + uzer.getExLoginDec())
                .setParameter(4, uzer.getId()).executeUpdate();
*/
       // return getUserByLogin(uzer.getLogin());
        return  em.merge(uzer);
    }

    // GlobalAPI

    public List<EventTypeResult> getActiveEventTypes(APIContext context) {
        return serviceBean.getActiveEventTypes(context);
    }

    public List<CompetitionResult> getCompetitions(APIContext context,  Set<String> eventTypeIds, Set<String> eventIds) {
        return serviceBean.getCompetitions( context, eventTypeIds, eventIds);
    }

    public List<EventResult> getEvents(APIContext context,
                                Set<String> eventTypeIds,
                                Set<String> competitionIds,
                                Set<String> eventIds) {

        return serviceBean.getEvents( context,  eventTypeIds,  competitionIds,  eventIds);

    }

    public List<MarketCatalogue> getMarkets(APIContext context, Set<String> eventIds, Set<String> marketIds) {
        return serviceBean.getMarkets( context, eventIds, marketIds);
    }


    public List<MarketBook> getMarketPrices(APIContext context, String marketId, String currencyCode) {
        return serviceBean.getMarketPrices(context,  marketId, currencyCode);
    }

    public List<MarketBook> getMarketStatus(APIContext context, String marketId) {
        return serviceBean.getMarketStatus ( context, marketId);
    }

    public List<MarketBook> listMarketBook(APIContext context, List<String> marketIds,
                                    PriceProjection priceProjection, OrderProjection orderProjection,
                                    MatchProjection matchProjection, String currencyCode) {

        return serviceBean.listMarketBook( context,  marketIds, priceProjection, orderProjection, matchProjection, currencyCode);
    }

    public List<MarketProfitAndLoss> listMarketProfitAndLoss (APIContext context, Set<String> marketIds) {
        return serviceBean.listMarketProfitAndLoss ( context,  marketIds);
    }


    public List<CurrentOrderSummary> listCurrentOrders(APIContext context, Set<String> betIds, Set<String> marketIds) {
        return serviceBean.listCurrentOrders( context,  betIds,  marketIds);
    }

    public PlaceExecutionReport placeOrders(APIContext context, String marketId, List<PlaceInstruction> instructions) {
        return serviceBean.placeOrders ( context, marketId, instructions);
    }

    public ReplaceExecutionReport replaceOrders(APIContext context, String marketId,  List<ReplaceInstruction> instructions ) {
        return serviceBean.replaceOrders(context, marketId, instructions );
    }

    public CancelExecutionReport cancelOrders(APIContext context, String marketId, List<CancelInstruction> instructions) {
        return serviceBean.cancelOrders(context, marketId, instructions);
    }

    public void keepAlive(APIContext context) throws Exception {
        serviceBean.keepAlive(context);
    }

    public void login(APIContext context, String userName,	String password) throws Exception {
        serviceBean.login( context, userName, password );
    }

    public void logout(APIContext context) throws Exception {
        serviceBean.logout(context);
    }

}
