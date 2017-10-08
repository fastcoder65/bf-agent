package com.betfair.aping.api;

import com.betfair.aping.ApiNGDemo;
import com.betfair.aping.containers.*;
import com.betfair.aping.entities.*;
import com.betfair.aping.enums.*;
import com.betfair.aping.exceptions.APINGException;
import com.betfair.aping.exceptions.AccountAPINGException;
import com.betfair.aping.util.JsonConverter;
import com.betfair.aping.util.JsonResponseHandler;
import com.betfair.aping.util.JsonrpcRequest;
import net.bir2.util.DTAction;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.*;
import java.util.logging.Level;

@ConcurrencyManagement( ConcurrencyManagementType.BEAN)
@Singleton
public class ApiNgJsonRpcOperations extends ApiNgOperations {

    @EJB
    HttpUtil requester;
/*
    public ApiNgJsonRpcOperations() {

	}
*/

    @PostConstruct
    public void create() {
        System.out.println("** ApiNgJsonRpcOperations @PostConstruct create() , hashcode=" + this.hashCode());
    }

    public AccountFundsResponse getAccountFunds(Wallet wallet, String appKey, String ssoId) throws AccountAPINGException {
        AccountFundsContainer container = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("wallet", wallet);

        String result = makeAccountRequest(ApiNgOperation.getAccountFunds.getOperationName(), params, appKey, ssoId);


//		if (ApiNGDemo.isDebug())
        printLog(Level.INFO, "'getAccountFunds' Response: " + result);

        if (result != null)
            container = JsonConverter.convertFromJson(result, AccountFundsContainer.class);

        if (container != null && container.getError() != null) {
            log.severe("getAccountFunds error mesage: " + container.getError().toString()); // container.getError().getData().getAPINGException().getMessage());
            //	throw (AccountAPINGException) container.getError().getData().getAPINGException();
        }

        return (container != null ? container.getResult() : null);

    }

    public String keepAlive(String appKey, String ssoId) {

        String result = makeKeepAliveRequest(appKey, ssoId);
        if (ApiNGDemo.isDebug())
            printLog("'keepAlive' Response: " + result);
        return result;
    }

    public String logout(String appKey, String ssoId) {

        String result = makeLogoutRequest(appKey, ssoId);
        if (ApiNGDemo.isDebug())
            printLog("'logout' Response: " + result);

        return result;
    }

    public List<EventTypeResult> listEventTypes(MarketFilter filter, MarketSort sort,
                                                String appKey, String ssoId) throws APINGException {

        printLog("appKey: " + appKey);
        printLog("ssoId: " + ssoId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale);
        params.put(SORT, sort);

        String result = makeRequest(
                ApiNgOperation.LISTEVENTTYPES.getOperationName(), params,
                appKey, ssoId);
        if (ApiNGDemo.isDebug())
            printLog("'listEventTypes' Response: " + result);

        EventTypeResultContainer container = JsonConverter.convertFromJson(
                result, EventTypeResultContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }

    public List<CompetitionResult> listCompetitions
            (MarketFilter filter, MarketSort sort, String maxResult, String appKey, String ssoId)
            throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(FILTER, filter);
        params.put(SORT, sort);
        params.put(MAX_RESULT, maxResult);

        String result = makeRequest(
                ApiNgOperation.LISTCOMPETITIONS.getOperationName(), params, appKey,
                ssoId);

        if (ApiNGDemo.isDebug())
            printLog("'listCompetitions' Response: " + result);

        CompetitionResultContainer container = JsonConverter.convertFromJson(result,
                CompetitionResultContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }

    public List<EventResult> listEvents(MarketFilter filter, MarketSort sort,
                                        String maxResult, String appKey, String ssoId)
            throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(FILTER, filter);
        params.put(SORT, sort);
        params.put(MAX_RESULT, maxResult);

        String result = makeRequest(
                ApiNgOperation.LISTEVENTS.getOperationName(), params, appKey,
                ssoId);

        if (ApiNGDemo.isDebug())
            printLog("'listEvents' Response: " + result);

        EventResultContainer container = JsonConverter.convertFromJson(result,
                EventResultContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }

    public List<MarketBook> listMarketBook(List<String> marketIds,
                                           PriceProjection priceProjection, OrderProjection orderProjection,
                                           MatchProjection matchProjection, String currencyCode,
                                           String appKey, String ssoId) throws APINGException {

        String result = null;
        ListMarketBooksContainer container = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);

        params.put(PRICE_PROJECTION, priceProjection);
        params.put(ORDER_PROJECTION, orderProjection);
        params.put(MATCH_PROJECTION, matchProjection);
        params.put("currencyCode", currencyCode);

        result = makeRequest(ApiNgOperation.LISTMARKETBOOK.getOperationName(), params, appKey, ssoId);

        if (ApiNGDemo.isDebug())
            printLog("'listMarketBook' Response: " + result);

        if (result != null)
            container = JsonConverter.convertFromJson(result, ListMarketBooksContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);
    }

