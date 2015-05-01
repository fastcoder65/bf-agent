package com.betfair.aping.containers;

import java.util.List;

import com.betfair.aping.entities.EventResult;


public class ListEventContainer extends Container{
	
	private List<EventResult> result;
		
	public List<EventResult> getResult() {
		return result;
	}
	public void setResult(List<EventResult> result) {
		this.result = result;
	}
}
