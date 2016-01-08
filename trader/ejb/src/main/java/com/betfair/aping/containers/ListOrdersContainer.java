package com.betfair.aping.containers;

import com.betfair.aping.entities.CurrentOrderSummaryReport;

public class ListOrdersContainer extends Container {

	private CurrentOrderSummaryReport result;
	
	public CurrentOrderSummaryReport getResult() {
		return result;
	}
	
	public void setResult(CurrentOrderSummaryReport result) {
		this.result = result;
	}

}
