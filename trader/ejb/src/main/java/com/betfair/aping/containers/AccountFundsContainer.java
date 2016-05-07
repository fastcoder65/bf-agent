package com.betfair.aping.containers;

import com.betfair.aping.entities.AccountFundsResponse;


public class AccountFundsContainer extends Container{
	
	private AccountFundsResponse result;
		
	public AccountFundsResponse getResult() {
		return result;
	}
	public void setResult(AccountFundsResponse result) {
		this.result = result;
	}
}
