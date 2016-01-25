package net.bir2.ejb.session.market;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.bir2.ejb.action.ShedulerActivity;
import net.bir2.multitrade.ejb.entity.*;
import net.bir2.multitrade.ejb.entity.MarketRunner;
import org.hibernate.Hibernate;

// import org.apache.log4j.Logger;

/**
 * Session Bean implementation class MarketServiceBean
 */
@Stateless
@Local( { MarketService.class })
public class MarketServiceBean implements MarketService {
	
	@Inject
    private Logger log;

//	protected final Logger log = Logger.getLogger(this.getClass());

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
	
	
	public EJBContext getContext() {
		return context;
	}

	public MarketServiceBean() {
	}

	public String getLoginName() {
		return context.getCallerPrincipal().getName();
	}

	/*
	 * @SuppressWarnings("unchecked") private boolean credEncoded(String _login)
	 * { org.hibernate.Session session = (org.hibernate.Session)
	 * em.getDelegate(); // String _login = getLoginName(); //System.out.println
	 * ("_login: " + _login); List<Object[]> _list =
	 * session.createSQLQuery(CHECK_ENCODED_QRY) .addScalar("encl",
	 * Hibernate.STRING) .addScalar("encp", Hibernate.STRING)
	 * .setParameter("hexNum", hexNumbersAntiPattern)
	 * 
	 * .setParameter("login", _login) .list(); Object[] flags = (_list != null
	 * && _list.size() > 0)? (Object[]) _list.get(0): null; boolean result =
	 * (flags != null && "0".equals(flags[0]) && "0".equals(flags[1])); if
	 * (flags != null){ //System.out.println ("flags[0]='" +flags[0]+
	 * "', flags[1]='" +flags[1]+ "', credEncoded()=" + result); } else { //
	 * System.out.println ("flags=" + flags); } return result ; }
	 */

	public Uzer getCurrentUser() {
		return getUserByLogin(getLoginName());
	}

	public Uzer getUserByLogin(String userLogin) {
		Uzer result;

		result = (Uzer) em.createNamedQuery("UserByLogin")
				.setParameter("login", userLogin)
				.getSingleResult();
		

		/*
		 * org.hibernate.Session session = (org.hibernate.Session)
		 * em.getDelegate(); Object[] decodedLoginPass = (Object[])
		 * session.createSQLQuery(DEC_QRY) .addScalar("decl", Hibernate.STRING)
		 * .addScalar("decp", Hibernate.STRING) .setParameter("login",
		 * userLogin).uniqueResult();
		 * 
		 * // result.setExLoginDec(decodedLoginPass[0].toString());
		 * result.setExPasswordDec(decodedLoginPass[1].toString());
		 */

		if (result != null) {
			result.setExLoginDec(result.getExLogin());
			result.setExPasswordDec(result.getExPassword());
		}

		// log.info("getUserByLogin - result: " + result);
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
	public List<Market> getMyActiveMarkets() {
		return (List<Market>) em.createNamedQuery("MyActiveMarkets")
				.setParameter("login", getLoginName()).setParameter(
						"marketStatus", "CLOSED").getResultList();
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
		List<MarketRunner> result = em.createQuery(

				"select r FROM MarketRunner r where r.market.marketId = :marketId "
		)
				.setParameter("marketId", marketId).getResultList();

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
		Market result =  em.find(Market.class, id);
		if (result != null) {
			Hibernate.initialize(result.getRunners());
			log.info("getMarket() - result.getRunners().size()= " + result.getRunners().size());
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
		long count = (Long) em.createNamedQuery("MarketCountByMarketId")
				.setParameter("marketId", marketId).getSingleResult();
	//	log.info("isMarketAlreadyExistsByMarketId - count:" + count);
		return (count > 0);
	}

	// "MarketByMarketId"

	public MarketRunner merge(MarketRunner runner) {
		return em.merge(runner);
	}

	public MarketRunner getRunner(long id) {
		return em.find(MarketRunner.class, id);
	}

	public MarketRunner getRunnerBySelectionId(long marketId, long selectionId) {
		return (MarketRunner) em.createNamedQuery("RunnerBySelectionId")
				.setParameter("marketId", marketId).setParameter("selectionId",
						selectionId).getSingleResult();
	}

	public void remove(Market market) {
		Uzer _currentUser = getCurrentUser();
		remove(market, _currentUser);
	}

	public void remove(Market market, Uzer currentUser) {

		Market marketRef = em.getReference(Market.class, market.getId());
		String marketRefName = String.valueOf(marketRef);
		System.out.println("start removing market " + marketRefName);

		Set<Feed4Market4User> feed4Market4Users = marketRef.getFeed4Market4Users();

		for (Feed4Market4User m4u : feed4Market4Users) {
			if (m4u.getUserId() == currentUser.getId()) {
				em.remove(m4u);
			}
		}

		Set<Market4User> market4users = marketRef.getMarket4Users();
		for (Market4User m4u : market4users) {
			if (m4u.getUserId() == currentUser.getId()) {
				em.remove(m4u);
			}
		}
		Set<MarketRunner> runners = marketRef.getRunners();
		for (MarketRunner runner : runners) {

			Set<Runner4User> runner4users = runner.getRunner4Users();
			for (Runner4User r4u : runner4users) {
				if (r4u.getUserId() == currentUser.getId()) {
					em.remove(r4u);
				}
			}

			Set<Feed4Runner4User> feed4Runner4Users = runner
					.getFeed4Runner4Users();
			for (Feed4Runner4User r4u : feed4Runner4Users) {
				if (r4u.getUserId() == currentUser.getId()) {
					em.remove(r4u);
				}
			}

			if (runner4users.size() == 1)
				em.remove(runner);
		}

		if (market4users.size() == 1) {
	//		ShedulerActivity serviceBean = ShedulerActivityBean.getInstance();
			serviceBean.getActiveMarkets().remove(marketRef.getMarketId());
			em.remove(marketRef);
		}
		System.out.println("market " + marketRefName + " removed.");
	}

	public void remove(MarketRunner runner) {
		em.remove(runner);
	}

	public void removeAllActiveMarkets() {
		for (Market market : listMarkets()) {
			remove(market);
		}
	}

	/*
	 * public RunnerPrice merge(RunnerPrice price) { return em.merge(price); }
	 */
	public Runner4User merge(Runner4User u4r) {
		return em.merge(u4r);
	}

	public Market4User merge(Market4User market4User) {
		return em.merge(market4User);
	}

	private  String getPassKey() {
		return "passKey";
	}

	public Uzer merge(Uzer uzer) {

		org.hibernate.Session session = (org.hibernate.Session) em
				.getDelegate();
		org.hibernate.Query query = session
				.createSQLQuery("update user set exLogin=hex(aes_encrypt(?,?)), exPassword=hex( aes_encrypt(?,?)) where id=?");

		query.setParameter(1, uzer.getExLoginDec()).setParameter(1,
				getPassKey()).setParameter(2, uzer.getExPasswordDec())
				.setParameter(3, getPassKey() + uzer.getExLoginDec())
				.setParameter(4, uzer.getId()).executeUpdate();

		return getUserByLogin(uzer.getLogin());
	}
}
