package com.betfair.aping.entities;

import java.util.Date;

public class Race extends Root {

	public Race(String id, String name, String type) {
		super(id, name, type);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -9219002568726351201L;
	private Date startTime;
	private String venue;
	private String countryCode;

	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "Race [startTime=" + startTime + ", venue=" + venue
				+ ", countryCode=" + countryCode + ", id=" + id + ", name="
				+ name + ", type=" + type + "]";
	}
	

}
