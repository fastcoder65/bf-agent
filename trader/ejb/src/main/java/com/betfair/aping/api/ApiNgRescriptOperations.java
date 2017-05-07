package com.betfair.aping.api;

import com.betfair.aping.ApiNGDemo;
import com.betfair.aping.containers.MarketProfitAndLossContainer;
import com.betfair.aping.entities.*;
import com.betfair.aping.enums.*;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.util.JsonConverter;
import com.google.gson.reflect.TypeToken;
import net.bir2.util.DTAction;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.util.*;
import java.util.logging.Level;

//@Lock(LockType.WRITE)
//@AccessTimeout(value=60, unit = TimeUnit.SECONDS )

@Lock(LockType.READ)
@Singleton
public class ApiNgRescriptOperations extends ApiNgOperations {


    public ApiNgRescriptOperations(){
        log.info("ApiNgRescriptOperations constructor, hashcode=" + this.hashCode());
    }

    public AccountFundsResponse  getAccountFunds  (Wallet wallet, String appKey, String ssoId )  throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("wallet", wallet);

        String result = makeRequest(
                ApiNgOperation.getAccountFunds.getOperationName(), params,
                appKey, ssoId);
        if (ApiNGDemo.isDebug())
            printLog("'getAccountFunds' Response: " + result);

        AccountFundsResponse container = JsonConverter.convertFromJson(result, new TypeToken<AccountFundsResponse>() {}.getType());

        return container;
    }


    public String keepAlive(String appKey, String ssoId) {
		
		String result = makeKeepAliveRequest(
                appKey, ssoId);
		if (ApiNGDemo.isDebug())
            printLog("Response: " + result);
		return result;
	}

	public String logout (String appKey, String ssoId) {
		
		String result = makeLogoutRequest(
                appKey, ssoId);
		if (ApiNGDemo.isDebug())
            printLog("Response: " + result);
		return result;
	}

	
    public List<EventTypeResult> listEventTypes(MarketFilter filter, MarketSort sort, String appKey, String ssoId) throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        params.put(SORT, sort);
        String result = makeRequest(ApiNgOperation.LISTEVENTTYPES.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

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
        String result = makeRequest(ApiNgOperation.LISTCOMPETITIONS.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

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
        String result = makeRequest(ApiNgOperation.LISTEVENTS.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            printLog("Response: "+result);

        List<EventResult> container = JsonConverter.convertFromJson(result, new TypeToken<List<EventTypeResult>>() {}.getType());

        return container;
		
	}

    public List<MarketBook> listMarketBook(List<String> marketIds, PriceProjection priceProjection, OrderProjection orderProjection,
                                           MatchProjection matchProjection, String currencyCode, String appKey, String ssoId) throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);
        String result = makeRequest(ApiNgOperation.LISTMARKETBOOK.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

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
        String result = makeRequest(ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

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

        String result = makeRequest(ApiNgOperation.LISTCURRENTORDERS.getOperationName(), params, appKey, ssoId);

        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

        return JsonConverter.convertFromJson(result, CurrentOrderSummaryReport.class);

    }


    public PlaceExecutionReport placeOrders(String marketId, List<PlaceInstruction> instructions, String customerRef , String appKey, String ssoId) throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_ID, marketId);
        params.put(INSTRUCTIONS, instructions);
        params.put(CUSTOMER_REF, customerRef);
        String result = makeRequest(ApiNgOperation.PLACE_ORDERS.getOperationName(), params, appKey, ssoId);

        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

        return JsonConverter.convertFromJson(result, PlaceExecutionReport.class);

    }

    public ReplaceExecutionReport replaceOrders(String marketId,
                                                List<ReplaceInstruction> instructions, String customerRef,
                                                String appKey, String ssoId) throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_ID, marketId);
        params.put(INSTRUCTIONS, instructions);
        params.put(CUSTOMER_REF, customerRef);
        String result = makeRequest(ApiNgOperation.REPLACE_ORDERS.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

        return JsonConverter.convertFromJson(result, ReplaceExecutionReport.class);

    }

    public CancelExecutionReport cancelOrders(String marketId, List<CancelInstruction> instructions, String customerRef,
                                              String appKey, String ssoId ) throws APINGException{

        Map<String, Object> params = new HashMap<String, Object>();
//        params.put(LOCALE, locale);
        params.put(MARKET_ID, marketId);
        params.put(INSTRUCTIONS, instructions);
        params.put(CUSTOMER_REF, customerRef);
        String result = makeRequest(ApiNgOperation.CANCEL_ORDERS.getOperationName(), params, appKey, ssoId);
        if(ApiNGDemo.isDebug())
            printLog("Response: " + result);

        return JsonConverter.convertFromJson(result, CancelExecutionReport.class);

    }

    public  List<MarketProfitAndLoss> listMarketProfitAndLoss ( Set<String> marketIds, boolean includeSettledBets, boolean includeBspBets, boolean netOfCommission,
                                                                String appKey, String ssoId) throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);

        params.put ( param_includeSettledBets, includeSettledBets);
        params.put ( param_includeBspBets, includeBspBets);
        params.put ( param_netOfCommission, netOfCommission);

        String result = makeRequest(ApiNgOperation.LIST_MARKET_PROFIT_AND_LOSS.getOperationName(), params, appKey, ssoId);

        if (ApiNGDemo.isDebug())
            printLog("'cancelOrders' Response: " + result);

        MarketProfitAndLossContainer container = JsonConverter.convertFromJson(result,
                MarketProfitAndLossContainer.class);

        if (container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return container.getResult();

    }


    protected String makeRequest(String operation, Map<String, Object> params, String appKey, String ssoToken)  throws  APINGException {
        String requestString;
        //Handling the Rescript request
            String _requestId = String.valueOf(customerRandom.nextLong());
            log.log(Level.INFO, "_requestId: "+ _requestId);

        params.put("id", _requestId);

        requestString =  JsonConverter.convertToJson(params);
        if(ApiNGDemo.isDebug())
            log.info("Request: "+requestString);

        //We need to pass the "sendPostRequest" method a string in util format:  requestString
      //  HttpUtil requester = new HttpUtil();
        String response = requester.sendPostRequestRescript(requestString, operation, appKey, ssoToken);
        if(response != null)
            return response;
        else
            throw new APINGException();
    }

    @EJB
    private HttpUtil requester;

	protected String makeKeepAliveRequest(String appKey, String ssoToken) {
		//HttpUtil requester = new HttpUtil();
		return requester.sendKeepAlivePostRequest( appKey, ssoToken);
	}

	protected String makeLogoutRequest(String appKey, String ssoToken) {
		//HttpUtil requester = new HttpUtil();
		return requester.sendLogoutRequest( appKey, ssoToken);
	}


}

