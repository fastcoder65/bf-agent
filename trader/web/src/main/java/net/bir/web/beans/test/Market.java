package net.bir.web.beans.test;

public  class Market extends MarketItem {

	private static final long serialVersionUID = 6434979417929445997L;

	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Market(int id, String name) {
		super(id, name);
	}

}
