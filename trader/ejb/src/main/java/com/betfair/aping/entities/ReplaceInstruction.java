package com.betfair.aping.entities;

/**
 * Created by Eugene on 10.01.2016.
 */
public class ReplaceInstruction {

    String betId; // Unique identifier for the bet

    double newPrice; //  The price to replace the bet at

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }
}
