package com.betfair.aping.entities;

import java.util.List;

/**
 * Created by Eugene on 03.01.2016.
 */
public class CurrentOrderSummaryReport {

    private boolean moreAvailable;
    private List<CurrentOrderSummary> currentOrders;

    public List<CurrentOrderSummary> getCurrentOrders() {
        return currentOrders;
    }

    public void setCurrentOrders(List<CurrentOrderSummary> currentOrders) {
        this.currentOrders = currentOrders;
    }


    public boolean isMoreAvailable() {
        return moreAvailable;
    }

    public void setMoreAvailable(boolean moreAvailable) {
        this.moreAvailable = moreAvailable;
    }

    @Override
    public String toString() {
        return "CurrentOrderSummaryReport{" +
                "currentOrders=" + currentOrders +
                ", moreAvailable=" + moreAvailable +
                '}';
    }
}
