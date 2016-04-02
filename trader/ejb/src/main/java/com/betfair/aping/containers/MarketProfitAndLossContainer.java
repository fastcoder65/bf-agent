package com.betfair.aping.containers;

import com.betfair.aping.entities.MarketProfitAndLoss;

import java.util.List;

/**
 * Created by Eugene on 02.04.2016.
 */
public class MarketProfitAndLossContainer  extends Container {
    List<MarketProfitAndLoss> result;

    public List<MarketProfitAndLoss> getResult() {
        return result;
    }

    public void setResult(List<MarketProfitAndLoss> result) {
        this.result = result;
    }

}
