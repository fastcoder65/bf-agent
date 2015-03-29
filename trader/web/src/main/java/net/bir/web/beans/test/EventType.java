package net.bir.web.beans.test;

import java.util.List;

public class EventType extends MarketItem {

	private static final long serialVersionUID = -621093218188603732L;

	private List<Event> events;

	private String eventDesc = "eventDesc_"+ this.hashCode();
	
	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public void click() {
	 System.out.println("eventType.click");	
	}
	
	public EventType(int id, String name) {
		super(id, name);
	}

	public EventType(int id, String name, List<Event> events) {
		this(id, name);
		this.events = events;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
