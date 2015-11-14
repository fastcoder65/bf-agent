package net.bir2.handler;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.betfair.aping.entities.*;
import my.pack.util.AccountConstants;
import net.bir2.multitrade.util.APIContext;
import net.bir2.util.DTAction;

import com.betfair.aping.api.ApiNgJsonRpcOperations;
import com.betfair.aping.api.ApiNgOperations;
import com.betfair.aping.enums.MarketProjection;
import com.betfair.aping.enums.MarketSort;
import com.betfair.aping.enums.MatchProjection;
import com.betfair.aping.enums.OrderProjection;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.HttpClientSSO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GlobalAPI {

	private static final Logger log = Logger.getLogger(GlobalAPI.class
			.getName());

	private static final String SESSION_TOKEN = "sessionToken";
	private static final String LOGIN_STATUS = "loginStatus";

	private static final String STATUS = "status";

	private static final ObjectMapper om = new ObjectMapper();

	public static void login(APIContext context, String userName,
			String password) throws Exception {
		String sessionToken = getSessionToken(AccountConstants.APP_KEY,
				userName, password);
		context.setProduct(AccountConstants.APP_KEY);
		context.setToken(sessionToken);
	}

	private static String getSessionToken(String appKey, String userName,
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

			log.info("Session token:" + sessionToken);
		} else {
			log.severe("Getting null session token from BetFair");
		}

		return sessionToken;
	}

	private static ApiNgOperations jsonOperations = ApiNgJsonRpcOperations
			.getInstance();

	// Get the active event types within the system (on both exchanges)
	public static List<EventTypeResult> getActiveEventTypes(APIContext context)
			throws APINGException {
		MarketFilter marketFilter = new MarketFilter();
		List<EventTypeResult> r = jsonOperations.listEventTypes(marketFilter,
				context.getProduct(), context.getToken());
		return r;
	}

	public static List<CompetitionResult> getCompetitions(APIContext context,
											  Set<String> eventTypeIds, Set<String> eventIds)
			throws APINGException {
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

		List<CompetitionResult> r = jsonOperations.listCompetitions(marketFilter,
				MarketSort.FIRST_TO_START, "500", context.getProduct(),
				context.getToken());
		return r;
	}


	public static List<EventResult> getEvents(APIContext context,
			Set<String> eventTypeIds,Set<String> competitionIds, Set<String> eventIds)
			throws APINGException {
		MarketFilter marketFilter = new MarketFilter();
		Set<String> _eventTypeIds = new HashSet<String>();
		Set<String> _competitionIds  = new HashSet<String>();
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

		TimeRange timeRange = new TimeRange();

		Calendar c = Calendar.getInstance();

		timeRange.setFrom(DTAction.getTimeFormBegin(c.getTime()));

		c.add(Calendar.DAY_OF_YEAR, 3);

		timeRange.setTo(DTAction.getTimeToEnd(c.getTime()));

		marketFilter.setMarketStartTime(timeRange);

		marketFilter.setTurnInPlayEnabled(true);

		List<EventResult> r = jsonOperations.listEvents(marketFilter,
				MarketSort.FIRST_TO_START, "500", context.getProduct(),
				context.getToken());
		return r;
	}

	public static List<MarketCatalogue> getMarkets(APIContext context,
			Set<String> eventIds, Set<String> marketIds) throws APINGException {
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

		if (marketIds != null && marketIds.size() > 0) {
			_marketIds.addAll(marketIds);
			marketFilter.setMarketIds(_marketIds);
//			marketProjection.add(MarketProjection.COMPETITION);
//			marketProjection.add(MarketProjection.EVENT);
			marketProjection.add(MarketProjection.MARKET_DESCRIPTION);
			marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);
			marketProjection.add(MarketProjection.RUNNER_METADATA);

		}	
		
		TimeRange timeRange = new TimeRange();
		Calendar c = Calendar.getInstance();
		timeRange.setFrom(DTAction.getTimeFormBegin(c.getTime()));

		c.add(Calendar.DAY_OF_YEAR, 3);
		timeRange.setTo(DTAction.getTimeToEnd(c.getTime()));

		marketFilter.setMarketStartTime(timeRange);
		marketFilter.setTurnInPlayEnabled(true);
// import com.betfair.aping.enums.MarketProjection;
		

	//	marketProjection.add(MarketProjection.COMPETITION);
		marketProjection.add(MarketProjection.EVENT);
	//	marketProjection.add(MarketProjection.EVENT_TYPE);
		marketProjection.add(MarketProjection.MARKET_DESCRIPTION);
		marketProjection.add(MarketProjection.RUNNER_DESCRIPTION);
		marketProjection.add(MarketProjection.RUNNER_METADATA);
		marketProjection.add(MarketProjection.MARKET_START_TIME);

		
		List<MarketCatalogue> r = jsonOperations.listMarketCatalogue(
				marketFilter, marketProjection, MarketSort.FIRST_TO_START,
				"500", context.getProduct(), context.getToken());

		return r;
	}

	public static List<MarketBook> listMarketBook(APIContext context, List<String> marketIds,
			PriceProjection priceProjection, OrderProjection orderProjection,
			MatchProjection matchProjection, String currencyCode) {
		
		List<MarketBook> result=null;
		try {
			result = jsonOperations.listMarketBook(marketIds, priceProjection, orderProjection, matchProjection, currencyCode, context.getProduct(), context.getToken());
		} catch (APINGException e) {
			log.log(Level.SEVERE, "error getting marketBook ", e);
		}
		return result;
	}
			
	// @TODO Implement Keep alive for Italian jurisdiction

	public static void keepAlive(APIContext context) throws Exception {
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

	public static void logout(APIContext context) throws Exception {
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

}
