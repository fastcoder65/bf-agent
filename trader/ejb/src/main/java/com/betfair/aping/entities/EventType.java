package com.betfair.aping.entities;

public class EventType extends Root {
	
	public EventType(String id, String name, String type) {
		super(id, name, type);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -6658152462101249370L;

	@Override
	public String toString() {
		return "EventType [id=" + id + ", name=" + name + ", type=" + type
				+ "]";
	}


}
