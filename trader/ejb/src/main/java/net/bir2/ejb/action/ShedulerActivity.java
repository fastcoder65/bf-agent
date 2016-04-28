package net.bir2.ejb.action;

import com.betfair.aping.entities.*;
import com.betfair.aping.enums.MatchProjection;
import com.betfair.aping.enums.OrderProjection;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir2.multitrade.util.APIContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ShedulerActivity {

	 // Life cycle method 
	 void create () throws Exception; 
	 void destroy () throws Exception; 

	 Map<String, Market> getActiveMarkets();
	 void setActiveMarkets(Map<String, Market> activeMarkets);
	 Market getActiveMarket(String marketId);
	 void setActiveMarket(String marketId, Market market);
	 void updateUserMarkets();
	 Map<String, Uzer> getActiveUsers();
	 boolean add2ActiveUsers(String login, Uzer uzer);
	 void sendKeepAlive(String login);
	 void sendRequest(Action action, String login, String marketId);
	 List<Double> getAllValidOdds();

	// GlobalAPI

	List<EventTypeResult> getActiveEventTypes(APIContext context);

	List<CompetitionResult> getCompetitions(APIContext context,  Set<String> eventTypeIds, Set<String> eventIds);

	List<EventResult> getEvents(APIContext context,
									   Set<String> eventTypeIds,
									   Set<String> competitionIds,
									   Set<String> eventIds);

	List<MarketCatalogue> getMarkets(APIContext context,
											Set<String> eventIds, Set<String> marketIds);


	List<MarketBook> getMarketPrices(APIContext context, String marketId, String currencyCode);

	List<MarketBook> getMarketStatus(APIContext context, String marketId);

	List<MarketBook> listMarketBook(APIContext context, List<String> marketIds,
										   PriceProjection priceProjection, OrderProjection orderProjection,
										   MatchProjection matchProjection, String currencyCode);

	List<MarketProfitAndLoss> listMarketProfitAndLoss (APIContext context, Set<String> marketIds);

	List<CurrentOrderSummary> listCurrentOrders(APIContext context,
													   Set<String> betIds, Set<String> marketIds);

	PlaceExecutionReport placeOrders(APIContext context, String marketId,
											List<PlaceInstruction> instructions);

	ReplaceExecutionReport replaceOrders(APIContext context, String marketId,
												List<ReplaceInstruction> instructions );

	CancelExecutionReport cancelOrders(APIContext context, String marketId, List<CancelInstruction> instructions);

		void keepAlive(APIContext context) throws Exception;

	    void login(APIContext context, String userName,	String password) throws Exception;

		void logout(APIContext context) throws Exception;

}