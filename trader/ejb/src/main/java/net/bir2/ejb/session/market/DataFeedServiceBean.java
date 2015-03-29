package net.bir2.ejb.session.market;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.bir2.multitrade.ejb.entity.DataFeed;
import net.bir2.multitrade.ejb.entity.DataFeedEvent;
import net.bir2.multitrade.ejb.entity.Feed4Market4User;
import net.bir2.multitrade.ejb.entity.Feed4Runner4User;
import net.bir2.multitrade.util.url.URLContentGetterException;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.centrebet.Event;
import com.centrebet.Market;
import com.centrebet.Outcome;
import com.centrebet.Oxip;
import com.centrebet.Type;
import com.unitab.race.FixedOdds;
import com.unitab.race.Race;

@Stateless
@Local( { DataFeedService.class })
public class DataFeedServiceBean {

	/*
	private static final Logger log = Logger
			.getLogger(DataFeedServiceBean.class);
*/
	
	private static final String TRUST_ALL_CERTS = "trustAll";
	private static final int HTTP_CON_TIMEOUT = 20 * 1000; // 10 s - default
	// http connect
	// timeout
	private static final int READ_TIMEOUT = 10 * 1000; // 5 s - default stream
	// read timeout

	private static final String PROP_CON_TIMEOUT = "net.bir.contimeout";
	private static final String PROP_READ_TIMEOUT = "net.bir.readTimeout";

	@Inject
    private Logger log;

	@PersistenceContext
	protected EntityManager em;

	private  void commonExceptionProcess(String errorMessage, Exception e)
			throws URLContentGetterException {
		throw new URLContentGetterException(errorMessage + ", "
				+ e.getMessage());
	}

	private  void throwLocalException(String errorMessage)
			throws URLContentGetterException {
		throw new URLContentGetterException(errorMessage);
	}

