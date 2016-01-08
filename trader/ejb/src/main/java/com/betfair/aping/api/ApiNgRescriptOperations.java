package com.betfair.aping.api;

import com.betfair.aping.ApiNGDemo;
import com.betfair.aping.entities.*;
import com.betfair.aping.enums.*;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.JsonConverter;
import com.google.gson.reflect.TypeToken;
import net.bir2.util.DTAction;

import java.util.*;


public class ApiNgRescriptOperations extends ApiNgOperations {

    private static ApiNgRescriptOperations instance = null;

    private ApiNgRescriptOperations(){}

    public static ApiNgRescriptOperations getInstance(){
        if (instance == null){
            instance = new ApiNgRescriptOperations();
        }
        return instance;
    }
    
	public String keepAlive(String appKey, String ssoId) {
		
		String result = getInstance().makeKeepAliveRequest(
				appKey, ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);
		return result;
	}

	public String logout (String appKey, String ssoId) {
		
		String result = getInstance().makeLogoutRequest(
				appKey, ssoId);
		if (ApiNGDemo.isDebug())
			System.out.println("\nResponse: " + result);
		return result;
	}

	
    public List<EventTypeResult> listEventTypes(MarketFilter filter, MarketSort sort, String appKey, String ssoId) throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        params.put(SORT, sort);
        String result = getInstance().makeRequest(ApiNgOperation.LISTEVENTTYPES.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            System.out.println("\nResponse: "+result);

        List<EventTypeResult> container = JsonConverter.convertFromJson(result, new TypeToken<List<EventTypeResult>>() {}.getType());

        return container;

    }

    public List<CompetitionResult> listCompetitions(MarketFilter filter, MarketSort sort,String maxResult,
                                         String appKey,
                                         String ssoId)
            throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        String result = getInstance().makeRequest(ApiNgOperation.LISTCOMPETITIONS.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            System.out.println("\nResponse: "+result);

        List<CompetitionResult> container = JsonConverter.convertFromJson(result, new TypeToken<List<CompetitionResult>>() {}.getType());

        return container;

    }


    public  List<EventResult> listEvents(MarketFilter filter, MarketSort sort,String maxResult,
			 String appKey, 
			 String ssoId)
			throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        String result = getInstance().makeRequest(ApiNgOperation.LISTEVENTS.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            System.out.println("\nResponse: "+result);

        List<EventResult> container = JsonConverter.convertFromJson(result, new TypeToken<List<EventTypeResult>>() {}.getType());

        return container;
		
	}

    public List<MarketBook> listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
                                           MatchProjection matchProjection, String currencyCode, String appKey, String ssoId) throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);
        String result = getInstance().makeRequest(ApiNgOperation.LISTMARKETBOOK.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            System.out.println("\nResponse: "+result);

        List<MarketBook> container = JsonConverter.convertFromJson(result, new TypeToken<List<MarketBook>>(){}.getType() );

        return container;

    }


    public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter, Set<MarketProjection> marketProjection,
                                                     MarketSort sort, String maxResult, String appKey, String ssoId) throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(FILTER, filter);
        params.put(SORT, sort);
        params.put(MAX_RESULT, maxResult);
        String result = getInstance().makeRequest(ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            System.out.println("\nResponse: "+result);

        List<MarketCatalogue> container = JsonConverter.convertFromJson(result, new TypeToken< List<MarketCatalogue>>(){}.getType() );

        return container;

    }

    public CurrentOrderSummaryReport  listCurrentOrders ( Set<String>betIds, Set<String>marketIds,
                                                                   OrderProjection orderProjection,
                                                                   int recordCount,
                                                                   String appKey, String ssoId ) throws APINGException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);
        params.put(BET_IDS, betIds);
        params.put(ORDER_PROJECTION, orderProjection);
        params.put(MAX_RESULT, recordCount);


        TimeRange timeRange = new TimeRange();

        Calendar c = Calendar.getInstance();

        c.add(Calendar.DAY_OF_YEAR, -3);

        timeRange.setFrom(DTAction.getTimeFormBegin(c.getTime()));

        c.add(Calendar.DAY_OF_YEAR, 4);

        timeRange.setTo(DTAction.getTimeToEnd(c.getTime()));

        params.put("dateRange", timeRange);

        params.put("orderBy", OrderBy.BY_PLACE_TIME);

        String result = getInstance().makeRequest(ApiNgOperation.LISTCURRENTORDERS.getOperationName(), params, appKey, ssoId);

        if(ApiNGDemo.isDebug())
            System.out.println("\nResponse: "+result);

        return JsonConverter.convertFromJson(result, CurrentOrderSummaryReport.class);

    }

    public PlaceExecutionReport placeOrders(String marketId, List<PlaceInstruction> instructions, String customerRef , String appKey, String ssoId) throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_ID, marketId);
        params.put(INSTRUCTIONS, instructions);
        params.put(CUSTOMER_REF, customerRef);
        String result = getInstance().makeRequest(ApiNgOperation.PLACORDERS.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            System.out.println("\nResponse: "+result);

        return JsonConverter.convertFromJson(result, PlaceExecutionReport.class);

    }


    protected String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken)  throws  APINGException {
        String requestString;
        //Handling the Rescript request
        params.put("id", 1);

        requestString =  JsonConverter.convertToJson(params);
        if(ApiNGDemo.isDebug())
            System.out.println("\nRequest: "+requestString);

        //We need to pass the "sendPostRequest" method a string in util format:  requestString
        HttpUtil requester = new HttpUtil();
        String response = requester.sendPostRequestRescript(requestString, operation, appKey, ssoToken);
        if(response != null)
            return response;
        else
            throw new APINGException();
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

