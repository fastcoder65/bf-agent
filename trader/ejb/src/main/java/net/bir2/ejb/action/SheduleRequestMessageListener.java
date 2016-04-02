package net.bir2.ejb.action;

import com.betfair.aping.entities.*;
import com.betfair.aping.enums.OrderStatus;
import com.betfair.aping.enums.OrderType;
import com.betfair.aping.enums.PersistenceType;
import com.betfair.aping.enums.Side;
import com.unitab.race.Race;
import generated.exchange.BFExchangeServiceStub.MarketLite;
import net.bir2.ejb.session.market.BaseService;
import net.bir2.ejb.session.market.BaseServiceBean;
import net.bir2.ejb.session.market.DataFeedService;
import net.bir2.ejb.session.market.MarketService;
import net.bir2.handler.GlobalAPI;
import net.bir2.multitrade.ejb.entity.*;
import net.bir2.multitrade.ejb.entity.Market;

import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/queue/PriceRequestQueue")})
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SheduleRequestMessageListener implements MessageListener {

    private static final char BS = '\\';
    /*
        protected static final Logger LOG = Logger
                .getLogger(SheduleRequestMessageListener.class);
    */
    public static final String ACTION_PROPERTY = "action";
    public static final String MARKET_ID_PROPERTY = "market_id";
    public static final String LOGIN_PROPERTY = "login";
    private static final long MIN_REFRESH_INT = 2;

    @Inject
    private Logger log;

    private void printLog(String logMessage) {
        if (log.isLoggable(Level.FINE)) {
            log.fine(logMessage);
        }
    }

    private void printLog(Level level, String logMessage) {
        if (log.isLoggable(level)) {
            log.log(level, logMessage);
        }
    }

    private void printLog(Level level, String logMessage, Throwable t) {
        if (log.isLoggable(level)) {
            log.log(level, logMessage, t);
        }
    }

    private void logError(String logMessage, Throwable t) {
        printLog(Level.SEVERE, logMessage, t);
    }

    private void logWarn(String logMessage) {
        printLog(Level.WARNING, logMessage);
    }

    private void logWarn(String logMessage, Throwable t) {
        printLog(Level.WARNING, logMessage, t);
    }

    private void logInfo(String logMessage) {
        printLog(Level.INFO, logMessage);
    }


    @EJB
    private BaseService baseService;

    @EJB
    ShedulerActivity serviceBean;

    public BaseService getBaseService() {
        return baseService;
    }

    @EJB
    private DataFeedService dataFeedService;

    public DataFeedService getDataFeedService() {
        return dataFeedService;
    }

    @EJB
    protected MarketService marketService;


    public void onMessage(Message msg) {

        if (msg == null || !(msg instanceof MapMessage))
            return;

        try {

            MapMessage message = (MapMessage) msg;
            if (message.getString(ACTION_PROPERTY) != null) {
                processActionRequest(message);
            }
        } catch (Exception e) {
            logError("error on Message..", e);
        }
    }

    private void actionUpdateUserMarkets() {
        serviceBean.updateUserMarkets();
    }

	/*
     * private void doUpdateMarketStatus(String login, Long marketId) { String
	 * eventId = new StringBuffer().append(
	 * Action.UPDATE_MARKET.toString()).append("=").append(login)
	 * .append(ITEM_SEPARATOR).append(marketId).toString(); long duration =
	 * 100L; log.info("duration: " + duration / 1000.0); createTimer(context,
	 * eventId, duration);
	 * 
	 * }
	 */

    private void doKeepAliveRequests() {

        printLog("* send keep-alive request to all active users..");

        if (marketService == null) {
            logWarn("*** !!!  marketService is null!!! ***");

        } else {
            for (Uzer uzer : marketService.getActiveUsers()) {
                if (!serviceBean.getActiveUsers().containsKey(uzer.getLogin())
                        || serviceBean.getActiveUsers().get(uzer.getLogin()).getMarket4Users().size() != uzer.getMarket4Users().size()) {

                    serviceBean.sendKeepAlive(uzer.getLogin());
                }
            }
        }

    }

    public static final String UNKNOWN = "UNKNOWN";

    private void actionUpdateMarketStatus(String login, String marketId) {

        printLog(new StringBuilder(100)
                .append("actionUpdateMarketStatus: login=").append(login)
                .append(", marketId=").append(marketId).toString());

        // int lastDigit = (int) (System.currentTimeMillis() % 10L);

        // ShedulerActivity serviceBean = ShedulerActivityBean.getInstance();

        //	Exchange selected_exchange;

        Market currentMarket = null;
        if (marketService.isMarketAlreadyExistsByMarketId(marketId)) {
            try {
                currentMarket = serviceBean.getActiveMarket(marketId);
            } catch (Exception e) {
                logError("ERROR READING CURRENT MARKET FOR STATUS RENEW , MSG ", e);
            }
        } else {
            serviceBean.getActiveMarkets().remove(marketId);
            return;
        }

        Uzer currentUser = serviceBean.getActiveUsers().get(login);

        if (currentMarket == null) {
            logWarn("currentMarket is not found for market_id:" + marketId);
            return;
        }

        if (currentUser == null) {
            logWarn("currentUser is not found for login:" + login);
            return;
        }

        Calendar current = Calendar.getInstance();

        long km = Math.abs(Math
                .round((currentMarket.getMarketTime().getTime() - current
                        .getTimeInMillis()) / (1000.0 * 60.0)));

        long refreshInt = Math.round(Math.sqrt((double) km));

        refreshInt = (refreshInt < MIN_REFRESH_INT ? MIN_REFRESH_INT
                : refreshInt);

        boolean needUpdate = ((Math.round(current.getTimeInMillis() / 1000.0) % refreshInt) == 0);
        if (!needUpdate)
            return;

        logInfo("currentMarket: " + currentMarket.getMenuPath() + BS
                + currentMarket.getName() + ", minutes to start: " + km
                + ", refreshInt(second(s)):" + refreshInt);

        // selected_exchange = currentMarket.getExchange() == 1 ? Exchange.UK: Exchange.AUS;

        String marketStatus;
        long startTime = System.currentTimeMillis();
        try {

            MarketLite marketLite = null; // ExchangeAPI.getMarketInfo(selected_exchange, currentUser.getApiContext(), Long.valueOf(currentMarket.getMarketId()).intValue());

            long endTime = System.currentTimeMillis();

            printLog("Update Market Status completed, login=" + login
                    + ", marketId=" + marketId + ", refreshInt:"
                    + refreshInt + " second(s), time consumed: "
                    + ((endTime - startTime) / 1000.0) + " second(s)");

            marketStatus = marketLite.getMarketStatus().toString();

            if (!UNKNOWN.equals(marketStatus)) {
                currentMarket.setMarketStatus(marketStatus);
                currentMarket = marketService.merge(currentMarket);
            }

        } catch (Exception e) {
            if (e.getMessage() != null && !e.getMessage().contains("EXCEEDED_THROTTLE"))
                logError("UpdateMarketStatus error, message: ", e);
        }


        if (!"CLOSED".equals(currentMarket.getMarketStatus())) {
            baseService.sendDelayedRequest(Action.UPDATE_MARKET_PRICES, login,
                    marketId.toString(), 1);
        } else {
            marketService.remove(currentMarket, currentUser);
        }

    }

    private void actionUpdateMarketPrices(String login, String marketId) {

        //	Exchange selected_exchange;

        Market currentMarket = null;
        try {

            currentMarket = serviceBean.getActiveMarket(marketId);
            if (currentMarket != null) {
                currentMarket = marketService.getMarket(currentMarket.getId());

                serviceBean.setActiveMarket(currentMarket.getMarketId(),
                        currentMarket);
            }
        } catch (Exception e) {
            logError("ERROR GETTING CURRENT MARKET , MSG ", e);
        }

        if (currentMarket == null) {
            logWarn("currentMarket is not found for market_id:" + marketId);
            return;
        }

        Uzer currentUser = serviceBean.getActiveUsers().get(login);

        if (currentUser == null) {
            logWarn("currentUser is not found for login:" + login);
            return;
        }

        Market4User market4User = currentMarket.getUserData4Market().get(
                currentUser.getId());

        logInfo("given market4User: " + market4User);

        boolean isOnAir = market4User.isOnAir();


        List<CurrentOrderSummary> curBets = new ArrayList<CurrentOrderSummary>();
        List<CurrentOrderSummary> currentBets = null;
        List<MarketProfitAndLoss> mProfitAndLosses = null;
        Map<Long, RunnerProfitAndLoss> rProfitAndLosses = new HashMap<Long, RunnerProfitAndLoss>();

        Set<String> marketIds = new HashSet<String>();
        long startTime = System.currentTimeMillis();
        marketIds.add(currentMarket.getMarketId());

        try {

            currentBets = GlobalAPI.listCurrentOrders(currentUser.getApiContext(), null, marketIds);

            logInfo("??? on market " + currentMarket + ", found currentBets count: " + (currentBets == null ? "0" : "" + currentBets.size()));

        } catch (Exception e) {
            logError(" 'Get Current Bets' error, message: ", e);
        }

        long endTime = System.currentTimeMillis();

        logInfo("action 'Get Current Bets' COMPLETED, login="
                + currentUser.getLogin() + ", marketId="
                + currentMarket.getMarketId() + ", time consumed: "
                + ((endTime - startTime) / 1000.0) + " second(s)");



        if (currentBets != null && currentBets.size() > 0) {

            marketIds = new HashSet<String>();
            startTime = System.currentTimeMillis();
            marketIds.add(currentMarket.getMarketId());

            try {

                mProfitAndLosses = GlobalAPI.listMarketProfitAndLoss(currentUser.getApiContext(), marketIds);
                Iterator i = mProfitAndLosses.iterator();
                MarketProfitAndLoss mpl = i.hasNext() ? (MarketProfitAndLoss) i.next() : null;
                if (mpl != null) {
                    for (RunnerProfitAndLoss rpl : mpl.getProfitAndLosses()) {
                        //     logInfo("filling 'profit and loss'- rpl.getSelectionId()= " + rpl.getSelectionId());
                        rProfitAndLosses.put(rpl.getSelectionId(), rpl);
                    }
                }
            } catch (Exception e) {
                logError(" 'listMarketProfitAndLoss' error, message: ", e);
            }

            endTime = System.currentTimeMillis();

            logInfo("action 'listMarketProfitAndLoss' COMPLETED, login="
                    + currentUser.getLogin() + ", marketId="
                    + currentMarket.getMarketId() + ", time consumed: "
                    + ((endTime - startTime) / 1000.0) + " second(s)");
        }

        if (currentBets != null) {

            for (CurrentOrderSummary bet : currentBets) {

                MarketRunner runner = currentMarket.getRunnersMap().get(bet.getSelectionId());

                if (runner == null) {
                    logWarn(new StringBuilder(100)
                            .append("runner is null for selectionId=")
                            .append(bet.getSelectionId()).toString());
                }

                if (runner != null) {

                    Runner4User r4u = runner.getUserData4Runner().get(
                            currentUser.getId());

                    if (r4u == null) {
                        logWarn(new StringBuilder(100)
                                .append("runner4user is null for userId=")
                                .append(currentUser.getId()).toString());
                    }

                    if (r4u != null) {

                        if (OrderStatus.EXECUTION_COMPLETE.equals(bet.getStatus()) // BetStatusEnum.M.equals
                                && Side.LAY.equals(bet.getSide())) { // BetTypeEnum.L

                            r4u.setMatchedLayPrice(bet.getPriceSize().getPrice());
                            r4u.setMatchedLayAmount(bet.getPriceSize().getSize());

                        }

                        marketService.merge(r4u);
                    }
                }
            }

            for (CurrentOrderSummary bet : currentBets) {

                MarketRunner runner = currentMarket.getRunnersMap().get(bet.getSelectionId());

                if (runner == null) {
                    logWarn(new StringBuilder(100)
                            .append("runner is null for selectionId=")
                            .append(bet.getSelectionId()).toString());
                }

                if (runner != null) {

                    Runner4User r4u = runner.getUserData4Runner().get(
                            currentUser.getId());

                    if (r4u == null) {
                        logWarn(new StringBuilder(100)
                                .append("runner4user is null for userId=")
                                .append(currentUser.getId()).toString());
                    }

                    if (r4u != null) {


                        if (OrderStatus.EXECUTABLE.equals(bet.getStatus()) // if (BetStatusEnum.U.equals
                                && Side.LAY.equals(bet.getSide())) { // BetTypeEnum.L

                            r4u.setUnmatchedLayPrice(bet.getPriceSize().getPrice());
                            r4u.setUnmatchedLayAmount(bet.getPriceSize().getSize() - r4u.getMatchedLayAmount());

                            curBets.add(bet);
                        }

                        marketService.merge(r4u);
                    }
                }
            }

        }
/*
			Double sum = null;

			if (currentBets != null)
			for (CurrentOrderSummary currentBet : currentBets) {
				if (OrderStatus.EXECUTION_COMPLETE.equals(currentBet.getStatus())) { // BetStatusEnum.M.equals

					if (Side.BACK.equals(currentBet.getSide())) { // == BetTypeEnum.B

						if (sum == null) {
							sum = currentBet.getPriceSize().getSize();
						} else {
							sum += currentBet.getPriceSize().getSize();
						}

						// sum += currentBets[i].getSize();
					} else {

						if (sum == null) {
							sum = -currentBet.getPriceSize().getSize();
						} else {
							sum -= currentBet.getPriceSize().getSize();
						}

						// sum -= currentBets[i].getSize();
					}
				}
			}
		*/
/*
			printLog("sum=" + sum);

			Map<Long, Double> profitMap = new HashMap<Long, Double>(10);

			if (currentBets != null && currentBets.size() > 0) {
				for (int i = 0; i < currentBets.size(); i++) {
					Long _key = currentBets.get(i).getSelectionId();

					Double _profit = null;
					if (OrderStatus.EXECUTION_COMPLETE.equals(currentBets.get(i).getStatus())) { // BetStatusEnum.M.

						double _profitItem = currentBets.get(i).getPriceSize().getSize()
								* currentBets.get(i).getPriceSize().getPrice();

						printLog(new StringBuilder(100)
									.append("_profitItem=").append(_profitItem)
									.toString());

						if (Side.BACK.equals(currentBets.get(i).getSide())) { //  == BetTypeEnum.B

							_profit = (_profit == null) ? new Double(
									_profitItem) : _profit + _profitItem;

						} else {
							_profit = (_profit == null) ? new Double(
									-_profitItem) : _profit - _profitItem;
						}
					}

					if (_profit != null) {
						profitMap.put(_key, _profit);
						printLog(new StringBuilder(100).append("_profit=").append(_profit).toString());
					}
				}
			}
*/

        boolean existMatchedBets = false;

        for (CurrentOrderSummary mbet : currentBets) {
            if (OrderStatus.EXECUTION_COMPLETE.equals(mbet.getStatus()) // if (BetStatusEnum.U.equals
                    && Side.LAY.equals(mbet.getSide())) { // BetTypeEnum.L
                existMatchedBets = true;
                break;
            }
        }

        for (MarketRunner runner : currentMarket.getRunners()) {

            Runner4User r4u = runner.getUserData4Runner().get(currentUser.getId());

//				Long _key = runner.getSelectionId();

  //          logInfo("reading 'profit and loss' - runner.getSelectionId()= "+ runner.getSelectionId());

            RunnerProfitAndLoss rpl = rProfitAndLosses.get(runner.getSelectionId());

            Double _profit = null;

            if (rpl != null && existMatchedBets) {
                _profit =  Double.valueOf(rpl.getIfWin() + rpl.getIfLose());
            }

   //         logInfo(new StringBuilder(100).append("_profit=").append(_profit).toString());
            r4u.setProfitLoss(_profit);
            marketService.merge(r4u);


/*
				Double _profit = profitMap.get(_key);

				if (sum != null) {
					if (_profit == null) {
						_profit = -sum;
					} else {
						_profit -= sum;
					}
				}

				if (_profit != null) {
					printLog(new StringBuilder(100).append("_profit=").append(_profit).toString());
					r4u.setProfitLoss(_profit);
					marketService.merge(r4u);
				}
*/
        }

        if (true) {

            logInfo("*** Update market prices for login: " + login + ", market: " + marketId);

            List<MarketBook> marketBooks = null;
            int inPlayDelay = 0;
            startTime = System.currentTimeMillis();
            String currency = "USD";
            MarketBook marketBook0 = null;

            try {

                marketBooks = GlobalAPI.getMarketPrices(currentUser.getApiContext(), currentMarket.getMarketId(), "USD");

                marketBook0 = marketBooks.get(0);

                inPlayDelay = marketBook0.getBetDelay();

                printLog("inPlayDelay =" + inPlayDelay);

            } catch (Exception e) {
                logError("getPrices() error: ", e);
            }

            endTime = System.currentTimeMillis();

            logInfo("actionUpdateMarketPrices COMPLETED, login=" + login
                    + ", marketId=" + marketId + ", time consumed: "
                    + ((endTime - startTime) / 1000.0) + " second(s)");

            printLog(Level.FINE, "currentMarket: name=" + currentMarket.getName()
                    + ", runners count="
                    + currentMarket.getRunners().size()
                    + ", runnersMap size="
                    + currentMarket.getRunnersMap().size());

            Map<Long, Runner> inflatedRunners = new HashMap<Long, Runner>();

            if (marketBook0 != null) {
                for (Runner r : marketBook0.getRunners()) {
                    inflatedRunners.put(r.getSelectionId(), r);
                }
            }

            printLog("currentUser: " + currentUser);

            for (MarketRunner runner : currentMarket.getRunners()) {

                printLog("iterate next runner: " + runner);

                Runner4User r4u = runner.getUserData4Runner().get(currentUser.getId());

                printLog("r4u: " + r4u);

                if (r4u == null) continue;

                Long _key = runner.getSelectionId();

                Runner r = inflatedRunners.get(_key);

                if (baseService != null) {
                    printLog("baseService: " + baseService + ", currency: " + currency);
                    r4u.setCurrency(baseService.getCurrencySymbol(currency));

                } else
                    printLog("baseService is NULL!!!");


                r4u.setBackPrice1(0.0);
                r4u.setBackAmount1(0.0);

                r4u.setBackPrice2(0.0);
                r4u.setBackAmount2(0.0);

                r4u.setBackPrice3(0.0);
                r4u.setBackAmount3(0.0);

                r4u.setLayAmount1(0.0);
                r4u.setLayPrice1(0.0);

                r4u.setLayAmount2(0.0);
                r4u.setLayPrice2(0.0);

                r4u.setLayAmount3(0.0);
                r4u.setLayPrice3(0.0);

                if (currentBets == null || currentBets.size() == 0) {
                    r4u.setUnmatchedLayPrice(0.0);
                    r4u.setUnmatchedLayAmount(0.0);
                }

                r4u.setTotalAmountMatched(0.0);
                r4u.setLastPriceMatched(0.0);


                r4u = marketService.merge(r4u);

                if (r != null) {

                    //if (r.getLayPrices() != null) {
                    if (r.getEx().getAvailableToLay() != null && !(r.getEx().getAvailableToLay().size() < 3)) {

                        printLog(new StringBuilder(100)
                                .append("r.getEx().getAvailableToLay().size()= ")
                                .append(r.getEx().getAvailableToLay().size()).toString());
                        //	for (InflatedPrice p : r.getLayPrices()) {
                        int priceDepth = 0;

                        for (PriceSize _priceSize : r.getEx().getAvailableToLay()) {

                            priceDepth++;

//							if ("B".equals(p.getType())) {

                            switch (priceDepth) {
                                case 1:

                                    r4u.setLayAmount1(_priceSize.getSize());
                                    r4u.setLayPrice1(_priceSize.getPrice());

                                    printLog(new StringBuilder(100)
                                            .append("r4u.getLayPrice1()= ")
                                            .append(r4u.getLayPrice1())
                                            .append(",\t r4u.getLayAmount1()= ")
                                            .append(r4u.getLayAmount1())
                                            .toString());
                                    break;
                                case 2:

                                    r4u.setLayAmount2(_priceSize.getSize());
                                    r4u.setLayPrice2(_priceSize.getPrice());

                                    printLog(new StringBuilder(100)
                                            .append("r4u.getLayPrice2()= ")
                                            .append(r4u.getLayPrice2())
                                            .append(",\t r4u.getLayAmount2()= ")
                                            .append(r4u.getLayAmount2())
                                            .toString());
                                    break;
                                case 3:

                                    r4u.setLayAmount3(_priceSize.getSize());
                                    r4u.setLayPrice3(_priceSize.getPrice());

                                    printLog(new StringBuilder(100)
                                            .append("r4u.getLayPrice3()= ")
                                            .append(r4u.getBackPrice3())
                                            .append(",\t r4u.getLayAmount3()= ")
                                            .append(r4u.getBackAmount3())
                                            .toString());
                                    break;
                                default:
                                    logWarn("!!! enter to case: 'Lay' default ");
                            }
                        }
//						}
                    }

                    if (r.getEx().getAvailableToBack() != null && !(r.getEx().getAvailableToBack().size() < 3)) {

                        printLog(new StringBuilder(100)
                                .append("r.getEx().getAvailableToBack().size()= ")
                                .append(r.getEx().getAvailableToBack().size())
                                .toString());

                        int priceDepth = 0;
                        for (PriceSize _priceSize : r.getEx().getAvailableToBack()) {
                            priceDepth++;

                            // if ("L".equals(p.getType())) {

                            switch (priceDepth) {

                                case 1:

                                    r4u.setBackAmount1(_priceSize.getSize());
                                    r4u.setBackPrice1(_priceSize.getPrice());

                                    printLog(new StringBuilder(100)
                                            .append("r4u.getBackPrice1()= ")
                                            .append(r4u.getBackPrice1())
                                            .append(",\t r4u.getBackAmount1()= ")
                                            .append(r4u.getBackAmount1())
                                            .toString());
                                    break;
                                case 2:

                                    r4u.setBackAmount2(_priceSize.getSize());
                                    r4u.setBackPrice2(_priceSize.getPrice());

                                    printLog(new StringBuilder(100)
                                            .append("r4u.getBackPrice2()= ")
                                            .append(r4u.getBackPrice2())
                                            .append(",\t r4u.getBackAmount2()= ")
                                            .append(r4u.getBackAmount2())
                                            .toString());
                                    break;

                                case 3:

                                    r4u.setBackAmount3(_priceSize.getSize());
                                    r4u.setBackPrice3(_priceSize.getPrice());

                                    printLog(new StringBuilder(100)
                                            .append("r4u.getBackPrice3()= ")
                                            .append(r4u.getBackPrice3())
                                            .append(",\t r4u.getBackAmount3()= ")
                                            .append(r4u.getBackAmount3())
                                            .toString());
                                    break;

                                default:
                                    logWarn("enter to case: 'Back' default ");
                            }
                        }
//						}
                    }

                    r4u.setTotalAmountMatched(marketBook0.getTotalMatched()); //  .getTotalAmountMatched()

                    r4u.setLastPriceMatched(0.0); // .getLastPriceMatched()

                } else {
                    printLog(new StringBuilder(100)
                            .append("!!! !!! !!! MarketRunner with selectionId=")
                            .append(_key)
                            .append(" not found in 'inflatedRunners' map!")
                            .toString());
                }

                // System.out.println
                // ("***----------------------------- after prices..");
                // @@@@
                Double _ak = r4u.getPercWinSglajivWithNR();

                printLog(new StringBuilder(100).append("_ak[")
                        .append(runner.getSelectionId()).append("]=")
                        .append(_ak).toString());

                Double oddsPrecosmeticVal = r4u.getLinkedRunner().getMarket()
                        .getUserData4Market().get(currentUser.getId())
                        .getPreCosmeticValue();
                Double _oddsPrecosmetic = (_ak > 0) ? oddsPrecosmeticVal / _ak
                        : 1.01;

                printLog(new StringBuilder(100)
                        .append("** raw  oddsPrecosmetic =")
                        .append(_oddsPrecosmetic).toString());

                Double _precosmOdds = baseService.getNearestOdds(_oddsPrecosmetic);

                printLog(new StringBuilder(100).append("_precosmOdds: ").append(_precosmOdds).toString());

                r4u.setOddsPrecosmetic(_precosmOdds);

                Double blueOdds1 = r4u.getBackPrice1();
                Double blueAmount1 = (r4u.getBackAmount1() == null ? 0.0 : r4u.getBackAmount1());
                Double blueOdds2 = r4u.getBackPrice2();
                Double pinkOdds = r4u.getLayPrice1();

                Double myOdds = r4u.getUnmatchedLayPrice();
                Double myAmount = (r4u.getUnmatchedLayAmount() == null ? 0.0 : r4u.getUnmatchedLayAmount());

                Double _cosmeticOdds;
                _cosmeticOdds = baseService.getCosmeticOdds(blueOdds1,
                        blueAmount1, blueOdds2, pinkOdds, _precosmOdds, myOdds,
                        myAmount);

                if (_cosmeticOdds == null) {
                    _cosmeticOdds = BaseServiceBean.FAKE_ODDS;
                    logWarn(new StringBuilder(100)
                            .append("!!!calculate  _cosmeticOdds is null, set to:  ")
                            .append(_cosmeticOdds).toString());
                }

                r4u.setOddsCosmetic(_cosmeticOdds);

                // r4u = marketService.merge(r4u);
                // System.out.println
                // ("***// r4u = marketService.merge(r4u)");

                Double _volumeStake = r4u.getLinkedRunner().getMarket()
                        .getUserData4Market().get(currentUser.getId())
                        .getVolumeStake();

                Double _maxLoss = r4u.getLinkedRunner().getMarket()
                        .getUserData4Market().get(currentUser.getId())
                        .getMaxLossPerSelection();

                r4u.setSelectionPrice(1.0);

                Double _selectionPrice = baseService.getSelectionPrice(
                        r4u.getOddsCosmetic(), r4u.getOdds(), _volumeStake,
                        _maxLoss, r4u.getProfitLoss(), inPlayDelay,
                        currentMarket.getMarketStatus(), r4u.getIsNonRunner());

                r4u.setSelectionPrice(_selectionPrice);

                r4u.setSelectionAmount(1.0);
                Double _selectionAmount = baseService.getSelectionAmount(
                        _selectionPrice, r4u.getOdds(), _volumeStake,
                        currentMarket.getMarketStatus());

                r4u.setSelectionAmount(_selectionAmount);

                printLog("+++ saved: r4u.getSelectionPrice(): " + r4u.getSelectionPrice() + ", r4u.getSelectionAmount(): " + r4u.getSelectionAmount());

                r4u = marketService.merge(r4u);

                printLog(new StringBuilder(100).append("** runner=")
                        .append(r4u.getLinkedRunner().getName())
                        .append(" result profitLoss=")
                        .append(r4u.getProfitLoss()).toString());

                printLog(new StringBuilder(100)
                        .append("r4u.getOddsPrecosmetic()=")
                        .append(r4u.getOddsPrecosmetic())
                        .append(",\t r4u.getOddsCosmetic()=")
                        .append(r4u.getOddsCosmetic())
                        .append(",\t _selectionPrice=")
                        .append(_selectionPrice)
                        .append(",\t _selectionAmount=")
                        .append(_selectionAmount).toString());

                printLog("" + r4u);
            }

            getFeedData(market4User);
        }
        updateBets(currentUser, currentMarket, market4User, curBets);
    }

    private void getFeedData(Market4User market4User) {
        getUnitabFeedData(market4User);
    }

    private void getUnitabFeedData(Market4User market4User) {
        Market market = market4User.getLinkedMarket();
        Uzer user = market4User.getLinkedUser();
        boolean mktAlreadyExists = dataFeedService
                .isFeed4Market4UserAlreadyExists("UNITAB%", market.getId(),
                        user.getId());
        // ###
        int foundCount = 0;

        printLog("== market is already Exists: " + mktAlreadyExists);

        if (mktAlreadyExists) {
            Feed4Market4User feed4Market4User = null;
            try {
                feed4Market4User = dataFeedService.getFeed4Market4User(
                        "UNITAB%", market.getId(), user.getId());
            } catch (Throwable t) {
                logError("error call getFeed4Market4User: ", t);
            }

            if (feed4Market4User == null) {
                logWarn("feed4Market4User is null, return");
                return;
            }

            DataFeedEvent dataFeedEvent = dataFeedService
                    .getDataFeedEvent(feed4Market4User.getDataFeedEventId());

            Race race = dataFeedService.getFreshedUnitabRace(dataFeedEvent);

            Map<String, com.unitab.race.Runner> feedRunners = new HashMap<String, com.unitab.race.Runner>(
                    10);
            for (com.unitab.race.Runner runner : race.getRunnerList()) {
                feedRunners.put(runner.getRunnerName().toUpperCase(), runner);
            }

            for (MarketRunner runner : market.getRunners()) {

                boolean rnrAlreadyExists = dataFeedService
                        .isFeed4Runner4UserAlreadyExists("UNITAB%",
                                runner.getId(), user.getId());

                Feed4Runner4User feed4Runner4User = null;
                if (rnrAlreadyExists)
                    try {
                        feed4Runner4User = dataFeedService.getFeed4Runner4User(
                                "UNITAB%", runner.getId(), user.getId());
                    } catch (Throwable t) {
                        logError("error call getFeed4Runner4User: ", t);
                    }
                else {
                    feed4Runner4User = new Feed4Runner4User(user, runner,
                            dataFeedEvent);

                }
                for (Map.Entry<String, com.unitab.race.Runner> stringRunnerEntry : feedRunners
                        .entrySet()) {
                    if (runner.getName().toUpperCase()
                            .contains(stringRunnerEntry.getKey())) {
                        com.unitab.race.Runner feedRunner = stringRunnerEntry
                                .getValue();
                        if (feed4Runner4User != null
                                && feedRunner != null
                                && feedRunner.getFixedOdds() != null
                                && feedRunner.getFixedOdds().getWinOdds() != null
                                && feedRunner.getFixedOdds().getWinOdds() > 1.0
                                && !feedRunner
                                .getFixedOdds()
                                .getWinOdds()
                                .equals(feed4Runner4User.getFeedOdds()
                                        .floatValue())) {
                            feed4Runner4User.setFeedOdds(Double
                                    .valueOf(feedRunner.getFixedOdds()
                                            .getWinOdds()));
                            feed4Runner4User = marketService
                                    .merge(feed4Runner4User);
                            foundCount++;
                        }
                    }
                }
            }

        } else {
            Set<String> runnerNames = new HashSet<String>(10);
            // Market currentMarket = market4User.getLinkedMarket();
            for (MarketRunner runner : market.getRunners()) {
                runnerNames.add(runner.getName());
            }
            DataFeedEvent dataFeedEvent = dataFeedService
                    .findUnitabDataFeedEvent(runnerNames);
            if (dataFeedEvent != null) {
                Race race = dataFeedService.getFreshedUnitabRace(dataFeedEvent);
                if (race == null) {
                    logWarn("freshed race: " + race);
                    return;
                }


                printLog("freshed race: " + race);
                printLog("freshed race: getRunnerList().size(): "
                        + race.getRunnerList().size());


                Map<String, com.unitab.race.Runner> feedRunners = new HashMap<String, com.unitab.race.Runner>(10);

                for (com.unitab.race.Runner runner : race.getRunnerList()) {
                    feedRunners.put(runner.getRunnerName().toUpperCase(),
                            runner);
                }

                Feed4Market4User feed4Market4User = new Feed4Market4User(user,
                        market, dataFeedEvent);
                marketService.merge(feed4Market4User);

                for (MarketRunner runner : market.getRunners()) {

                    Feed4Runner4User feed4Runner4User = new Feed4Runner4User(
                            user, runner, dataFeedEvent);
                    // feed4Runner4User = marketService.merge(feed4Runner4User);
                    for (Map.Entry<String, com.unitab.race.Runner> stringRunnerEntry : feedRunners
                            .entrySet()) {
                        if (runner.getName().toUpperCase()
                                .contains(stringRunnerEntry.getKey())) {
                            com.unitab.race.Runner feedRunner = stringRunnerEntry
                                    .getValue();
                            if (feedRunner != null
                                    && feedRunner.getFixedOdds() != null
                                    && feedRunner.getFixedOdds().getWinOdds() != null
                                    && feedRunner.getFixedOdds().getWinOdds() > 1.0) {
                                feed4Runner4User.setFeedOdds(Double
                                        .valueOf(feedRunner.getFixedOdds()
                                                .getWinOdds()));
                                feed4Runner4User = marketService
                                        .merge(feed4Runner4User);
                                foundCount++;
                            }
                        }
                    }
                }
            }
        }

        if (foundCount > 0)
            printLog("getUnitabFeedData: found fixed odds:" + foundCount
                    + " for " + market.getRunners().size() + " runners");

    }

    public boolean isEqual(Double arg1, Double arg2) {
        boolean result = baseService.isEqual(arg1, arg2);

        printLog("isEqual(): arg1=" + arg1 + ", arg2=" + arg2
                + ", result=" + result);
        return result;
    }

    private void makeUpdateList(final List<CurrentOrderSummary> curBets,
                                final List<PlaceInstruction> newBets, List<ReplaceInstruction> cUpdates,
                                List<PlaceInstruction> cInserts, List<CancelInstruction> cDeletes) {

        for (PlaceInstruction nb : newBets) {

            printLog(new StringBuilder(100)
                    .append(",  nb.getSelectionId()=").append(nb.getSelectionId())
                    .toString());

            for (CurrentOrderSummary cb : curBets) {

                printLog(new StringBuilder(100)
                        .append(",  cb.getSelectionId()=")
                        .append(cb.getSelectionId())
                        .append(", cb.getPrice()=").append(cb.getPriceSize().getPrice())
                        .append(", cb.getSize()=").append(cb.getPriceSize().getSize())
                        .toString());

                if (cb.getSelectionId() == nb.getSelectionId()) {

                    printLog(new StringBuilder(100)
                            .append("process bet with cb.getSelectionId()=")
                            .append(cb.getSelectionId())
                            .append(", cb.getAsianLineId()=")
                            .append(", nb.getPrice()=").append(nb.getLimitOrder().getPrice())
                            .append(", nb.getSize()=").append(nb.getLimitOrder().getSize())
                            .toString());

                    if ((nb.getLimitOrder().getPrice() >= BaseServiceBean.MIN_ODDS
                            && nb.getLimitOrder().getPrice() <= BaseServiceBean.MAX_ODDS && nb
                            .getLimitOrder().getSize() >= BaseServiceBean.MIN_STAKE_AMOUNT)
                            // And ((oBetCurrent.Price <> oBet.Price And
                            // oBetCurrent.RequestedSize = oBet.RequestedSize)
                            // Or (oBetCurrent.Price = oBet.Price And
                            // oBetCurrent.RequestedSize <> oBet.RequestedSize))
                            && (!(isEqual(cb.getPriceSize().getPrice(), nb.getLimitOrder().getPrice()))
                            && isEqual(cb.getPriceSize().getSize(), nb.getLimitOrder().getSize()))
/*
							|| (isEqual(cb.getPriceSize().getPrice(), nb.getLimitOrder().getPrice()) && !(isEqual(
									cb.getPriceSize().getSize(), nb.getLimitOrder().getSize())))
*/
                            ) {

                        ReplaceInstruction ub = new ReplaceInstruction();
                        ub.setBetId(cb.getBetId());
                        // ub.
                        //ub.setOldBetPersistenceType(BetPersistenceTypeEnum.NONE);
                        // ub.setNewBetPersistenceType(BetPersistenceTypeEnum.NONE);

                        //ub.setOldPrice(cb.getPrice());
                        //ub.setOldSize(cb.getSize());
                        ub.setNewPrice(nb.getLimitOrder().getPrice());

                        //ub.setNewSize(nb.getSize());
                        cUpdates.add(ub);
                        printLog(new StringBuilder(100)
                                .append("add bet to update: ub.getNewPrice()=")
                                .append(ub.getNewPrice())
                                .toString());
                    }

                    // If oBet.Price = FAKE_ODDS Or (oBetCurrent.Price <>
                    // oBet.Price And oBetCurrent.RequestedSize <>
                    // oBet.RequestedSize) Then

                    if (isEqual(nb.getLimitOrder().getPrice(), BaseServiceBean.FAKE_ODDS)
                            || (!(isEqual(cb.getPriceSize().getPrice(), nb.getLimitOrder().getPrice())) || !(isEqual(
                            cb.getPriceSize().getSize(), nb.getLimitOrder().getSize())))) {

                        //CancelBets cab = new CancelBets();
                        CancelInstruction ci = new CancelInstruction();
                        ci.setSizeReduction(cb.getSizeRemaining());
                        ci.setBetId(cb.getBetId());
                        cDeletes.add(ci);

                        printLog(Level.INFO, new StringBuilder(100)
                                .append("add bet to delete: cb.getBetId()=")
                                .append(cb.getBetId()).toString());
                    }

                    if (!betContains(nb, cInserts)
                            && (nb.getLimitOrder().getPrice() >= BaseServiceBean.MIN_ODDS && nb
                            .getLimitOrder().getPrice() <= BaseServiceBean.MAX_ODDS)
                            && nb.getLimitOrder().getSize() >= BaseServiceBean.MIN_STAKE_AMOUNT

                            && (!(isEqual(cb.getPriceSize().getPrice(), nb.getLimitOrder().getPrice())) || !(isEqual(
                            cb.getPriceSize().getSize(), nb.getLimitOrder().getSize())))) {
                        cInserts.add(nb);
                        logInfo(new StringBuilder(100)
                                .append("add bet to insert: nb.getPrice()=")
                                .append(nb.getLimitOrder().getPrice())
                                .append(", nb.getSize()=").append(nb.getLimitOrder().getSize())
                                .toString());
                    }

                }
            }
        }

        logInfo(new StringBuilder(100).append("cUpdates.size()=")
                .append(cUpdates.size()).append(", cDeletes.size()=")
                .append(cDeletes.size()).append(", cInserts.size()=")
                .append(cInserts.size()).toString());
    }

    private boolean betContains(PlaceInstruction bet, List<PlaceInstruction> bets) {
        boolean result = false;
        for (PlaceInstruction pb : bets) {
            if (pb.getSelectionId() == bet.getSelectionId()) {
                result = true;
                System.out.println("bet " + bet
                        + " already contains in bets to insert!");
                break;
            }
        }
        return result;
    }

    private void updateBets(Uzer currentUser, Market currentMarket, Market4User market4User,
                            //						List<MUBet> curBets
                            List<CurrentOrderSummary> curBets
    ) {


        Market4User _market4User = currentMarket.getUserData4Market().get(currentUser.getId());
        logInfo("$$$ updateBets - _market4User: " + _market4User);

        Calendar now = Calendar.getInstance();
        Date marketStartTime = currentMarket.getMarketTime();

        printLog("###@@@ MarketDisplayTime = " + marketStartTime);

        Calendar turnOffTime = Calendar.getInstance();
        turnOffTime.setTime(marketStartTime);

        Integer turnOffTimeOffsetHours = market4User
                .getTurnOffTimeOffsetHours();
        Integer turnOffTimeOffsetMinutes = market4User
                .getTurnOffTimeOffsetMinutes();

        turnOffTime.add(Calendar.HOUR, turnOffTimeOffsetHours);
        turnOffTime.add(Calendar.MINUTE, turnOffTimeOffsetMinutes);

        printLog("* DO TURN OFF: Market Id: "
                + currentMarket.getMarketId() + ", Market Name: "
                + currentMarket.getMenuPath() + BS
                + currentMarket.getName() + ", turnOffTime="
                + turnOffTime.getTime() + ", now: " + now.getTime());

        if (now.after(turnOffTime)) {
            boolean isOnAir = market4User.isOnAir();

            printLog("** DO TURN OFF: Market '" + currentMarket
                    + "' isOnAir=" + isOnAir);
            if (isOnAir) {
                market4User.setOnAir(false);
                market4User = marketService.merge(market4User);
                logWarn("*** DO TURN OFF: Market " + currentMarket
                        + " is set 'OnAir' OFF!!");
            }
        }

        boolean isOnAir = market4User.isOnAir();

        logInfo("*** updateBets: for market " + currentMarket + ", isOnAir=" + isOnAir);

// 		Exchange selected_exchange = currentMarket.getExchange() == 1 ? Exchange.UK : Exchange.AUS;

//		List<CancelBets> cDeletes = new ArrayList<CancelBets>();

        List<CancelInstruction> cDeletes = new ArrayList<CancelInstruction>();

        if (!isOnAir) {

            if (curBets != null && curBets.size() > 0) {
                logInfo("*** On Air is OFF for market " + currentMarket + ",\n CANCEL ALL BETS: current bets count :" + curBets.size());

                for (CurrentOrderSummary cb : curBets) {

                    logInfo("will be canceled: CurrentOrderSummary: " + cb);

                    //CancelBets cab = new CancelBets();
                    CancelInstruction ci = new CancelInstruction();

                    ci.setBetId(cb.getBetId());
                    ci.setSizeReduction(cb.getSizeRemaining());

                    logInfo("cancel betId: " + cb.getBetId());
                    cDeletes.add(ci);
                }

                //CancelBetsResult[] cancelBetsResults = null;
                CancelExecutionReport cancelExecutionReport = null;

                if (!cDeletes.isEmpty()) {
                    try {
                        logInfo("*** On Air is OFF for market " + currentMarket.getName() + ", CANCEL ALL BETS for market " + currentMarket.getMarketId());
                        cancelExecutionReport = GlobalAPI.cancelOrders(currentUser.getApiContext(), currentMarket.getMarketId(), cDeletes);

						/*
								ExchangeAPI.cancelBets(
								selected_exchange, currentUser.getApiContext(),
								cDeletes);
*/

                    } catch (Exception e) {
                        logError(new StringBuilder(100)
                                .append("GlobalAPI.cancelOrders error: ")
                                .append(e.getMessage()).append(", market: ")
                                .append(currentMarket.getMarketId()).toString(), e);
                    }
                }
                if (cancelExecutionReport != null) {

                    logInfo("cancelExecutionReport: " + cancelExecutionReport.toString());

                    for (CancelInstructionReport cir : cancelExecutionReport.getInstructionReports()) {

                        logInfo(new StringBuilder(100).append("betId: ")
                                .append(cir.getInstruction().getBetId()).append(", canceled size: ")
                                .append(cir.getSizeCancelled()).append(", errorCode: ")
                                .append(cancelExecutionReport.getErrorCode())
                                .append(", market: ")
                                .append(currentMarket.getMenuPath()).append(BS)
                                .append(currentMarket.getName()).toString());
                    }
                }

                logInfo(curBets.size() + " bets canceled.");
            }
            return;
        }

        logInfo("*** updateBets: On Air is ON for market " + currentMarket + ",\n DOING BETS!!");

        //	List<UpdateBets> cUpdates = new ArrayList<UpdateBets>();

        List<ReplaceInstruction> cUpdates = new ArrayList<ReplaceInstruction>();

        //List<PlaceBets> cInserts = new ArrayList<PlaceBets>();
        List<PlaceInstruction> cInserts = new ArrayList<PlaceInstruction>();

        //List<PlaceBets> newBets = new ArrayList<PlaceBets>();
        List<PlaceInstruction> newBets = new ArrayList<PlaceInstruction>();

        for (MarketRunner runner : currentMarket.getRunners()) {

            Runner4User r4u = runner.getUserData4Runner().get(currentUser.getId());

            if (r4u == null) continue;

            logInfo("*** updateBets: r4u.getSelectionPrice(): " + r4u.getSelectionPrice() + ", r4u.getSelectionAmount(): " + r4u.getSelectionAmount());

            if (r4u.getSelectionPrice() != null && r4u.getSelectionPrice() > BaseServiceBean.MIN_ODDS
                    && r4u.getSelectionAmount() != null && r4u.getSelectionAmount() >= BaseServiceBean.MIN_STAKE_AMOUNT) {

                //		logInfo("*** updateBets: r4u.getSelectionPrice(): " + r4u.getSelectionPrice() + ", r4u.getSelectionAmount(): " + r4u.getSelectionAmount());

                PlaceInstruction pi = new PlaceInstruction();

                pi.setSelectionId(runner.getSelectionId());

                pi.setHandicap(0);
                pi.setSide(Side.LAY);
                // pi.setSide(Side.BACK);
                pi.setOrderType(OrderType.LIMIT);

                LimitOrder limitOrder = new LimitOrder();
                limitOrder.setPersistenceType(PersistenceType.LAPSE);

                limitOrder.setPrice(r4u.getSelectionPrice());
//				limitOrder.setPrice(1.02);
                limitOrder.setSize(r4u.getSelectionAmount());

//				limitOrder.setPrice(100.0);
//				limitOrder.setSize(4.0);

                pi.setLimitOrder(limitOrder);
                logInfo(" pi: " + pi);
                newBets.add(pi);
            }
        }
/*
                PlaceInstruction instruction = new PlaceInstruction();
                instruction.setHandicap(0);
                instruction.setSide(Side.BACK);
                instruction.setOrderType(OrderType.LIMIT);

                LimitOrder limitOrder = new LimitOrder();
                limitOrder.setPersistenceType(PersistenceType.LAPSE);
                //API-NG will return an error with the default size=0.01. This is an expected behaviour.
                //You can adjust the size and price value in the "apingdemo.properties" file
                limitOrder.setPrice(getPrice());
                limitOrder.setSize(getSize());

                instruction.setLimitOrder(limitOrder);
                instruction.setSelectionId(selectionId);
                instructions.add(instruction);

 */
        logInfo("newBets.size()=" + newBets.size() + ", curBets.size()=" + curBets.size());

        makeUpdateList(curBets, newBets, cUpdates, cInserts, cDeletes);

        ReplaceExecutionReport replaceExecutionReport = null;

        if (!cUpdates.isEmpty()) {
            for (ReplaceInstruction ui : cUpdates) {
                logInfo(new StringBuilder(100).append("UpdateBets - BetId()=")
                        .append(ui.getBetId()).append(", bet id = ")
                        .append(ui.getNewPrice()).append(", new price = ")
                        .toString());
            }
            try {
/*
				replaceOrders(APIContext context, String marketId,
                                                List<ReplaceInstruction> instructions
				 */
                replaceExecutionReport = GlobalAPI.replaceOrders(currentUser.getApiContext(), currentMarket.getMarketId(), cUpdates);

            } catch (Exception e) {
                logError(new StringBuilder(100)
                        .append("ExchangeAPI.updateBets error: ")
                        .append(e.getMessage()).append(", market: ")
                        .append(currentMarket.getMenuPath()).append(BS)
                        .append(currentMarket.getName()).toString(), e);
            }
        }

        if (replaceExecutionReport != null) {
            for (ReplaceInstructionReport rir : replaceExecutionReport.getInstructionReports()) {
                logInfo(new StringBuilder(100).append("orders cancelled: ")
                        .append(rir.getCancelInstructionReport())
                        .append(",\n status: ").append(rir.getStatus())
                        .append(", \n errorCode: ").append(replaceExecutionReport.getErrorCode())
                        .append(",\n orders placed: ").append(rir.getPlaceInstructionReport())
                        .append(", market: ").append(currentMarket.getName()).toString());
            }
        }

        CancelExecutionReport cancelExecutionReport = null;

        if (!cDeletes.isEmpty()) {
            try {
                //cancelBetsResults = null; // ExchangeAPI.cancelBets(selected_exchange, currentUser.getApiContext(), cDeletes);
                cancelExecutionReport = GlobalAPI.cancelOrders(currentUser.getApiContext(), currentMarket.getMarketId(), cDeletes);
            } catch (Exception e) {
                logError(new StringBuilder(100)
                        .append("ExchangeAPI.cancelBets error: ")
                        .append(e.getMessage()).append(", market: ")
                        .append(currentMarket.getMenuPath()).append(BS)
                        .append(currentMarket.getName()).toString(), e);
            }
        }

        if (cancelExecutionReport != null) {
            for (CancelInstructionReport cir : cancelExecutionReport.getInstructionReports()) {
                logInfo(new StringBuilder(100).append("bet ")
                        .append(cir.getInstruction()).append(" canceled ")
                        .append(cir.getSizeCancelled()).append(", ")
                        .append(cancelExecutionReport.getErrorCode())
                        .append(", market: ")
                        .append(currentMarket.getMenuPath()).append(BS)
                        .append(currentMarket.getName()).toString());
            }
        }

        int _startRecord = 0;
        int _pageSize = K_MAX_BETS_SUBMIT;

        boolean bMore;

        if (_pageSize > K_MAX_BETS_GET)
            _pageSize = K_MAX_BETS_GET;

        int _endRecord = _startRecord + _pageSize - 1;

        if (_endRecord > newBets.size())
            _endRecord = newBets.size();

        PlaceExecutionReport placeExecutionReport = null;

        List<PlaceInstruction> oBets;
        do {
            bMore = false;
            if (curBets.isEmpty()) {
                if (newBets.size() > _pageSize) {
                    oBets = new ArrayList<PlaceInstruction>();
                    copyBets(newBets, oBets, _startRecord, _endRecord);
                } else {
                    oBets = new ArrayList<PlaceInstruction>();
                    copyBets(newBets, oBets, 0, newBets.size());
                }

            } else {
                oBets = new ArrayList<PlaceInstruction>();
                oBets.addAll(cInserts);
            }

            if (!oBets.isEmpty()) {
/*

				for (PlaceInstruction pbs : oBets) {

					log.info(new StringBuilder(100)
							.append("PlaceBets - AsianLineId=")
							.append(pbs.getAsianLineId()).append(", marketId=")
							.append(pbs.getMarketId()).append(", selectionId=")
							.append(pbs.getSelectionId()).append(", price=")
							.append(pbs.getPrice()).append(", size=")
							.append(pbs.getSize()).append(", betType=")
							.append(pbs.getBetType()).toString());


				}
*/

                try {
                    //	placeBetsResults = null; // ExchangeAPI.placeBets(selected_exchange, currentUser.getApiContext(), oBets);

                    placeExecutionReport = GlobalAPI.placeOrders(currentUser.getApiContext(), currentMarket.getMarketId(), oBets);
/*
					for (PlaceInstructionReport pir : placeExecutionReport.getInstructionReports()) {

						logInfo(new StringBuilder(100).append("bet ").append(pir.getBetId())
								.append(" placed, status: ").append(pir.getStatus())
								.append(", errorCode: ").append(pir.getErrorCode().ordinal())
								.append(", cause: ").append(placeExecutionReport.getErrorCode())
								.append(", market: ").append(currentMarket.getMenuPath())
								.append(BS).append(currentMarket.getName()).toString());
					}
*/

                } catch (Exception e) {
                    logError(new StringBuilder(100)
                            .append("ExchangeAPI.placeBets error: ")
                            .append(e.getMessage()).append(", market: ")
                            .append(currentMarket.getMenuPath()).append(BS)
                            .append(currentMarket.getName()).toString(), e);
                }
            }

            if (oBets.size() != newBets.size())
                bMore = (newBets.size() > _endRecord);

        } while (bMore);

    }

    void copyBets(List<PlaceInstruction> newBets, List<PlaceInstruction> oBets,
                  int _startRecord, int _endRecord) {
        for (int i = _startRecord; i < _endRecord; i++) {
            oBets.add(newBets.get(i));
        }
    }

    private static final int K_MAX_BETS_GET = 100;
    private static final int K_MAX_BETS_SUBMIT = 60;

    private void loadAllActiveMarkets() {

        printLog("* Loading active markets..");
        if (marketService == null) {
            logWarn("marketService is null!");

        } else {

            int i = 0;
            for (Market market : marketService.listMarkets()) {
                if (!serviceBean.getActiveMarkets().containsKey(
                        market.getMarketId())) {

                    serviceBean.getActiveMarkets().put(market.getMarketId(),
                            market);
                    logInfo(new StringBuilder(100).append("market added: ")
                            .append(market.getMenuPath()).append('/')
                            .append(market.getName()).toString());
                    i++;
                }
            }
            if (i > 0)
                logInfo(i + " active market(s) loaded.");
        }
    }

    private void loadActiveUsers() {

        printLog("* Loading active users..");

        if (marketService == null) {
            logWarn("marketService is null!");

        } else {
            int i = 0;
            for (Uzer uzer : marketService.getActiveUsers()) {
                if (!serviceBean.getActiveUsers().containsKey(uzer.getLogin())
                        || serviceBean.getActiveUsers().get(uzer.getLogin()).getMarket4Users().size() != uzer.getMarket4Users().size()) {

                    boolean result = serviceBean.add2ActiveUsers(uzer.getLogin(), uzer);

                    if (result) {
                        logInfo(new StringBuilder(100)
                                .append("user added ok: ").append(uzer)
                                .toString());
                        i++;
                    }
                }
            }
            if (i > 0)
                logInfo(i + " active user(s) loaded.");

        }
    }

    private void processActionRequest(MapMessage message) throws JMSException {
        String act_name = message.getString(ACTION_PROPERTY);
        String market_id = message.getString(MARKET_ID_PROPERTY);
        String login = message.getString(LOGIN_PROPERTY);

        //Long marketId = market_id == null ? null : Long.valueOf(market_id);

        if (act_name == null) {
            logWarn("Unknown action!");
            return;
        }

        Action act = Action.valueOf(act_name);

        printLog("given message with action: " + act.toString());

        try {
            switch (act) {
                case LOAD_ACTIVE_USERS: {
                    loadActiveUsers();
                    break;
                }

                case LOAD_ACTIVE_MARKETS: {
                    loadAllActiveMarkets();
                    break;
                }

                case UPDATE_MARKETS: {
                    actionUpdateUserMarkets();
                    break;
                }

                case UPDATE_MARKET: {
                    actionUpdateMarketStatus(login, market_id);
                    // doUpdateMarketStatus(login, marketId);
                    break;
                }

                case UPDATE_MARKET_PRICES: {
                    actionUpdateMarketPrices(login, market_id);
                    break;
                }
                case KEEP_ALIVE: {
                    doKeepAliveRequests();
                    break;
                }

                default: {
                    logWarn("Unknown action: " + act.name());
                }
            }
        } catch (Throwable t) {
            logError("processActionRequest: ", t);
        }
    }

}