	@SuppressWarnings("unchecked")
	public List<DataFeed> listDataFeeds(String sourceName) {
		return em.createNamedQuery("ActiveDataFeedLikeName").setParameter(
				"name", sourceName).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DataFeedEvent> listDataFeedEvents(String sourceName, Date now) {
		return (List<DataFeedEvent>) em.createNamedQuery(
				"ActiveDataFeedsEventByDataFeedName").setParameter("likeName",
				sourceName).setParameter("eventTime", now).getResultList();
	}

	public DataFeedEvent getDataFeedEventByEventId(int eventId) {
		return (DataFeedEvent) em.createNamedQuery("ActiveDataFeedEventById")
				.setParameter("eventId", eventId).getSingleResult();
	}

	public DataFeedEvent getDataFeedEventByEventName(String eventName) {
		return (DataFeedEvent) em.createNamedQuery("ActiveDataFeedEventByName")
				.setParameter("eventName", eventName).getSingleResult();
	}

	public boolean isDataFeedEventAlreadyExistsByEventId(int eventId) {
		long count = (Long) em.createNamedQuery("DataFeedEventCountByEventId")
				.setParameter("eventId", eventId).getSingleResult();
		return (count > 0);
	}

	 boolean isDataFeedEventAlreadyExistsByEventUri(String uri) {
		long count = (Long) em.createNamedQuery("DataFeedEventCountByEventURI")
				.setParameter("uri", uri).getSingleResult();
		return (count > 0);
	}

	private static final String FM_COUNT_QUERY = " select count(*) FROM Feed4Market4User fmu "
			+ " join DataFeedEvent dfe on fmu.dataFeedEvent_id=dfe.id "
			+ " join  DataFeed df on dfe.dataFeed_id=df.id "

			+ " WHERE fmu.user_id = :userId"
			+ " and fmu.market_id = :marketId"
			+ " and df.name like :likeName";

	public boolean isFeed4Market4UserAlreadyExists(String likeName,
			long marketId, int userId) {
		int count;

		HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
		
		Session session = hem.getSession();
		
		org.hibernate.Query query = session.createSQLQuery(FM_COUNT_QUERY)
				.setParameter("userId", userId).setParameter("marketId",
						marketId).setParameter("likeName", likeName);

		count = ((BigInteger) query.uniqueResult()).intValue();
		return (count > 0);
	}

	private static final String FR_COUNT_QUERY = " select count(*) FROM Feed4Runner4User fru "
		+ " join DataFeedEvent dfe on fru.dataFeedEvent_id=dfe.id "
		+ " join  DataFeed df on dfe.dataFeed_id=df.id "

		+ " WHERE fru.user_id = :userId"
		+ " and fru.runner_id = :runnerId"
		+ " and df.name like :likeName";

public boolean isFeed4Runner4UserAlreadyExists(String likeName,
		long runnerId, int userId) {
	int count ;

	org.hibernate.Session session = (Session) em.getDelegate();
	org.hibernate.Query query = session.createSQLQuery(FR_COUNT_QUERY)
			.setParameter("userId", userId).setParameter("runnerId",
					runnerId).setParameter("likeName", likeName);

	count = ((BigInteger) query.uniqueResult()).intValue();
	return (count > 0);
}
	
	@SuppressWarnings("unchecked")
	public Feed4Market4User getFeed4Market4User(String likeName, long marketId,
			int userId) {

		Feed4Market4User result = null;
		try {
			List<Feed4Market4User> list = em.createNamedQuery(
					"GetFeed4Market4User").setParameter("likeName", likeName)
					.setParameter("userId", userId).setParameter("marketId",
							marketId).getResultList();

			Iterator<Feed4Market4User> i = (list != null ? list.iterator()
					: null);
			result = (i != null && i.hasNext()) ? i.next()
					: null;
		} catch (Exception e) {
			log.severe("getFeed4Market4User: " + e.getClass().getName());
		}
		return result;
	}

/*	private static final String FR_QUERY = " select fmu.* FROM Feed4Runner4User fmu "
			+ "	join DataFeedEvent dfe on fmu.dataFeedEvent_id=dfe.id "
			+ "	join  DataFeed df on dfe.dataFeed_id=df.id "
			+ " WHERE fmu.user_id = :userId"
			+ " and fmu.runner_id = :runnerId"
			+ " and df.name like :likeName";
*/
	public Feed4Runner4User getFeed4Runner4User(String likeName, long runnerId,
			int userId) {

		return (Feed4Runner4User) em.createNamedQuery("GetFeed4Runner4User")
				.setParameter("likeName", likeName).setParameter("userId",
						userId).setParameter("runnerId", runnerId)
				.getSingleResult();
	}

	DataFeedEvent getDataFeedEventByEventUri(String uri) {
		return (DataFeedEvent) em.createNamedQuery("ActiveDataFeedEventByURI")
				.setParameter("uri", uri).getSingleResult();
	}

	// "DataFeedEventCountByEventName"

	public void readActiveDataFeeds() {
		for (DataFeed dataFeed : listDataFeeds("UNITAB%")) {
			readUnitabDataFeed(dataFeed);
		}

		for (DataFeed dataFeed : listDataFeeds("CENTREBET%")) {
			readCentrebetDataFeed(dataFeed);
		}

	}

	private void readUnitabDataFeed(DataFeed dataFeed) {
		String sXmlFeedData = null;
		log.info("read 'All Races' xmlFeed from: " + dataFeed.getUri());
		try {
			sXmlFeedData = getURLContentAsString(dataFeed.getUri());
		} catch (URLContentGetterException e) {
			log.severe("readUnitabDataFeed: " + e.getMessage());
		}

		if (sXmlFeedData == null) {
			log.info("XmlFeedData is not read.");
			return;
		}

		IBindingFactory bFactAllRace;
		IUnmarshallingContext uctx;
	

		com.unitab.allraces.AllRaceDays allRaceDays = null;

		try {
			bFactAllRace = BindingDirectory
					.getFactory(com.unitab.allraces.AllRaceDays.class);
			uctx = bFactAllRace.createUnmarshallingContext();
			allRaceDays = (com.unitab.allraces.AllRaceDays) uctx
					.unmarshalDocument(new StringReader(sXmlFeedData), null);

		} catch (JiBXException e) {
			log.severe("Unmarshall error: " + e.getMessage());

		}

		if (allRaceDays == null) {
			log.info("Object 'allRaceDays' is null.");
			return;
		}

		List<com.unitab.allraces.RaceDay> raceDayList = allRaceDays
				.getRaceDayList();
		if (raceDayList == null) {
			log.info("Object 'raceDayList' is null.");
			return;
		}

		int eventAddedCount = 0;
		int eventChangedCount = 0;
		for (com.unitab.allraces.RaceDay raceDay : raceDayList) {
			List<com.unitab.allraces.Meeting> meetings = raceDay
					.getMeetingList();
			if (meetings == null)
				continue;
			for (com.unitab.allraces.Meeting meeting : meetings) {
				String mCode = meeting.getMeetingCode();
				List<com.unitab.allraces.Race> raceList = meeting.getRaceList();
				if (raceList == null)
					continue;
				for (com.unitab.allraces.Race race : raceList) {
					Byte raceNo = race.getRaceNo();

					Calendar c = Calendar.getInstance();
					Date raceTime = race.getRaceTime();
					c.setTime(raceTime);
					String uriFeedEvent = new StringBuilder(100).append("http://unitab.com/data/racing/").append(c.get(Calendar.YEAR)).append('/').append(c.get(Calendar.MONTH) + 1).append('/').append(c.get(Calendar.DAY_OF_MONTH)).append('/').append(mCode).append(raceNo).append(".xml").toString();
					/*
					 * System.out.println("race =" + mCode + raceNo +
					 * ", raceTime=" + raceTime + ", uri=" + uriFeedEvent);
					 */
					if (!isDataFeedEventAlreadyExistsByEventUri(uriFeedEvent)) {
						DataFeedEvent dfe = new DataFeedEvent();
						dfe.setEventId(0);
						// dfe.se1tName(uriFeedEvent);
						dfe.setEventTime(raceTime);
						dfe.setUri(uriFeedEvent);
						dfe.setDataFeed(dataFeed);
						em.merge(dfe);
						eventAddedCount++;
					}

					eventChangedCount += readUnitabDataFeedEvent(uriFeedEvent);

				}
			}
		}
		log.info("dataFeed \"" + dataFeed.getName() + "\" : " + eventAddedCount
				+ " events added, " + eventChangedCount + " events changed.");
	}

	public DataFeedEvent findUnitabDataFeedEvent(Set<String> runnerNames) {
		DataFeedEvent result = null;
		Date now = Calendar.getInstance().getTime();
		boolean found = false;
/*		System.out.print("\n");
		for (String sk : runnerNames) {
			System.out.print("\t" + sk);
		}
		System.out.println();
*/
		int countMatched = 0;
		@SuppressWarnings("unused")
		int index = 0;
		for (DataFeedEvent dataFeedEvent : listDataFeedEvents("UNITAB%", now)) {
			if (dataFeedEvent.getEventContent() != null) {
				Race race = getUnitabRace(dataFeedEvent);
			//	System.out.println("race :" + race);
				if (race == null)
					continue;
				List<com.unitab.race.Runner> runners = race.getRunnerList();
				if (runners != null) {
					for (com.unitab.race.Runner runner : runners) {
						for (String sRunnerName : runnerNames) {
							String _sRunnerName = sRunnerName.toUpperCase();
							if (_sRunnerName.contains(runner.getRunnerName()
									.toUpperCase())) {
								countMatched++;
							}
						}
					}
				}
			}
			if (countMatched > Math.round(0.75 * runnerNames.size())) {
				found = true;
				result = dataFeedEvent;
				break;
			}
			index++;
		}
	//	System.out.println("searched in " + index + " unitab races");
		return found ? result : null;
	}

	public DataFeedEvent getDataFeedEvent(long id) {
		return em.find(DataFeedEvent.class, id);
	}

	private  Race getUnitabRace(DataFeedEvent dataFeedEvent) {
		IBindingFactory bFactRace;
		IUnmarshallingContext uctx;
		Race race = null;

		try {
			bFactRace = BindingDirectory.getFactory(Race.class);
			uctx = bFactRace.createUnmarshallingContext();
			race = (com.unitab.race.Race) uctx.unmarshalDocument(
					new StringReader(dataFeedEvent.getEventContent()), null);

		} catch (JiBXException e) {
			log.severe("Unmarshall error: " + e.getMessage());
		}
		return race;
	}

	public Race getFreshedUnitabRace(DataFeedEvent dataFeedEvent) {

		String sXmlFeedData = null;
		log.fine("read 'race' xmlFeed from: " + dataFeedEvent.getUri());
		try {
			sXmlFeedData = getURLContentAsString(dataFeedEvent.getUri());
		} catch (URLContentGetterException e) {
			log.severe("readUnitabDataFeedEvent: " + e.getMessage());
		}

		if (sXmlFeedData == null) {
			log.fine("XmlFeedData is not read.");
			return null;
		}

		IBindingFactory bFactRace;
		IUnmarshallingContext uctx;
		com.unitab.race.RaceDay raceDay = null;

		try {
			bFactRace = BindingDirectory
					.getFactory(com.unitab.race.RaceDay.class);
			uctx = bFactRace.createUnmarshallingContext();
			raceDay = (com.unitab.race.RaceDay) uctx.unmarshalDocument(
					new StringReader(sXmlFeedData), null);

		} catch (JiBXException e) {
			log.severe("Unmarshall error: " + e.getMessage());

		}

		if (raceDay == null) {
			log.info("getFreshedUnitabRace: Object 'raceDay' is null.");
			return null;
		}

        /*		String raceName = race.getRaceName() + ", " + race.getRaceTime() + ", "
                        + race.getDistance();
                //log.info("check " + raceName + " for match runners..");
        */
		return raceDay.getMeeting().getRace();
	}

	private int readUnitabDataFeedEvent(String uri) {

		String sXmlFeedData = null;
		log.fine("read 'race' xmlFeed from: " + uri);
		try {
			sXmlFeedData = getURLContentAsString(uri);
		} catch (URLContentGetterException e) {
			log.severe("readUnitabDataFeedEvent: " + e.getMessage());
		}

		if (sXmlFeedData == null) {
			log.fine("XmlFeedData is not read.");
			return 0;
		}

		IBindingFactory bFactRace = null;
		IUnmarshallingContext uctx;
		IMarshallingContext mctx;
		com.unitab.race.RaceDay raceDay = null;

		try {
			bFactRace = BindingDirectory
					.getFactory(com.unitab.race.RaceDay.class);
			uctx = bFactRace.createUnmarshallingContext();
			raceDay = (com.unitab.race.RaceDay) uctx.unmarshalDocument(
					new StringReader(sXmlFeedData), null);

		} catch (JiBXException e) {
			log.severe("Unmarshall error: " + e.getMessage());

		}

		if (raceDay == null) {
			log.fine("Object 'raceDay' is null.");
			return 0;
		}
		int eventChangedCount = 0;
		Race race = raceDay.getMeeting().getRace();

		String raceName = race.getRaceName() + ", " + race.getRaceTime() + ", "
				+ race.getDistance();
		List<com.unitab.race.Runner> runners = race.getRunnerList();
		int fixedOddsCount = 0;
        
		for (com.unitab.race.Runner runner : runners) {
			FixedOdds fixedOdds = runner.getFixedOdds();
			if (fixedOdds != null) {
				Float winFixedOdds = runner.getFixedOdds().getWinOdds();
				if (winFixedOdds != null && winFixedOdds > 0) {
					fixedOddsCount++;
					log.fine(new StringBuilder(100).append(runner.getRunnerName()).append(": ").append(winFixedOdds).toString());
				}
			}
		}

		if (fixedOddsCount > 0) {
			OutputStream os = new ByteArrayOutputStream();
			try {
				mctx = bFactRace.createMarshallingContext();
				mctx.marshalDocument(race, "UTF-8", null, os);
			} catch (JiBXException e) {
				log.severe("Error marshalling event: " + e.getMessage());
			}

			String eventContent = os.toString();
			log.fine("event: " + eventContent);

			if (isDataFeedEventAlreadyExistsByEventUri(uri)) {
				DataFeedEvent dfe = getDataFeedEventByEventUri(uri);
				if (eventContent != null
						&& !eventContent.equals(dfe.getEventContent())) {

					log
							.info("content replaced for event with name "
									+ raceName);
					dfe.setName(raceName);
					dfe.setEventContent(eventContent);
					em.merge(dfe);
					eventChangedCount++;
				}
			}
		}
		return eventChangedCount;
	}

	private void readCentrebetDataFeed(DataFeed dataFeed) {
		String sXmlFeedData = null;
		log.info("read xmlfeed from: " + dataFeed.getUri());
		try {
			sXmlFeedData = getURLContentAsString(dataFeed.getUri());
		} catch (URLContentGetterException e) {
			log.severe("get URI content error: " + e.getMessage());
		}

		if (sXmlFeedData == null) {
			log.log(Level.WARNING, "XmlFeedData is not read.");
			return;
		}

		IBindingFactory bfact = null;
		IUnmarshallingContext uctx;
		IMarshallingContext mctx;
		Oxip result = null;
		try {
			bfact = BindingDirectory.getFactory(Oxip.class);
			uctx = bfact.createUnmarshallingContext();
			result = (Oxip) uctx.unmarshalDocument(new StringReader(
					sXmlFeedData), null);

		} catch (JiBXException e) {
			log.severe("Unmarshall error: " + e.getMessage());

		}
		if (result == null) {
			log.log(Level.WARNING, "Root object 'Oxip' is null.");
			return;
		}

		List<Type> types = result.getResponse().getRespQueryApplyXSLT()
				.getBookmaker().get_Class().getTypeList();

		if (types == null) {
			log.log(Level.WARNING, " no types found in current xml datafeed!");
			return;
		}

		for (Type _type : types) {
			List<Event> events = _type.getEventList();
			String townName = _type.getName().getName();

			if (events != null) {
				log.info(new StringBuilder(100).append("townName:").append(townName).append(", events.size():").append(events.size()).toString());
				int eventAddedCount = 0;
				int eventChangedCount = 0;
				for (Event _event : events) {
					Integer eventId = _event.getId().getId();
					String eventName = _event.getName().getName();
					String eventDate = String.valueOf(_event.getDate().getDate());
					java.sql.Time eventTime = _event.getTime().getTime();

					log.fine(new StringBuilder(100).append("Race (event) Name: ").append(eventName).append(",\tdate: ").append(eventDate).append(",\ttime: ").append(eventTime).toString());

					Market market = _event.getMarket();

					String marketName = market.getName().getName();

					List<Outcome> outcomes = market.getOutcomeList();
					log.fine(new StringBuilder(100).append("marketName=").append(marketName).toString());
					int oddsCount = 0;
					if (outcomes != null) {
						log.fine(new StringBuilder(100).append("marketName=").append(marketName).append(", outcomes.size(): ").append(outcomes.size()).toString());

						for (Outcome outcome : outcomes) {
							int oId = outcome.getId().getId();
							String oName = outcome.getName().getName();

							if (outcome.getDecimalodds() != null) {
								Float odds = outcome.getDecimalodds() != null ? outcome
										.getDecimalodds().getDecimalodds()
										: 0.0f;
								if (odds > 0)
									oddsCount++;
								log.fine(new StringBuilder(100).append("oId: ").append(oId).append(",\toName:").append(oName).append(",\todds:").append(odds).toString());
							}
						}
						if (oddsCount > 0) {
							// marchall current event
							OutputStream os = new ByteArrayOutputStream();
							try {
								mctx = bfact.createMarshallingContext();
								mctx.marshalDocument(_event, "UTF-8", null, os);
							} catch (JiBXException e) {
								log.severe(new StringBuilder(100).append("Error marshalling event: ").append(e.getMessage()).toString());
							}

							String eventContent = os.toString();
							log.fine(new StringBuilder(100).append("event: ").append(eventContent).toString());
							if (!isDataFeedEventAlreadyExistsByEventId(eventId)) {
								DataFeedEvent dfe = new DataFeedEvent();
								dfe.setEventId(eventId);
								dfe.setName(eventName);
								dfe.setEventContent(eventContent);
								dfe.setDataFeed(dataFeed);
								em.merge(dfe);
								eventAddedCount++;
							} else {
								DataFeedEvent dfe = getDataFeedEventByEventId(eventId);
								if (eventContent != null
										&& !eventContent.equals(dfe
												.getEventContent())) {

									log
											.info(new StringBuilder(100).append("content replaced for event with id ").append(eventId).toString());
									dfe.setEventContent(os.toString());
									em.merge(dfe);
									eventChangedCount++;
								}
							}
						} // oddsCount> 0
					} // if (outcomes != null)

				} // for (Event _event : events) {
				log.info(new StringBuilder(100).append("dataFeed \"").append(dataFeed.getName()).append("\" : ").append(eventAddedCount).append(" events added, ").append(eventChangedCount).append(" events changed.").toString());
			}// if events != null
		}
	}

	public String getURLContentAsString(String path)
			throws URLContentGetterException {

		String sConTimeout = System.getProperty(PROP_CON_TIMEOUT);
		int httpTimeout = (sConTimeout != null) ? Integer.valueOf(sConTimeout)
				: HTTP_CON_TIMEOUT;
		String sReadTimeout = System.getProperty(PROP_READ_TIMEOUT);
		int readTimeout = (sReadTimeout != null) ? Integer
				.valueOf(sReadTimeout) : READ_TIMEOUT;
		return getURLContentAsString(path, httpTimeout, readTimeout);
	}

	public String getHttpUrlContentAsString(String path, String data2send,
			String httpMethod) throws URLContentGetterException {
		String sConTimeout = System.getProperty(PROP_CON_TIMEOUT);
		int httpTimeout = (sConTimeout != null) ? Integer.valueOf(sConTimeout)
				: HTTP_CON_TIMEOUT;
		String sReadTimeout = System.getProperty(PROP_READ_TIMEOUT);
		int readTimeout = (sReadTimeout != null) ? Integer
				.valueOf(sReadTimeout) : READ_TIMEOUT;
		return getHttpUrlContentAsString(path, data2send, httpMethod,
				httpTimeout, readTimeout);

	}

	public String getHttpUrlContentAsString(String path, String data2send,
			String httpMethod, int connectionTimeout, int readTimeout)
			throws URLContentGetterException {

		InputStream inURLContent = null;
		InputStreamReader inDecodedURLContent = null;
		BufferedReader urlReader = null;
		String result = null;
		HttpURLConnection connection = null;

		log.log(Level.WARNING, new StringBuilder(100).append("requested URL: \"").append(path).append('\"').toString());
		log.log(Level.WARNING, new StringBuilder(100).append("data sent: \"").append(data2send).append('\"').toString());

		try {
			URL url = null;
			try {
				url = new URL(path);
			} catch (MalformedURLException e) {
				commonExceptionProcess("Bad URL \"" + path + '\"', e);
			}

			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					if (urlHostName != null
							&& urlHostName.equals(session.getPeerHost())) {
						return true;
					} else {
						log.log(Level.WARNING, "Warning: URL Host: " + urlHostName + " vs. "
								+ session.getPeerHost());
						return false;
					}
				}
			};

			String sTrustAll = System.getProperty(TRUST_ALL_CERTS);
			if (sTrustAll != null && "true".equals(sTrustAll)) {
				try {
					trustAllHttpsCertificates();
				} catch (Exception e) {
					log.severe(e.getMessage());
				}
			}

			try {
				if (sTrustAll != null && "true".equals(sTrustAll)) {
					HttpsURLConnection.setDefaultHostnameVerifier(hv);
				}

				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(connectionTimeout);
				connection.setReadTimeout(readTimeout);

				connection.setRequestMethod(httpMethod);
				connection
						.setRequestProperty("Content-type", "text/xml; utf-8");

				connection.setDoInput(true);
				connection.setDoOutput(true);
				if (data2send != null) {
					connection.setRequestProperty("Content-Length", String.valueOf(data2send.getBytes().length));

					OutputStream os = connection.getOutputStream();

					PrintWriter out = null;
					try {
						out = new PrintWriter(os);
						out.print(data2send);
					} finally {
						if (out != null) {
							out.flush();
							out.close();
						}
					}
				}
				inURLContent = connection.getInputStream();

			} catch (IOException e) {
				commonExceptionProcess("Can't open URL \"" + path + '\"', e);
			}

			if (inURLContent == null)
				throwLocalException(" No input from URL");

			try {
				inDecodedURLContent = new InputStreamReader(inURLContent,
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				commonExceptionProcess(" Can't find decoding character scheme",
						e);
			}

			urlReader = new BufferedReader(inDecodedURLContent);

			StringBuffer sb;
            sb = new StringBuffer(2048);
            while (true) {
				String line = null;
				try {
					line = urlReader.readLine();
				} catch (IOException e) {
					commonExceptionProcess(" Error while reading URL content",
							e);
				}
				if (line == null)
					break;
				sb.append(line);
				sb.append('\n');
			}
			result = sb.toString();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			// Release resources
			if (urlReader != null)
				try {
					urlReader.close();
				} catch (IOException e) {
					log.severe(" Can't close BufferedReader" + e.getMessage());
				}
			if (inDecodedURLContent != null)
				try {
					inDecodedURLContent.close();
				} catch (IOException e) {
					log
							.severe(" Can't close InputStreamReader"
									+ e.getMessage());
				}
			if (inURLContent != null)
				try {
					inURLContent.close();
				} catch (IOException e) {
					log.severe(" Can't close InputStream" + e.getMessage());
				}
		}

		log.log(Level.WARNING, "given response: \"" + result + '\"');
		return result;
	}

