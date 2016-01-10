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

	CANCEL_ORDERS("cancelOrders"),
	PLACE_ORDERS("placeOrders");
	
	private String operationName;
	
	private ApiNgOperation(String operationName){
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}

}
