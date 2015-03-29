package net.bir2.ejb.session.market;

import javax.ejb.Local;

import net.bir2.ejb.action.Action;

@Local
public interface BaseService {

 Double getUpOdds(Double cOdds);
 Double getDownOdds(Double cOdds);
 Double getNearestOdds(Double rawOdds);
 Double getCosmeticOdds (Double blueOdds1, Double blueAmount1, Double blueOdds2, Double pinkOdds, Double precosmOdds, Double myOdds, Double myAmount);
 void sendDelayedRequest(Action action, String userLogin, String sMarketId, int index);
 Double getSelectionPrice ( Double finalOdds, Double sourceOdds, Double volumeStake, Double maxLoss, Double profitLoss, Integer inplayDelay, String marketStatus, Boolean isNonRunner);
 Double getSelectionAmount (Double finalOdds, Double sourceOdds, Double volumeStake, String marketStatus);
 String getCurrencySymbol(String currencyAlias);
 boolean isEqual(Double arg1, Double arg2);
}