	public String getURLContentAsString(String path, int connectionTimeout,
			int readTimeout) throws URLContentGetterException {

		InputStream inURLContent = null;
		InputStreamReader inDecodedURLContent = null;
		BufferedReader urlReader = null;
		String result = null;
		try {
			URL url = null;
			try {
				url = new URL(path);
			} catch (MalformedURLException e) {
				commonExceptionProcess("Bad URL \"" + path + '\"', e);
			}

			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					if (urlHostName != null
							&& urlHostName.equals(session.getPeerHost())) {
						return true;
					} else {
						log.log(Level.WARNING, "Warning: URL Host: " + urlHostName + " vs. "
								+ session.getPeerHost());
						return false;
					}
				}
			};

			String sTrustAll = System.getProperty(TRUST_ALL_CERTS);
			if (sTrustAll != null && "true".equals(sTrustAll)) {
				try {
					trustAllHttpsCertificates();
				} catch (Exception e) {
					log.severe(e.getMessage());
				}
			}
			try {
				if (sTrustAll != null && "true".equals(sTrustAll)) {
					HttpsURLConnection.setDefaultHostnameVerifier(hv);
				}
				URLConnection urlCon = url.openConnection();
				urlCon.setConnectTimeout(connectionTimeout);
				urlCon.setReadTimeout(readTimeout);
				urlCon.setDoInput(true);
				urlCon.setDoOutput(false);
				inURLContent = urlCon.getInputStream();
			} catch (IOException e) {
				commonExceptionProcess("Can't open URL", e);
			}

