package net.bir2.ejb.session.market;

import java.util.List;

import javax.ejb.Local;

import net.bir2.multitrade.ejb.entity.Feed4Market4User;
import net.bir2.multitrade.ejb.entity.Feed4Runner4User;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Market4User;
import net.bir2.multitrade.ejb.entity.Runner;

import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir2.multitrade.ejb.entity.Runner4User;

@Local
public interface MarketService {

	List<Market> listMarkets();

	List<Market> getMyActiveMarkets();
	
	List<Runner> listRunners(String marketId);

	Market merge(Market market);

	Market getMarket(long id);
	Market getMarketByMarketId(String marketId);
	boolean isMarketAlreadyExistsByMarketId(String marketId);

	Runner merge(Runner runner);

	Runner getRunner(long id);
	
    Runner getRunnerBySelectionId(long marketId, long selectionId);

	void removeAllActiveMarkets();
	void remove(Market market);
	void remove(Market market, Uzer currentUser);
	void remove(Runner runner);
	

	
	Uzer getCurrentUser() ;
	
	List<Uzer> getActiveUsers();
	
	Runner4User merge (Runner4User u4r);
	Market4User merge (Market4User market4User);

    Uzer merge (Uzer uzer);
    Feed4Market4User merge (Feed4Market4User arg);
    Feed4Runner4User merge (Feed4Runner4User arg);
}
