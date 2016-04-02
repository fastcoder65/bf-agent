package com.betfair.aping.entities;

/**
 * Created by Eugene on 02.04.2016.
 */
public class RunnerProfitAndLoss {

    private long selectionId;   // The unique identifier for the selection

    private double ifWin;       // Profit or loss for the market if this selection is the winner.

    private double ifLose;      //  Profit or loss for the market if this selection is the loser. Only returned for multi-winner odds markets.

    private double ifPlace;

    @Override
    public String toString() {
        return "RunnerProfitAndLoss{" +
                "ifLose=" + ifLose +
                ", selectionId=" + selectionId +
                ", ifWin=" + ifWin +
                ", ifPlace=" + ifPlace +
                '}';
    }

    public double getIfLose() {
        return ifLose;
    }

    public void setIfLose(double ifLose) {
        this.ifLose = ifLose;
    }

    public double getIfPlace() {
        return ifPlace;
    }

    public void setIfPlace(double ifPlace) {
        this.ifPlace = ifPlace;
    }

    public double getIfWin() {
        return ifWin;
    }

    public void setIfWin(double ifWin) {
        this.ifWin = ifWin;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

}
