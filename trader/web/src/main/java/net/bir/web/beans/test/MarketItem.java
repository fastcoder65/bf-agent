package net.bir.web.beans.test;

public  abstract class MarketItem implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private int id;
	private String name;

	public MarketItem(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

}
