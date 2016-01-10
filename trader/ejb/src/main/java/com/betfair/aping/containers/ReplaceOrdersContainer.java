package com.betfair.aping.containers;


import com.betfair.aping.entities.PlaceExecutionReport;
import com.betfair.aping.entities.ReplaceExecutionReport;

public class ReplaceOrdersContainer extends Container {

	private ReplaceExecutionReport result;
	
	public ReplaceExecutionReport getResult() {
		return result;
	}
	
	public void setResult(ReplaceExecutionReport result) {
		this.result = result;
	}

}
