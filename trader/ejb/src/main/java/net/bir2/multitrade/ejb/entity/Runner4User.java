package net.bir2.multitrade.ejb.entity;

import net.bir2.ejb.session.market.BaseServiceBean;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@IdClass(Runner4UserId.class)
public class Runner4User implements java.io.Serializable {

    @Transient
    private static final Logger log = Logger.getLogger(Runner4User.class.getName());

    private static final long serialVersionUID;

    static {
        serialVersionUID = 1L;
    }

    @Id
    @Column(name = "user_id", insertable = false, updatable = false)
    private int userId;

    public int getUserId() {
        return userId;
    }

    @Id
    @Column(name = "runner_id", insertable = false, updatable = false)
    private long runnerId;

    public long getRunnerId() {
        return runnerId;
    }

    public Runner4User() {
    }

    public Runner4User(Uzer uzer, MarketRunner runner) {
        this.userId = uzer.getId();
        this.linkedUser = uzer;
        this.runnerId = runner.getId();
        this.linkedRunner = runner;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "user_id")
    private Uzer linkedUser;

    public Uzer getLinkedUser() {
        return linkedUser;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "runner_id")
    private MarketRunner linkedRunner;

    public MarketRunner getLinkedRunner() {
        return linkedRunner;
    }

    private Double odds;

    public Double getOdds() {
        return odds;
    }

    public void setOdds(Double odds) {
        this.odds = odds;
    }

    public Double getSelectionPrice() {
        return selectionPrice;
    }

    public void setSelectionPrice(Double selectionPrice) {
        this.selectionPrice = selectionPrice;
    }

    public Double getSelectionAmount() {
        return selectionAmount;
    }

    public void setSelectionAmount(Double selectionAmount) {
        this.selectionAmount = selectionAmount;
    }

    private Double selectionPrice;

    private Double selectionAmount;

    private Double profitLoss;

    public Double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Double profitLoss) {
        this.profitLoss = profitLoss;
    }

    public String getProfitColor() {
        return (this.profitLoss < 0 ? "red" : "green");
    }

    //@Formula(" select case when r4u.odds > 0 then (1/r4u.odds) else 1 end from Runner4User r4u  where r4u.user_id = user_id  and r4u.runner_Id = runner_id)")

