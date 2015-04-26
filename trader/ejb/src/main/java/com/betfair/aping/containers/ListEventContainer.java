package com.betfair.aping.containers;

import java.util.List;

import com.betfair.aping.entities.Event;


public class ListEventContainer extends Container{
	
	private List<Event> result;
		
	public List<Event> getResult() {
		return result;
	}
	public void setResult(List<Event> result) {
		this.result = result;
	}
}
