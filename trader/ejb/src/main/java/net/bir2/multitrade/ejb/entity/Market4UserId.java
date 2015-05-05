package net.bir2.multitrade.ejb.entity;

public class Market4UserId implements java.io.Serializable{

	private static final long serialVersionUID = 4530007929898831370L;
	private long marketId;
	private int userId;
	
	public long getMarketId() {
		return marketId;
	}

	public int getUserId() {
		return userId;
	}

	public Market4UserId() {}
	
	public Market4UserId(long marketId, int userId) {
		super();
		this.marketId = marketId;
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (marketId ^ (marketId >>> 32));
		result = prime * result + userId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Market4UserId other = (Market4UserId) obj;
		if (marketId != other.marketId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	
}
