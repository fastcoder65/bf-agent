package com.betfair.aping.entities;

public class Group  extends Root  {

	public Group(String id, String name, String type) {
		super(id, name, type);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", type=" + type + "]";
	}

}
