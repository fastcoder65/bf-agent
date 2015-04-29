package com.betfair.aping.api;

import com.betfair.aping.ApiNGDemo;
import com.betfair.aping.containers.EventTypeResultContainer;
import com.betfair.aping.containers.ListEventContainer;
import com.betfair.aping.containers.ListMarketBooksContainer;
import com.betfair.aping.containers.ListMarketCatalogueContainer;
import com.betfair.aping.containers.PlaceOrdersContainer;
import com.betfair.aping.entities.*;
import com.betfair.aping.enums.*;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.JsonConverter;
import com.betfair.aping.util.JsonrpcRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiNgJsonRpcOperations extends ApiNgOperations {

	private static ApiNgJsonRpcOperations instance = null;

	private ApiNgJsonRpcOperations() {
	}

	public static ApiNgJsonRpcOperations getInstance() {
		if (instance == null) {
			instance = new ApiNgJsonRpcOperations();
		}
		return instance;
	}

	public String keepAlive(String appKey, String ssoId) {
		
		String result = getInstance().makeKeepAliveRequest(appKey, ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);
		return result;
	}

	public String logout(String appKey, String ssoId) {
		
		String result = getInstance().makeLogoutRequest(appKey, ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);
		
		return result;
	}

	public List<EventTypeResult> listEventTypes(MarketFilter filter,
			String appKey, String ssoId) throws APINGException {
	
		System.out.println ("appKey: " + appKey);
		System.out.println ("ssoId: " + ssoId);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FILTER, filter);
		params.put(LOCALE, locale);
		
		String result = getInstance().makeRequest(
				ApiNgOperation.LISTEVENTTYPES.getOperationName(), params,
				appKey, ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);

		EventTypeResultContainer container = JsonConverter.convertFromJson(
				result, EventTypeResultContainer.class);
		if (container.getError() != null)
			throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	public List<Event> listEvents(MarketFilter filter, MarketSort sort,String maxResult,
			 String appKey, 
			 String ssoId
			 )
			throws APINGException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LOCALE, locale);
		params.put(FILTER, filter);
		params.put(SORT, sort);
		params.put(MAX_RESULT, maxResult);

		String result = getInstance().makeRequest(
				ApiNgOperation.LISTEVENTS.getOperationName(), params,
				appKey, ssoId);
		
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);

		ListEventContainer container = JsonConverter.convertFromJson(result, ListEventContainer.class);

		if (container.getError() != null)
			throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	public List<MarketBook> listMarketBook(List<String> marketIds,
			PriceProjection priceProjection, OrderProjection orderProjection,
			MatchProjection matchProjection, String currencyCode,
			String appKey, String ssoId) throws APINGException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LOCALE, locale);
		params.put(MARKET_IDS, marketIds);
		String result = getInstance().makeRequest(
				ApiNgOperation.LISTMARKETBOOK.getOperationName(), params,
				appKey, ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);

		ListMarketBooksContainer container = JsonConverter.convertFromJson(
				result, ListMarketBooksContainer.class);

		if (container.getError() != null)
			throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter,
			Set<MarketProjection> marketProjection, MarketSort sort,String maxResult,
			 String appKey, String ssoId)
			throws APINGException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LOCALE, locale);
		params.put(FILTER, filter);
		params.put(SORT, sort);
		params.put(MAX_RESULT, maxResult);
		String result = getInstance().makeRequest(
				ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params,
				appKey, ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);

		ListMarketCatalogueContainer container = JsonConverter.convertFromJson(
				result, ListMarketCatalogueContainer.class);

		if (container.getError() != null)
			throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	public PlaceExecutionReport placeOrders(String marketId,
			List<PlaceInstruction> instructions, String customerRef,
			String appKey, String ssoId) throws APINGException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(LOCALE, locale);
		params.put(MARKET_ID, marketId);
		params.put(INSTRUCTIONS, instructions);
		params.put(CUSTOMER_REF, customerRef);
		String result = getInstance().makeRequest(
				ApiNgOperation.PLACORDERS.getOperationName(), params, appKey,
				ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);

		PlaceOrdersContainer container = JsonConverter.convertFromJson(result,
				PlaceOrdersContainer.class);

		if (container.getError() != null)
			throw container.getError().getData().getAPINGException();

		return container.getResult();

	}

	protected String makeRequest(String operation, Map<String, Object> params,
			String appKey, String ssoToken) {
		String requestString;
		// Handling the JSON-RPC request
		JsonrpcRequest request = new JsonrpcRequest();
		request.setId("1");
		request.setMethod(ApiNGDemo.getProp().getProperty("SPORTS_APING_V1_0")
				+ operation);
		request.setParams(params);

		requestString = JsonConverter.convertToJson(request);
		if (ApiNGDemo.isDebug())
			System.out.println("\nRequest: " + requestString);

		// We need to pass the "sendPostRequest" method a string in util format:
		// requestString
		HttpUtil requester = new HttpUtil();
		return requester.sendPostRequestJsonRpc(requestString, operation, appKey, ssoToken);
	}

	protected String makeKeepAliveRequest(String appKey, String ssoToken) {
		HttpUtil requester = new HttpUtil();
		return requester.sendKeepAlivePostRequest( appKey, ssoToken);
	}

	protected String makeLogoutRequest(String appKey, String ssoToken) {
		HttpUtil requester = new HttpUtil();
		return requester.sendLogoutRequest( appKey, ssoToken);
	}

}
