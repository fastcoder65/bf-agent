package net.bir2.multitrade.ejb.entity;

public class Runner4UserId implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int userId;
	
	private long runnerId;

	public int getUserId() {
		return userId;
	}

	public long getRunnerId() {
		return runnerId;
	}
	public Runner4UserId() {}
	public Runner4UserId(int userId, long runnerId) {
		super();
		this.userId = userId;
		this.runnerId = runnerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Runner4UserId other = (Runner4UserId) obj;
		if (runnerId != other.runnerId)
			return false;
        return userId == other.userId;
    }
	
	
}