// (1/r4u.odds)

    @Formula("( select case when r4u.odds > 0 then 1/r4u.odds else 1 end from Runner4User r4u where r4u.user_id = user_id  and r4u.runner_Id = runner_id )")
    private Double _returnPercent;

    public Double getReturnPercent() {
        log.fine("getReturnPercent() - _returnPercent: " + _returnPercent);
        return _returnPercent;
    }

    /*
    @Formula("(select coalesce(1/r4u.odds, 0) from Runner4User r4u  where r4u.user_id = userId  and r4u.runner_Id=runnerId)")
    public Double getReturnPercent() {
        return _returnPercent;
    }
*/
    @Transient
    public Double getPrcWin() {
        Double _sumReturnPercent = linkedRunner.getMarket()
                .getUserData4Market().get(this.userId).getSumReturnPercent();
        printLog("*** _sumReturnPercent= " + _sumReturnPercent);
        Double result = ((this.odds != null && this.odds > 0) ? _sumReturnPercent / this.odds : 0.0);

        printLog("* getPrcWin()= " + result);
        return result;
    }

    @Transient
    public Double getPseudoStake() {
        Double _psv = linkedRunner.getMarket().getUserData4Market().get( this.userId ).getPseudoStakeVolume();
        printLog("* Pseudo Stake Volume= " + _psv);
        Double result = _psv * getPrcWin();
        printLog("* getPseudoStake()= " + result);
        return result;
    }

    @Transient
    public Double getPinkStakeReturn() {
        Double result = ((getBackPrice1() == 0.0) ? 1 / 1000.0 : 1 / getBackPrice1());
        printLog("* getPinkStakeReturn()= " + result);
        return result;
    }

    @Transient
    public Double getPrcWinPinkStakes() {

        Double _sumPercentWinPink = linkedRunner.getMarket()
                .getUserData4Market().get(this.userId).getSumPercentWinPink();
        printLog("getPrcWinPinkStakes(): _sumPercentWinPink=" + _sumPercentWinPink);

        Double price1b = getBackPrice1(); // getLinkedRunner().getBackPrices().get(1).getPrice();

        Double _prcWinPink = (price1b == null || price1b == 0.0 ? _sumPercentWinPink / 1000.0
                : _sumPercentWinPink / price1b);

        printLog("getPrcWinPinkStakes(): _prcWinPink=" + _prcWinPink);
        return _prcWinPink;
    }

    @Transient
    public Double getPseudoPinkStake() {
        Double _pseudoPinkStakeVolume = linkedRunner.getMarket()
                .getUserData4Market().get(this.userId)
                .getPseudoPinkStakeVolume();

        printLog("_pseudoPinkStakeVolume=" + _pseudoPinkStakeVolume);
        Double result = _pseudoPinkStakeVolume * getPrcWinPinkStakes();
        printLog("getPseudoPinkStake result=" + result);
        return result;
    }

    @Transient
    private static void printLog(String logMessage) {
        if (log.isLoggable(Level.FINE)) {
            log.fine(logMessage);
        }
    }

    @Transient
    private static void printLog(Level level, String logMessage) {
        if (log.isLoggable(level)) {
            log.log(level, logMessage);
        }
    }

    @Transient
    public Double getSumPseudoRealPseudoPinkStakes() {
        return getPseudoStake() + getMatchedLayAmount()
                + getPseudoPinkStake();
    }

    private boolean isNonRunner;

    public boolean getIsNonRunner() {
        return isNonRunner;
    }

    public void setIsNonRunner(boolean isNonRunner) {
        if (this.isNonRunner != isNonRunner) {
          if (isNonRunner)
            log.fine("isNonRunner is set for " + this.getLinkedRunner().getName()+ "!");
        }
        this.isNonRunner = isNonRunner;
    }


    @Transient
    public boolean getIsNonRunner0() {
        boolean result;
        result = (this.getBackPrice1() == 0 && this.getBackPrice2() == 0
                && this.getBackPrice3() == 0 && this.getLayPrice1() == 0
                && this.getLayPrice2() == 0 && this.getLayPrice3() == 0);


        if (result) {
            if (this.linkedRunner != null) {
                String _sName = (this.linkedRunner != null && this.linkedRunner.getName() != null ? this.linkedRunner.getName() : " null name!");
                String s = new StringBuilder().append(" getIsNonRunner(): ").append(_sName).append(" is 'NonRunner' now!").toString();
                printLog(Level.FINE, s);
                //log.info(new StringBuilder().append(" getIsNonRunner(): ").append(_sName).append(" is 'NonRunner' now!").toString());
            }
        }
        return result;
    }

    @Transient
    public Double getSumPseudoRealPseudoPinkStakesHoldWithNR() {
        Double result = (getIsNonRunner() ? 0 : getSumPseudoRealPseudoPinkStakes());
        printLog("log: " + log);
        printLog("getSumPseudoRealPseudoPinkStakesHoldWithNR=" + result);
        return result;
    }

    @Transient
    public Double getPercWinSglajivWithNR() {
        Double _x;
        Map<Integer, Market4User> udm =  linkedRunner.getMarket().getUserData4Market();
        Market4User m4u = udm.get(this.userId);
        _x = (m4u != null ? m4u.getSummOfPsRealStPinkStakeHoldWithNR():null);

        Double result = (_x!= null && _x > 0) ? getSumPseudoRealPseudoPinkStakesHoldWithNR() / _x : 0.0;
        printLog("getPercWinSglajivWithNR=" + result);
        return result;
    }

    private Double oddsCosmetic;

    public Double getOddsCosmetic() {
        oddsCosmetic = (oddsCosmetic == null ? BaseServiceBean.FAKE_ODDS
                : oddsCosmetic);
        return oddsCosmetic;
    }

    public void setOddsCosmetic(Double oddsCosmetic) {
        this.oddsCosmetic = oddsCosmetic;
    }

    private Double oddsPrecosmetic;

    public Double getOddsPrecosmetic() {
        oddsPrecosmetic = (oddsPrecosmetic == null ? BaseServiceBean.FAKE_ODDS
                : oddsPrecosmetic);
        return oddsPrecosmetic;
    }

    public void setOddsPrecosmetic(Double oddsPrecosmetic) {
        this.oddsPrecosmetic = oddsPrecosmetic;
    }

    public Double getBackPrice1() {
        if (backPrice1 == null)
            backPrice1 = 0.0;
        return backPrice1;
    }

    public void setBackPrice1(Double backPrice1) {
        this.backPrice1 = backPrice1;
    }

    public Double getBackAmount1() {
        if (backAmount1 == null)
            backAmount1 = 0.0;
        return backAmount1;
    }

    public void setBackAmount1(Double backAmount1) {
        this.backAmount1 = backAmount1;
    }

    public Double getBackPrice2() {
        if (backPrice2 == null)
            backPrice2 = 0.0;

        return backPrice2;
    }

    public void setBackPrice2(Double backPrice2) {
        this.backPrice2 = backPrice2;
    }

    public Double getBackAmount2() {
        if (backAmount2 == null)
            backAmount2 = 0.0;

        return backAmount2;
    }

    public void setBackAmount2(Double backAmount2) {
        this.backAmount2 = backAmount2;
    }

    public Double getBackPrice3() {
        if (backPrice3 == null)
            backPrice3 = 0.0;

        return backPrice3;
    }

    public void setBackPrice3(Double backPrice3) {
        this.backPrice3 = backPrice3;
    }

    public Double getBackAmount3() {
        if (backAmount3 == null)
            backAmount3 = 0.0;

        return backAmount3;
    }

    public void setBackAmount3(Double backAmount3) {
        this.backAmount3 = backAmount3;
    }

    public Double getLayPrice1() {
        if (layPrice1 == null)
            layPrice1 = 0.0;
        return layPrice1;
    }

    public void setLayPrice1(Double layPrice1) {
        this.layPrice1 = layPrice1;
    }

    public Double getLayAmount1() {
        if (layAmount1 == null)
            layAmount1 = 0.0;
        return layAmount1;
    }

    public void setLayAmount1(Double layAmount1) {
        this.layAmount1 = layAmount1;
    }

    public Double getLayPrice2() {
        if (layPrice2 == null)
            layPrice2 = 0.0;

        return layPrice2;
    }

    public void setLayPrice2(Double layPrice2) {
        this.layPrice2 = layPrice2;
    }

    public Double getLayAmount2() {
        if (layAmount2 == null)
            layAmount2 = 0.0;

        return layAmount2;
    }

    public void setLayAmount2(Double layAmount2) {
        this.layAmount2 = layAmount2;
    }

    public Double getLayPrice3() {
        if (layPrice3 == null)
            layPrice3 = 0.0;
        return layPrice3;
    }

    public void setLayPrice3(Double layPrice3) {
        this.layPrice3 = layPrice3;
    }

    public Double getLayAmount3() {
        if (layAmount3 == null)
            layAmount3 = 0.0;
        return layAmount3;
    }

    public void setLayAmount3(Double layAmount3) {
        this.layAmount3 = layAmount3;
    }

    private Double backPrice1;
    private Double backAmount1;

    private Double backPrice2;
    private Double backAmount2;

    private Double backPrice3;
    private Double backAmount3;

    private Double layPrice1;
    private Double layAmount1;

    private Double layPrice2;
    private Double layAmount2;

    private Double layPrice3;
    private Double layAmount3;

    public Double getMatchedLayPrice() {
        return matchedLayPrice;
    }

    public void setMatchedLayPrice(Double matchedLayPrice) {
        this.matchedLayPrice = matchedLayPrice;
    }

    public Double getMatchedLayAmount() {
        Double result = matchedLayAmount == null ? 0.0 : matchedLayAmount;
        printLog("matchedLayAmount=" + result);
        return result;
    }

    public void setMatchedLayAmount(Double matchedLayAmount) {
        this.matchedLayAmount = matchedLayAmount;
    }

    public Double getUnmatchedLayPrice() {
        return unmatchedLayPrice;
    }

    public void setUnmatchedLayPrice(Double unmatchedLayPrice) {
        this.unmatchedLayPrice = unmatchedLayPrice;
    }

    public Double getUnmatchedLayAmount() {
        return unmatchedLayAmount;
    }

    public void setUnmatchedLayAmount(Double unmatchedLayAmount) {
        this.unmatchedLayAmount = unmatchedLayAmount;
    }

    private Double matchedLayPrice;
    private Double matchedLayAmount;

    private Double unmatchedLayPrice;
    private Double unmatchedLayAmount;

    private String returnedMessage;

    public String getReturnedMessage() {
        return returnedMessage;
    }

    public void setReturnedMessage(String returnedMessage) {
        this.returnedMessage = returnedMessage;
    }


    @Override
    public String toString() {
        return "Runner4User ["// _returnPercent=" + _returnPercent
                + " matchedLayAmount=" + matchedLayAmount
                + ", matchedLayPrice=" + matchedLayPrice + ", odds=" + odds
                + ", oddsCosmetic=" + oddsCosmetic + ", oddsPrecosmetic="
                + oddsPrecosmetic + ", profitLoss=" + profitLoss
                + ", returnedMessage=" + returnedMessage + ", selectionAmount="
                + selectionAmount + ", selectionPrice=" + selectionPrice
                + ", unmatchedLayAmount=" + unmatchedLayAmount
                + ", unmatchedLayPrice=" + unmatchedLayPrice + ']';
    }

    public String toStringShort() {
        return "Runner4User [" + ", runnerId=" + runnerId + ", userId="
                + userId + ']';
    }

    private String currency = "";

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getTotalAmountMatched() {
        return totalAmountMatched;
    }

    public void setTotalAmountMatched(Double totalAmountMatched) {
        this.totalAmountMatched = totalAmountMatched;
    }

    public Double getLastPriceMatched() {
        return lastPriceMatched;
    }

    public void setLastPriceMatched(Double lastPriceMatched) {
        this.lastPriceMatched = lastPriceMatched;
    }

    private Double totalAmountMatched;
    private Double lastPriceMatched;

}
