package com.betfair.aping.enums;

public enum ApiNgOperation {
	LISTEVENTTYPES("listEventTypes"), 
	LISTCOMPETITIONS("listCompetitions"),
	LISTTIMERANGES("listTimeRanges"),
	LISTEVENTS("listEvents"),
	LISTMARKETTYPES("listMarketTypes"),
	LISTCOUNTRIES("listCountries"),
	LISTVENUES("listVenues"),

	LISTMARKETCATALOGUE("listMarketCatalogue"),

	LISTMARKETBOOK("listMarketBook"),

	LISTCURRENTORDERS("listCurrentOrders"),

	KEEPALIVE("keepAlive"),
	getAccountFunds("getAccountFunds"),

	CANCEL_ORDERS("cancelOrders"),
	REPLACE_ORDERS("replaceOrders"),
	LIST_MARKET_PROFIT_AND_LOSS("listMarketProfitAndLoss"),
	PLACE_ORDERS("placeOrders");

	private String operationName;
	
	private ApiNgOperation(String operationName){
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}

}
