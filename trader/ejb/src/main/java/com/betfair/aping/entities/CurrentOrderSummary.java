package com.betfair.aping.entities;

import com.betfair.aping.enums.OrderStatus;
import com.betfair.aping.enums.OrderType;
import com.betfair.aping.enums.PersistenceType;
import com.betfair.aping.enums.Side;

import java.util.Date;

/**
 * Created by Eugene on 03.01.2016.
 */
public class CurrentOrderSummary {
    @Override
    public String toString() {
        return "CurrentOrderSummary{" +
                "averagePriceMatched=" + averagePriceMatched +
                ", betId='" + betId + '\'' +
                ", marketId='" + marketId + '\'' +
                ", selectionId=" + selectionId +
                ", handicap=" + handicap +
                ", priceSize=" + priceSize +
                ", bspLiability=" + bspLiability +
                ", side=" + side +
                ", status=" + status +
                ", persistenceType=" + persistenceType +
                ", orderType=" + orderType +
                ", placedDate=" + placedDate +
                ", matchedDate=" + matchedDate +
                ", sizeMatched=" + sizeMatched +
                ", sizeRemaining=" + sizeRemaining +
                ", sizeLapsed=" + sizeLapsed +
                ", sizeCancelled=" + sizeCancelled +
                ", sizeVoided=" + sizeVoided +
                ", regulatorAuthCode='" + regulatorAuthCode + '\'' +
                ", regulatorCode='" + regulatorCode + '\'' +
                '}';
    }

    public double getAveragePriceMatched() {
        return averagePriceMatched;
    }

    public void setAveragePriceMatched(double averagePriceMatched) {
        this.averagePriceMatched = averagePriceMatched;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public double getBspLiability() {
        return bspLiability;
    }

    public void setBspLiability(double bspLiability) {
        this.bspLiability = bspLiability;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public Date getMatchedDate() {
        return matchedDate;
    }

    public void setMatchedDate(Date matchedDate) {
        this.matchedDate = matchedDate;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public PersistenceType getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(PersistenceType persistenceType) {
        this.persistenceType = persistenceType;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Date placedDate) {
        this.placedDate = placedDate;
    }

    public PriceSize getPriceSize() {
        return priceSize;
    }

    public void setPriceSize(PriceSize priceSize) {
        this.priceSize = priceSize;
    }

    public String getRegulatorAuthCode() {
        return regulatorAuthCode;
    }

    public void setRegulatorAuthCode(String regulatorAuthCode) {
        this.regulatorAuthCode = regulatorAuthCode;
    }

    public String getRegulatorCode() {
        return regulatorCode;
    }

    public void setRegulatorCode(String regulatorCode) {
        this.regulatorCode = regulatorCode;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public double getSizeCancelled() {
        return sizeCancelled;
    }

    public void setSizeCancelled(double sizeCancelled) {
        this.sizeCancelled = sizeCancelled;
    }

    public double getSizeLapsed() {
        return sizeLapsed;
    }

    public void setSizeLapsed(double sizeLapsed) {
        this.sizeLapsed = sizeLapsed;
    }

    public double getSizeMatched() {
        return sizeMatched;
    }

    public void setSizeMatched(double sizeMatched) {
        this.sizeMatched = sizeMatched;
    }

    public double getSizeRemaining() {
        return sizeRemaining;
    }

    public void setSizeRemaining(double sizeRemaining) {
        this.sizeRemaining = sizeRemaining;
    }

    public double getSizeVoided() {
        return sizeVoided;
    }

    public void setSizeVoided(double sizeVoided) {
        this.sizeVoided = sizeVoided;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * The bet ID of the original place order.
     */
    private String betId;

    /**
     * The market id the order is for.
     */
    private String marketId;

    /**
     * The selection id the order is for.
     */
    long selectionId;


    /**
     * The handicap associated with the runner in case of Asian handicap markets, null otherwise.
     */
    private double handicap;


    /**
     * The price and size of the bet.
     */
    private PriceSize priceSize;

    /**
     * Not to be confused with size. This is the liability of a given BSP bet.
     */
    private double bspLiability;

    /**
     * BACK/LAY
     */

    private Side side;

    /**
     * Either EXECUTABLE (an unmatched amount remains) or EXECUTION_COMPLETE (no unmatched amount remains).
     */
    private OrderStatus status;

    /**
     * What to do with the order at turn-in-play.
     */
    private PersistenceType persistenceType;

    /**
     * BSP Order type.
     */
    private OrderType orderType;

    /**
     * The date, to the second, the bet was placed.
     */
    private Date placedDate;

    /**
     * The date, to the second, of the last matched bet fragment (where applicable)
     */
    private Date matchedDate;

    /**
     * The average price matched at. Voided match fragments are removed from this average calculation. The price is automatically adjusted in the event of non runners being declared with applicable reduction factors.
     */
    private double averagePriceMatched;

    /**
     * The current amount of this bet that was matched.
     */
    private double sizeMatched;

    /**
     * The current amount of this bet that is unmatched.
     */
    private double sizeRemaining;

    /**
     * The current amount of this bet that was lapsed.
     */
    private double sizeLapsed;

    /**
     * The current amount of this bet that was cancelled.
     */
    private double sizeCancelled;

    /**
     * The current amount of this bet that was voided.
     */
    private double sizeVoided;

    /**
     * The regulator authorisation code.
     */
    private String regulatorAuthCode;

    /**
     * The regulator Code.
     */
    private String regulatorCode;

}
