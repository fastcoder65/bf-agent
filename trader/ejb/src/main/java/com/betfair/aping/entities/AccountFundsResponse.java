package com.betfair.aping.entities;

/**
 * Created by Eugene on 04.05.2016.
 */
public class AccountFundsResponse {

    double availableToBetBalance; // Amount available to bet. ( UK & AUS )

    double  exposure; //  Current exposure.  ( UK & AUS )

    double  retainedCommission;  //  Sum of retained commission. ( UK & AUS )

    double  exposureLimit; //Exposure limit.( UK & AUS )

    double   discountRate; // User Discount Rate. ( UK only )

    int pointsBalance;  // The Betfair points balance UK only

    public double getAvailableToBetBalance() {
        return availableToBetBalance;
    }

    public void setAvailableToBetBalance(double availableToBetBalance) {
        this.availableToBetBalance = availableToBetBalance;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public double getExposureLimit() {
        return exposureLimit;
    }

    public void setExposureLimit(double exposureLimit) {
        this.exposureLimit = exposureLimit;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public double getRetainedCommission() {
        return retainedCommission;
    }

    public void setRetainedCommission(double retainedCommission) {
        this.retainedCommission = retainedCommission;
    }
}
