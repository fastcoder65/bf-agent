package net.bir2.ejb.action;

import java.util.Map;

import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Uzer;

public interface ShedulerActivity {

	 // Life cycle method 
	 void create () throws Exception; 
	 void destroy () throws Exception; 

	 Map<Long, Market> getActiveMarkets();
	 void setActiveMarkets(Map<Long, Market> activeMarkets);
	 Market getActiveMarket(Long marketId);
	 void setActiveMarket(Long marketId, Market market);
	 void updateUserMarkets();
	 Map<String, Uzer> getActiveUsers();
	 boolean add2ActiveUsers(String login, Uzer uzer);
	 void sendKeepAlive(String login);
	 void sendRequest(Action action, String login, String marketId);
	
}