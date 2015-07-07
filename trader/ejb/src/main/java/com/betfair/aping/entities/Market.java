package com.betfair.aping.entities;

import java.util.Date;

public class Market extends Root {

	public Market(String id, String name, String type) {
		super(id, name, type);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 2536922843974294714L;

	private String exchangeId;
	private Date marketStartTime;
	private String marketType;
	private String numberOfWinners;

	public String getExchangeId() {
		return exchangeId;
	}
	public void setExchangeId(String exchangeId) {
		this.exchangeId = exchangeId;
	}
	public Date getMarketStartTime() {
		return marketStartTime;
	}
	public void setMarketStartTime(Date marketStartTime) {
		this.marketStartTime = marketStartTime;
	}
	public String getMarketType() {
		return marketType;
	}
	public void setMarketType(String marketType) {
		this.marketType = marketType;
	}
	public String getNumberOfWinners() {
		return numberOfWinners;
	}
	public void setNumberOfWinners(String numberOfWinners) {
		this.numberOfWinners = numberOfWinners;
	}
	
	@Override
	public String toString() {
		return "Market [exchangeId=" + exchangeId + ", marketStartTime="
				+ marketStartTime + ", marketType=" + marketType
				+ ", numberOfWinners=" + numberOfWinners + ", id=" + id
				+ ", name=" + name + ", type=" + type + "]";
	}

	
}
