package net.bir2.ejb.session.market;

import com.betfair.aping.entities.*;
import com.betfair.aping.enums.MatchProjection;
import com.betfair.aping.enums.OrderProjection;
import com.betfair.aping.enums.Wallet;
import net.bir2.ejb.action.ShedulerActivity;
import net.bir2.multitrade.ejb.entity.*;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.util.APIContext;

import javax.ejb.Local;
import java.util.List;
import java.util.Set;

@Local
public interface MarketService {

	ShedulerActivity getServiceBean();

	List<Market> listMarkets();

	List<Market> getMyActiveMarkets();
	
	List<MarketRunner> listRunners(String marketId);

	Market merge(Market market);

	Market getMarket(long id);
	Market getMarketByMarketId(String marketId);
	boolean isMarketAlreadyExistsByMarketId(String marketId);

	MarketRunner merge(MarketRunner runner);

	void persist ( MarketRunner runner );

	MarketRunner getRunner(long id);
	
    MarketRunner getRunnerBySelectionId(long marketId, long selectionId);

	void removeAllActiveMarkets();
	void remove(Market market);
	void remove(Market market, Uzer currentUser);
	void remove(MarketRunner runner);

	Uzer getCurrentUser() ;
	
	List<Uzer> getActiveUsers();
	
	Runner4User merge (Runner4User u4r);
	Market4User merge (Market4User market4User);

	void persist(Market4User market4User);

    Uzer merge (Uzer uzer);
    Feed4Market4User merge (Feed4Market4User arg);
    Feed4Runner4User merge (Feed4Runner4User arg);


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

	AccountFundsResponse  getAccountFunds  (Wallet wallet, String appKey, String ssoId );

}
