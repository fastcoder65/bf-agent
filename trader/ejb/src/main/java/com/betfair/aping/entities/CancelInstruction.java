package com.betfair.aping.entities;

/**
 * Created by Eugene on 09.01.2016.
 */
public class CancelInstruction {

    String betId;
    double sizeReduction;

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public double getSizeReduction() {
        return sizeReduction;
    }

    public void setSizeReduction(double sizeReduction) {
        this.sizeReduction = sizeReduction;
    }
}
