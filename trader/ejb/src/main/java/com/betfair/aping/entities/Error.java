package com.betfair.aping.entities;

public class Error {

	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Error{" +
				"data=" + data +
				'}';
	}
}
