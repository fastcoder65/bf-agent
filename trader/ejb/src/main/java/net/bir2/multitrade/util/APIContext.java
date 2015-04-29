package net.bir2.multitrade.util;

import java.util.Date;

// Class for storing the connection status and other details of an API connection
public class APIContext implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// The session token
	private String token;
	
	private String product;
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	// the API calls usage map.
	private UsageMap usage = new UsageMap();

	// The Last time a call was made.
	private Date lastCall;
	
	public String getToken() {
		return token;
	}
	
	public UsageMap getUsage() {
		return usage;
	}

	public Date getLastCall() {
		return lastCall;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setLastCall(Date lastCall) {
		this.lastCall = lastCall;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		APIContext other = (APIContext) obj;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "APIContext [token=" + token + ", product=" + product + "]";
	}
	
}