			if (inURLContent == null)
				throwLocalException(" No input from URL");

			try {
				inDecodedURLContent = new InputStreamReader(inURLContent,
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				commonExceptionProcess(" Can't find decoding character scheme",
						e);
			}

			urlReader = new BufferedReader(inDecodedURLContent);

            StringBuilder sb = new StringBuilder(2048);
			while (true) {
				String line = null;
				try {
					line = urlReader.readLine();
				} catch (IOException e) {
					commonExceptionProcess(" Error while reading URL content",
							e);
				}
				if (line == null)
					break;
				sb.append(line);
				sb.append('\n');
			}
			result = sb.toString();
		} finally {
			// Release resources
			if (urlReader != null)
				try {
					urlReader.close();
				} catch (IOException e) {
					log.severe(" Can't close BufferedReader" + e.getMessage());
				}
			if (inDecodedURLContent != null)
				try {
					inDecodedURLContent.close();
				} catch (IOException e) {
					log
							.severe(" Can't close InputStreamReader"
									+ e.getMessage());
				}
			if (inURLContent != null)
				try {
					inURLContent.close();
				} catch (IOException e) {
					log.severe(" Can't close InputStream" + e.getMessage());
				}
		}
		return result;
	}

	private static class MyITM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@SuppressWarnings("unused")
		public static boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {

			/*
			 * for (X509Certificate cert : certs) log.fine(cert.getVersion());
			 */

			return true;
		}

		@SuppressWarnings("unused")
		public static boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			/*
			 * for (X509Certificate cert : certs) log.fine(cert.getVersion());
			 */

			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {

		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {

		}
	}

	private  void trustAllHttpsCertificates() throws Exception {

		// Create a trust manager that does not validate certificate chains:

		javax.net.ssl.TrustManager[] trustAllCerts =

		new javax.net.ssl.TrustManager[1];

		javax.net.ssl.TrustManager tm = new MyITM();

		trustAllCerts[0] = tm;

		javax.net.ssl.SSLContext sc =

		javax.net.ssl.SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, null);

		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

		sc.getSocketFactory());

	}

}
