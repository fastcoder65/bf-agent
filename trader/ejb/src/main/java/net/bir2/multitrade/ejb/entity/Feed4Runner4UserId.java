package net.bir2.multitrade.ejb.entity;

public class Feed4Runner4UserId  implements java.io.Serializable{

	private static final long serialVersionUID = 8798067734311798559L;

	private int userId;
	
	private long runnerId;

	private long dataFeedEventId;
	
	public long getDataFeedEventId() {
		return dataFeedEventId;
	}

	public int getUserId() {
		return userId;
	}

	public long getRunnerId() {
		return runnerId;
	}
	
	public Feed4Runner4UserId() {}
	
	public Feed4Runner4UserId(int userId, long runnerId, long dataFeedEventId) {
		super();
		this.userId = userId;
		this.runnerId = runnerId;
		this.dataFeedEventId = dataFeedEventId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (dataFeedEventId ^ (dataFeedEventId >>> 32));
		result = prime * result + (int) (runnerId ^ (runnerId >>> 32));
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
		Feed4Runner4UserId other = (Feed4Runner4UserId) obj;
		if (dataFeedEventId != other.dataFeedEventId)
			return false;
		if (runnerId != other.runnerId)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

}
