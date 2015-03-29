package net.bir.web.beans.test;

import java.util.List;

public class Event extends MarketItem {

	private static final long serialVersionUID = -4436985567084603421L;
	private List<Market> markets;

	public Event(int id, String name) {
		super(id, name);
	}

	public Event(int id, String name, List<Market> markets) {
		this(id, name);
		this.markets = markets;
	}

	public void click() {
		System.out.println("eventType.click");
	}

	public List<Market> getMarkets() {
		return markets;
	}

	public void setMarkets(List<Market> markets) {
		this.markets = markets;
	}

}
