package com.betfair.aping.entities;

import java.util.List;

/**
 * Created by Eugene on 02.04.2016.
 */
public class MarketProfitAndLoss {

    private String marketId; // The unique identifier for the market

    private double commissionApplied; // The commission rate applied to P&L values. Only returned if netOfCommision option is requested

    public double getCommissionApplied() {
        return commissionApplied;
    }

    public void setCommissionApplied(double commissionApplied) {
        this.commissionApplied = commissionApplied;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public List<RunnerProfitAndLoss> getProfitAndLosses() {
        return profitAndLosses;
    }

    public void setProfitAndLosses(List<RunnerProfitAndLoss> profitAndLosses) {
        this.profitAndLosses = profitAndLosses;
    }

    private List<RunnerProfitAndLoss> profitAndLosses; // Calculated profit and loss data.

    @Override
    public String toString() {
        return "MarketProfitAndLoss{" +
                "commissionApplied=" + commissionApplied +
                ", marketId='" + marketId + '\'' +
                ", profitAndLosses=" + profitAndLosses +
                '}';
    }
}