    public List<MarketCatalogue> listMarketCatalogue(MarketFilter filter,
                                                     Set<MarketProjection> marketProjection, MarketSort sort,
                                                     String maxResult, String appKey, String ssoId)

            throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(FILTER, filter);
        params.put(SORT, sort);
        params.put(MAX_RESULT, maxResult);
        params.put(MARKET_PROJECTION, marketProjection);

        String result = makeRequest(
                ApiNgOperation.LISTMARKETCATALOGUE.getOperationName(), params,
                appKey, ssoId);
        if (ApiNGDemo.isDebug())
            printLog("'listMarketCatalogue' Response: " + result);

        ListMarketCatalogueContainer container = JsonConverter.convertFromJson(
                result, ListMarketCatalogueContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }


    public CurrentOrderSummaryReport listCurrentOrders(Set<String> betIds, Set<String> marketIds,
                                                       OrderProjection orderProjection,

                                                       // TimeRange dateRange,
                                                       // OrderBy orderBy, SortDir sortDir,

                                                       int recordCount,
                                                       String appKey, String ssoId)
            throws APINGException {
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

        String result = makeRequest(
                ApiNgOperation.LISTCURRENTORDERS.getOperationName(), params,
                appKey, ssoId);
        if (ApiNGDemo.isDebug())
            printLog("'listCurrentOrders' Response: " + result);

        ListOrdersContainer container = JsonConverter.convertFromJson(result, ListOrdersContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }

    public List<MarketProfitAndLoss> listMarketProfitAndLoss(Set<String> marketIds, boolean includeSettledBets, boolean includeBspBets, boolean netOfCommission,
                                                             String appKey, String ssoId) throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_IDS, marketIds);

        params.put(param_includeSettledBets, includeSettledBets);
        params.put(param_includeBspBets, includeBspBets);
        params.put(param_netOfCommission, netOfCommission);

        String result = makeRequest(ApiNgOperation.LIST_MARKET_PROFIT_AND_LOSS.getOperationName(), params, appKey, ssoId);

        if (ApiNGDemo.isDebug())
            printLog("'cancelOrders' Response: " + result);

        MarketProfitAndLossContainer container = JsonConverter.convertFromJson(result,
                MarketProfitAndLossContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }


    public CancelExecutionReport cancelOrders(String marketId, List<CancelInstruction> instructions, String customerRef,
                                              String appKey, String ssoId) throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();

//		params.put(LOCALE, locale);
        params.put(MARKET_ID, marketId);
        params.put(INSTRUCTIONS, instructions);
        params.put(CUSTOMER_REF, customerRef);

        String result = makeRequest(ApiNgOperation.CANCEL_ORDERS.getOperationName(), params, appKey, ssoId);

        if (ApiNGDemo.isDebug())
            printLog("'cancelOrders' Response: " + result);

        CancelOrdersContainer container = JsonConverter.convertFromJson(result,
                CancelOrdersContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }

