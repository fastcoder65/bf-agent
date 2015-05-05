package com.betfair.aping.entities;

import java.io.Serializable;
import java.util.List;

public class Root implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	protected String id;
	protected String name;
	protected String type;
	
	public List<Root> getChildren() {
		return children;
	}
	public void setChildren(List<Root> children) {
		this.children = children;
	}
	
	protected List<Root> children;
	
}
