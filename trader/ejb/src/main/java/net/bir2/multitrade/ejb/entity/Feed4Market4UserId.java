package net.bir2.multitrade.ejb.entity;

public class Feed4Market4UserId implements java.io.Serializable {

	private static final long serialVersionUID = -6510620229415756018L;

	private long marketId;
	private int userId;

	public long getMarketId() {
		return marketId;
	}

	public int getUserId() {
		return userId;
	}

	private long dataFeedEventId;

	public long getDataFeedEventId() {
		return dataFeedEventId;
	}

	public Feed4Market4UserId() {
	}

	public Feed4Market4UserId(long marketId, int userId, long dataFeedEventId) {
		super();
		this.marketId = marketId;
		this.userId = userId;
		this.dataFeedEventId = dataFeedEventId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (dataFeedEventId ^ (dataFeedEventId >>> 32));
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
		Feed4Market4UserId other = (Feed4Market4UserId) obj;
		if (dataFeedEventId != other.dataFeedEventId)
			return false;
		if (marketId != other.marketId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

}