    public ReplaceExecutionReport replaceOrders(String marketId,
                                                List<ReplaceInstruction> instructions, String customerRef,
                                                String appKey, String ssoId) throws APINGException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_ID, marketId);
        params.put(INSTRUCTIONS, instructions);
        params.put(CUSTOMER_REF, customerRef);

        String result = makeRequest(
                ApiNgOperation.REPLACE_ORDERS.getOperationName(), params, appKey,
                ssoId);

        if (ApiNGDemo.isDebug())
            printLog("'replaceOrders' Response: " + result);

        ReplaceOrdersContainer container = JsonConverter.convertFromJson(result,
                ReplaceOrdersContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }


    public PlaceExecutionReport placeOrders(String marketId,
                                            List<PlaceInstruction> instructions, String customerRef,
                                            String appKey, String ssoId) throws APINGException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LOCALE, locale);
        params.put(MARKET_ID, marketId);
        params.put(INSTRUCTIONS, instructions);
        params.put(CUSTOMER_REF, customerRef);

        String result = makeRequest(
                ApiNgOperation.PLACE_ORDERS.getOperationName(), params, appKey,
                ssoId);
        if (ApiNGDemo.isDebug())
            printLog("'placeOrders' Response: " + result);

        PlaceOrdersContainer container = JsonConverter.convertFromJson(result,
                PlaceOrdersContainer.class);

        if (container != null && container.getError() != null)
            throw container.getError().getData().getAPINGException();

        return (container != null ? container.getResult() : null);

    }

    protected String makeRequest(String operation, Map<String, Object> params,
                                 String appKey, String ssoToken) {
        String requestString;
        // Handling the JSON-RPC request
        JsonrpcRequest request = new JsonrpcRequest();
        String _requestId = String.valueOf(customerRandom.nextLong());
        log.log(Level.FINE, "_requestId: " + _requestId);

        request.setId(_requestId);

        request.setMethod(ApiNGDemo.getProp().getProperty("SPORTS_APING_V1_0")
                + operation);
        request.setParams(params);

        requestString = JsonConverter.convertToJson(request);
        //	if (ApiNGDemo.isDebug())
        printLog(Level.FINE, "'makeRequest' Request: " + requestString);

        // We need to pass the "sendPostRequest" method a string in util format:
        // requestString
//		HttpUtil requester = new HttpUtil();

        String apiNgURL = ApiNGDemo.getProp().getProperty("APING_URL")
                + ApiNGDemo.getProp().getProperty("JSON_RPC_SUFFIX");

        return requester.sendPostRequest(requestString, operation, appKey, ssoToken, apiNgURL, new JsonResponseHandler());

		/*
		return requester.sendPostRequestJsonRpc(requestString, operation,
				appKey, ssoToken);
		*/
    }

    protected String makeAccountRequest(String operation, Map<String, Object> params, String appKey,
                                        String ssoToken) {
        String requestString;
        // Handling the JSON-RPC request
        JsonrpcRequest request = new JsonrpcRequest();
        String _requestId = String.valueOf(customerRandom.nextLong());
        log.log(Level.FINE, "_requestId: " + _requestId);

        request.setId(_requestId);

        request.setMethod(ApiNGDemo.getProp().getProperty("AccountAPING_V1_0") + operation);

        //request.setMethod(operation);

        request.setParams(params);

        requestString = JsonConverter.convertToJson(request);
        //	if (ApiNGDemo.isDebug())
        printLog(Level.FINE, "'makeAccountRequest' Request: " + requestString);

        // We need to pass the "sendPostRequest" method a string in util format:
        // requestString
//		HttpUtil requester = new HttpUtil();
        String apiNgURL = ApiNGDemo.getProp().getProperty("APING_URL_ACCOUNT")
                + ApiNGDemo.getProp().getProperty("JSON_RPC_SUFFIX");

        return requester.sendPostRequest(requestString, operation, appKey, ssoToken, apiNgURL, new JsonResponseHandler());
/*
		return requester.sendPostRequestJsonRpc(requestString, operation,
				appKey, ssoToken);
*/
    }

    protected String makeKeepAliveRequest(String appKey, String ssoToken) {
//		HttpUtil requester = new HttpUtil();
        return requester.sendKeepAlivePostRequest(appKey, ssoToken);
    }

    protected String makeLogoutRequest(String appKey, String ssoToken) {
//		HttpUtil requester = new HttpUtil();
        return requester.sendLogoutRequest(appKey, ssoToken);
    }

}
