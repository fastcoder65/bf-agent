package com.betfair.aping.entities;

import java.util.Date;

public class Event extends Root {

	public Event(String id, String name, String type) {
		super(id, name, type);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -6420728416591165012L;
	private String countryCode;
	private String timezone;
	private String venue;
	private Date openDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	@Override
	public String toString() {
		return "Event [countryCode=" + countryCode + ", timezone=" + timezone
				+ ", venue=" + venue + ", openDate=" + openDate + ", id=" + id
				+ ", name=" + name + ", type=" + type + "]";
	}


}
