package net.bir2.ejb.action;

import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetStatusEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.CancelBets;
import generated.exchange.BFExchangeServiceStub.CancelBetsResult;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.MarketLite;
import generated.exchange.BFExchangeServiceStub.PlaceBets;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;
import generated.exchange.BFExchangeServiceStub.UpdateBets;
import generated.exchange.BFExchangeServiceStub.UpdateBetsResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import net.bir2.ejb.session.market.BaseService;
import net.bir2.ejb.session.market.BaseServiceBean;
import net.bir2.ejb.session.market.DataFeedService;
import net.bir2.ejb.session.market.MarketService;
import net.bir2.handler.ExchangeAPI;
import net.bir2.handler.ExchangeAPI.Exchange;
import net.bir2.multitrade.ejb.entity.DataFeedEvent;
import net.bir2.multitrade.ejb.entity.Feed4Market4User;
import net.bir2.multitrade.ejb.entity.Feed4Runner4User;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Market4User;
import net.bir2.multitrade.ejb.entity.Runner;
import net.bir2.multitrade.ejb.entity.Runner4User;
import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir2.multitrade.util.InflatedMarketPrices.InflatedPrice;
import net.bir2.multitrade.util.InflatedMarketPrices.InflatedRunner;

//import org.apache.log4j.Logger;
import java.util.logging.*;

import com.unitab.race.Race;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/queue/PriceRequestQueue") })
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

	// @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void onMessage(Message msg) {

		if (msg == null || !(msg instanceof MapMessage))
			return;

		try {

			MapMessage message = (MapMessage) msg;
			if (message.getString(ACTION_PROPERTY) != null) {
				processActionRequest(message);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE,"error on Message..", e);
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

	public static final String UNKNOWN = "UNKNOWN";

	private void actionUpdateMarketStatus(String login, Long marketId) {
		
			log.fine(new StringBuilder(100)
					.append("actionUpdateMarketStatus: login=").append(login)
					.append(", marketId=").append(marketId).toString());

		// int lastDigit = (int) (System.currentTimeMillis() % 10L);

		// ShedulerActivity serviceBean = ShedulerActivityBean.getInstance();

		Exchange selected_exchange;

		Market currentMarket = null;
		if (marketService.isMarketAlreadyExistsByMarketId(marketId)) {
			try {
				currentMarket = serviceBean.getActiveMarket(marketId);
			} catch (Exception e) {
				log.log(Level.SEVERE, "ERROR READING CURRENT MARKET FOR STATUS RENEW , MSG "
						, e);
			}
		} else {
			serviceBean.getActiveMarkets().remove(marketId);
			return;
		}

		Uzer currentUser = serviceBean.getActiveUsers().get(login);

		if (currentMarket == null) {
			log.log(Level.WARNING, "currentMarket is not found for market_id:" + marketId);
			return;
		}

		if (currentUser == null) {
			log.log(Level.WARNING, "currentUser is not found for login:" + login);
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

			log.fine("currentMarket: " + currentMarket.getMenuPath() + BS
					+ currentMarket.getName() + ", minutes to start: " + km
					+ ", refreshInt(second(s)):" + refreshInt);

		selected_exchange = currentMarket.getExchange() == 1 ? Exchange.UK
				: Exchange.AUS;

		String marketStatus;
		long startTime = System.currentTimeMillis();
		try {

			MarketLite marketLite = ExchangeAPI.getMarketInfo(
					selected_exchange, currentUser.getApiContext(), Long
							.valueOf(currentMarket.getMarketId()).intValue());

			long endTime = System.currentTimeMillis();
			
				log.fine("Update Market Status completed, login=" + login
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
				log.log(Level.SEVERE, "UpdateMarketStatus error, message: ", e);
		}


		if (!"CLOSED".equals(currentMarket.getMarketStatus())) {
			baseService.sendDelayedRequest(Action.UPDATE_MARKET_PRICES, login,
					marketId.toString(), 1);
		} else {
			marketService.remove(currentMarket, currentUser);
		}

	}

	private void actionUpdateMarketPrices(String login, Long marketId) {

		Exchange selected_exchange;

		Market currentMarket = null;
		try {

			currentMarket = serviceBean.getActiveMarket(marketId);
			if (currentMarket != null) {
				currentMarket = marketService.getMarket(currentMarket.getId());

				serviceBean.setActiveMarket(currentMarket.getMarketId(),
						currentMarket);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "ERROR GETTING CURRENT MARKET , MSG ", e);
		}

		if (currentMarket == null) {
			log.log(Level.WARNING, "currentMarket is not found for market_id:"+ marketId);
			return;
		}

		Uzer currentUser = serviceBean.getActiveUsers().get(login);

		if (currentUser == null) {
			log.log(Level.WARNING,"currentUser is not found for login:" + login);
			return;
		}

		Market4User market4User = currentMarket.getUserData4Market().get(
				currentUser.getId());
		
			log.fine( "given: " + market4User);

		boolean isOnAir = market4User.isOnAir();

		if (isOnAir) {
			
				log.fine("*** Update market prices for login: " + login
						+ ", market: " + marketId);

			selected_exchange = currentMarket.getExchange() == 1 ? Exchange.UK
					: Exchange.AUS;

			List<MUBet> curBets = new ArrayList<MUBet>(10);
			MUBet[] currentBets = null;
			long startTime = System.currentTimeMillis();

			try {

				currentBets = ExchangeAPI.getMUBets(selected_exchange,
						currentUser.getApiContext(),
						Long.valueOf(currentMarket.getMarketId()).intValue());

			} catch (Exception e) {
				log.severe(" 'Get Current Bets' error, message: "
						+ e.getMessage());
			}

			long endTime = System.currentTimeMillis();
			
				log.fine("action 'Get Current Bets' COMPLETED, login="
						+ currentUser.getLogin() + ", marketId="
						+ currentMarket.getMarketId() + ", time consumed: "
						+ ((endTime - startTime) / 1000.0) + " second(s)");

			if (currentBets != null)
				for (MUBet bet : currentBets) {
					
						log.fine(new StringBuilder(100)
								.append("bet: SelectionId=")
								.append(bet.getSelectionId())
								.append(", bet.getBetStatus()=")
								.append(bet.getBetStatus()).append(", price=")
								.append(bet.getPrice()).append(", size=")
								.append(bet.getSize()).toString());

					// Long _key = Long.valueOf(bet.getSelectionId());

					Runner runner = currentMarket.getRunnersMap().get(
							(long) bet.getSelectionId());

					/*
					 * Runner runner = marketService.getRunnerBySelectionId(
					 * currentMarket.getId(), _key);
					 */
					if (runner == null) {
						log.severe(new StringBuilder(100)
								.append("runner is null for selectionId=")
								.append(bet.getSelectionId()).toString());
					}
					if (runner != null) {

						Runner4User r4u = runner.getUserData4Runner().get(
								currentUser.getId());

						if (r4u == null) {
							log.severe(new StringBuilder(100)
									.append("runner4user is null for userId=")
									.append(currentUser.getId()).toString());
						}
						if (r4u != null) {
							if (BetStatusEnum.U.equals(bet.getBetStatus())
									&& BetTypeEnum.L.equals(bet.getBetType())) {
								
									log.fine(new StringBuilder(100)
											.append("get unmatched bet for selectionId=")
											.append(bet.getSelectionId())
											.toString());
								r4u.setUnmatchedLayPrice(bet.getPrice());
								r4u.setUnmatchedLayAmount(bet.getSize());
								curBets.add(bet);
							}

							if (BetStatusEnum.M.equals(bet.getBetStatus())
									&& BetTypeEnum.L.equals(bet.getBetType())) {
								log.info(new StringBuilder(100)
										.append("get matched bet for selectionId=")
										.append(bet.getSelectionId())
										.toString());
								r4u.setMatchedLayPrice(bet.getPrice());
								r4u.setMatchedLayAmount(bet.getSize());
							}

							marketService.merge(r4u);

						}
					}
				}

			Double sum = null;

			for (MUBet currentBet : currentBets) {
				if (BetStatusEnum.M.equals(currentBet.getBetStatus())) {
					if (currentBet.getBetType() == BetTypeEnum.B) {

						if (sum == null) {
							sum = currentBet.getSize();
						} else {
							sum += currentBet.getSize();
						}

						// sum += currentBets[i].getSize();
					} else {

						if (sum == null) {
							sum = -currentBet.getSize();
						} else {
							sum -= currentBet.getSize();
						}

						// sum -= currentBets[i].getSize();
					}
				}
			}

				log.fine("sum=" + sum);

			Map<Long, Double> profitMap = new HashMap<Long, Double>(10);

			if (currentBets != null && currentBets.length > 0) {
				for (int i = 0; i < currentBets.length; i++) {
					Long _key = (long) currentBets[i].getSelectionId();

					Double _profit = null;
					if (BetStatusEnum.M.equals(currentBets[i].getBetStatus())) {
						
							log.fine(new StringBuilder(100).append("i=")
									.append(i).append(", betType=")
									.append(currentBets[i].getBetType())
									.append(", price=")
									.append(currentBets[i].getPrice())
									.append(", size=")
									.append(currentBets[i].getSize())
									.toString());

						double _profitItem = currentBets[i].getSize()
								* currentBets[i].getPrice();
					
							log.fine(new StringBuilder(100)
									.append("_profitItem=").append(_profitItem)
									.toString());

						if (currentBets[i].getBetType() == BetTypeEnum.B) {

							_profit = (_profit == null) ? new Double(
									_profitItem) : _profit + _profitItem;

							/*
							 * if (_profit == null) { _profit = new
							 * Double(_profitItem); } else { _profit = _profit +
							 * _profitItem; }
							 */
						} else {
							_profit = (_profit == null) ? new Double(
									-_profitItem) : _profit - _profitItem;
							/*
							 * if (_profit == null) { _profit = new
							 * Double(-_profitItem); } else { _profit -=
							 * _profitItem; }
							 */
						}
					}

					if (_profit != null) {
						profitMap.put(_key, _profit);
					
							log.fine(new StringBuilder(100).append("_profit=")
									.append(_profit).toString());
					}
					// r4u.setProfitLoss(_profit);
					// r4u = marketService.merge(r4u);

				}
				// System.out.println("profitMap filled.");
			}

			for (Runner runner : currentMarket.getRunners()) {
				Runner4User r4u = runner.getUserData4Runner().get(
						currentUser.getId());
				Long _key = runner.getSelectionId();

				// Double _profit = (profitMap.get(_key)==null? 0.0:
				// profitMap.get(_key));
				Double _profit = profitMap.get(_key);

				if (sum != null) {
					if (_profit == null) {
						_profit = -sum;
					} else {
						_profit -= sum;
					}
				}

				if (_profit != null) {
				
						log.fine(new StringBuilder(100).append("_profit=")
								.append(_profit).toString());
					r4u.setProfitLoss(_profit);
					marketService.merge(r4u);
				}
			}

			net.bir2.multitrade.util.InflatedMarketPrices prices = null;
			int inPlayDelay = 0;
			startTime = System.currentTimeMillis();
			String currency = "";
			try {
				prices = ExchangeAPI.getMarketPrices(selected_exchange,
						currentUser.getApiContext(),
						Long.valueOf(currentMarket.getMarketId()).intValue());
				inPlayDelay = prices.getInPlayDelay();
		
					log.fine("inPlayDelay =" + inPlayDelay);
					
				currency = prices.getCurrency();
				// System.setProperty(currency, value)
			} catch (Exception e) {
				log.severe("getPrices() error, message: " + e.getMessage());
			}

			endTime = System.currentTimeMillis();
			
				log.fine("actionUpdateMarketPrices COMPLETED, login=" + login
						+ ", marketId=" + marketId + ", time consumed: "
						+ ((endTime - startTime) / 1000.0) + " second(s)");
			
				log.fine("currentMarket: name=" + currentMarket.getName()
						+ ", runners count="
						+ currentMarket.getRunners().size()
						+ ", runnersMap size="
						+ currentMarket.getRunnersMap().size());

			Map<Long, InflatedRunner> inflatedRunners = new HashMap<Long, InflatedRunner>(
					10);

			if (prices != null)

				for (InflatedRunner r : prices.getRunners()) {
					inflatedRunners.put((long) r.getSelectionId(), r);
				}

			for (Runner runner : currentMarket.getRunners()) {
				Runner4User r4u = runner.getUserData4Runner().get(
						currentUser.getId());
				Long _key = runner.getSelectionId();

				InflatedRunner r = inflatedRunners.get(_key);

				r4u.setCurrency(baseService.getCurrencySymbol(currency));
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

				r4u.setTotalAmountMatched(0.0);
				r4u.setLastPriceMatched(0.0);

				r4u = marketService.merge(r4u);

				if (r != null) {

					if (r.getLayPrices() != null) {
					
							log.fine(new StringBuilder(100)
									.append("r.getLayPrices().size()=")
									.append(r.getLayPrices().size()).toString());
						for (InflatedPrice p : r.getLayPrices()) {

							if ("B".equals(p.getType())) {
							
									log.fine(new StringBuilder(100)
											.append("p.getType()=")
											.append(p.getType())
											.append(",\t p.getDepth()=")
											.append(p.getDepth()).toString());
								switch (p.getDepth()) {
								case 1:
									r4u.setBackAmount1(p.getAmountAvailable());
									r4u.setBackPrice1(p.getPrice());
								
										log.fine(new StringBuilder(100)
												.append("r4u.getBackPrice1()=")
												.append(r4u.getBackPrice1())
												.append(",\t r4u.getBackAmount1()")
												.append(r4u.getBackAmount1())
												.toString());
									break;
								case 2:

									r4u.setBackAmount2(p.getAmountAvailable());
									r4u.setBackPrice2(p.getPrice());
									
										log.fine(new StringBuilder(100)
												.append("r4u.getBackPrice2()=")
												.append(r4u.getBackPrice2())
												.append(",\t r4u.getBackAmount2()")
												.append(r4u.getBackAmount2())
												.toString());
									break;
								case 3:
									r4u.setBackAmount3(p.getAmountAvailable());
									r4u.setBackPrice3(p.getPrice());
									
										log.fine(new StringBuilder(100)
												.append("r4u.getBackPrice3()=")
												.append(r4u.getBackPrice3())
												.append(",\t r4u.getBackAmount3()")
												.append(r4u.getBackAmount3())
												.toString());
									break;
								default:
									log.log(Level.WARNING, "!!! enter to case: B default ");
								}
							}
						}
					}

					if (r.getBackPrices() != null) {

							log.fine(new StringBuilder(100)
									.append("r.getBackPrices().size()=")
									.append(r.getBackPrices().size())
									.toString());

						for (InflatedPrice p : r.getBackPrices()) {

							if ("L".equals(p.getType())) {

									log.fine(new StringBuilder(100)
											.append("p.getType()=")
											.append(p.getType())
											.append(",\t p.getDepth()=")
											.append(p.getDepth()).toString());
								switch (p.getDepth()) {
								case 1:
									r4u.setLayAmount1(p.getAmountAvailable());
									r4u.setLayPrice1(p.getPrice());

										log.fine(new StringBuilder(100)
												.append("r4u.getLayPrice1()=")
												.append(r4u.getLayPrice1())
												.append(",\t r4u.getBackAmount1()")
												.append(r4u.getBackAmount1())
												.toString());
									break;
								case 2:
									r4u.setLayAmount2(p.getAmountAvailable());
									r4u.setLayPrice2(p.getPrice());

										log.fine(new StringBuilder(100)
												.append("r4u.getLayPrice2()=")
												.append(r4u.getLayPrice2())
												.append(",\t r4u.getBackAmount2()")
												.append(r4u.getBackAmount2())
												.toString());
									break;

								case 3:
									r4u.setLayAmount3(p.getAmountAvailable());
									r4u.setLayPrice3(p.getPrice());

										log.fine(new StringBuilder(100)
												.append("r4u.getLayPrice3()=")
												.append(r4u.getLayPrice3())
												.append(",\t r4u.getBackAmount3()")
												.append(r4u.getBackAmount3())
												.toString());
									break;

								default:
									log.log(Level.WARNING, "enter to case: L default ");
								}
							}
						}
					}

					r4u.setTotalAmountMatched(r.getTotalAmountMatched());
					r4u.setLastPriceMatched(r.getLastPriceMatched());

				} else {

						log.fine(new StringBuilder(100)
								.append("!!! !!! !!! Runner with selectionId=")
								.append(_key)
								.append(" not found in 'inflatedRunners' map!")
								.toString());
				}

				// System.out.println
				// ("***----------------------------- after prices..");
				Double _ak = r4u.getPercWinSglajivWithNR();

					log.fine(new StringBuilder(100).append("_ak[")
							.append(runner.getSelectionId()).append("]=")
							.append(_ak).toString());
				Double oddsPrecosmeticVal = r4u.getLinkedRunner().getMarket()
						.getUserData4Market().get(currentUser.getId())
						.getPreCosmeticValue();
				Double _oddsPrecosmetic = (_ak > 0) ? oddsPrecosmeticVal / _ak
						: 1.01;

					log.fine(new StringBuilder(100)
							.append("** raw  oddsPrecosmetic =")
							.append(_oddsPrecosmetic).toString());

				Double _precosmOdds = baseService
						.getNearestOdds(_oddsPrecosmetic);

					log.fine(new StringBuilder(100).append("_precosmOdds: ")
							.append(_precosmOdds).toString());
				r4u.setOddsPrecosmetic(_precosmOdds);

				Double blueOdds1 = r4u.getLayPrice1();
				Double blueAmount1 = (r4u.getLayAmount1() == null ? 0.0 : r4u
						.getLayAmount1());
				Double blueOdds2 = r4u.getLayPrice2();
				Double pinkOdds = r4u.getBackPrice1();

				Double myOdds = r4u.getUnmatchedLayPrice();
				Double myAmount = (r4u.getUnmatchedLayAmount() == null ? 0.0
						: r4u.getUnmatchedLayAmount());
				Double _cosmeticOdds;
				_cosmeticOdds = baseService.getCosmeticOdds(blueOdds1,
						blueAmount1, blueOdds2, pinkOdds, _precosmOdds, myOdds,
						myAmount);

				if (_cosmeticOdds == null) {
					_cosmeticOdds = BaseServiceBean.FAKE_ODDS;
					log.log(Level.WARNING, new StringBuilder(100)
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

				r4u = marketService.merge(r4u);

					log.fine(new StringBuilder(100).append("** runner=")
							.append(r4u.getLinkedRunner().getName())
							.append(" result profitLoss=")
							.append(r4u.getProfitLoss()).toString());

					log.fine(new StringBuilder(100)
							.append("r4u.getOddsPrecosmetic()=")
							.append(r4u.getOddsPrecosmetic())
							.append(",\t r4u.getOddsCosmetic()=")
							.append(r4u.getOddsCosmetic())
							.append(",\t _selectionPrice=")
							.append(_selectionPrice)
							.append(",\t _selectionAmount=")
							.append(_selectionAmount).toString());

				// log.fine(r4u);
			}
			getFeedData(market4User);
			updateBets(currentUser, currentMarket, market4User, curBets);
		}
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

			log.fine("== market is already Exists: " + mktAlreadyExists);
			
		if (mktAlreadyExists) {
			Feed4Market4User feed4Market4User = null;
			try {
				feed4Market4User = dataFeedService.getFeed4Market4User(
						"UNITAB%", market.getId(), user.getId());
			} catch (Throwable t) {
				log.severe("error call getFeed4Market4User: " + t);
			}
			if (feed4Market4User == null) {
				log.log(Level.WARNING, "feed4Market4User is null, return");
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

			for (Runner runner : market.getRunners()) {

				boolean rnrAlreadyExists = dataFeedService
						.isFeed4Runner4UserAlreadyExists("UNITAB%",
								runner.getId(), user.getId());

				Feed4Runner4User feed4Runner4User = null;
				if (rnrAlreadyExists)
					try {
						feed4Runner4User = dataFeedService.getFeed4Runner4User(
								"UNITAB%", runner.getId(), user.getId());
					} catch (Throwable t) {
						log.severe(new StringBuilder(100)
								.append("error call getFeed4Runner4User: ")
								.append(t).toString());
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
			for (Runner runner : market.getRunners()) {
				runnerNames.add(runner.getName());
			}
			DataFeedEvent dataFeedEvent = dataFeedService
					.findUnitabDataFeedEvent(runnerNames);
			if (dataFeedEvent != null) {
				Race race = dataFeedService.getFreshedUnitabRace(dataFeedEvent);
				if (race == null) {
					log.log(Level.WARNING, "freshed race: " + race);
					return;
				}
	
				
					log.fine("freshed race: " + race);
					log.fine("freshed race: getRunnerList().size(): "
							+ race.getRunnerList().size());
				

				Map<String, com.unitab.race.Runner> feedRunners = new HashMap<String, com.unitab.race.Runner>(
						10);
				for (com.unitab.race.Runner runner : race.getRunnerList()) {
					feedRunners.put(runner.getRunnerName().toUpperCase(),
							runner);
				}

				Feed4Market4User feed4Market4User = new Feed4Market4User(user,
						market, dataFeedEvent);
				marketService.merge(feed4Market4User);

				for (Runner runner : market.getRunners()) {

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
			log.info("getUnitabFeedData: found fixed odds:" + foundCount
					+ " for " + market.getRunners().size() + " runners");

	}

	public boolean isEqual(Double arg1, Double arg2) {
		boolean result = baseService.isEqual(arg1, arg2);
		
			log.fine("isEqual(): arg1=" + arg1 + ", arg2=" + arg2
					+ ", result=" + result);
		return result;
	}

	private void makeUpdateList(final List<MUBet> curBets,
			final List<PlaceBets> newBets, List<UpdateBets> cUpdates,
			List<PlaceBets> cInserts, List<CancelBets> cDeletes) {
		for (PlaceBets nb : newBets) {
		
				log.fine(new StringBuilder(100).append("nb.getAsianLineId()=")
						.append(nb.getAsianLineId())
						.append(",  nb.getSelectionId()=")
						.append(nb.getSelectionId()).toString());

			for (MUBet cb : curBets) {
		
					log.fine(new StringBuilder(100)
							.append("cb.getAsianLineId()=")
							.append(cb.getAsianLineId())
							.append(",  cb.getSelectionId()=")
							.append(cb.getSelectionId())
							.append(", cb.getPrice()=").append(cb.getPrice())
							.append(", cb.getSize()=").append(cb.getSize())
							.toString());

				if (cb.getSelectionId() == nb.getSelectionId()
						&& cb.getAsianLineId() == nb.getAsianLineId()) {
		
						log.fine(new StringBuilder(100)
								.append("process bet with cb.getSelectionId()=")
								.append(cb.getSelectionId())
								.append(", cb.getAsianLineId()=")
								.append(cb.getAsianLineId())
								.append(", nb.getPrice()=")
								.append(nb.getPrice())
								.append(", nb.getSize()=").append(nb.getSize())
								.toString());

					if ((nb.getPrice() >= BaseServiceBean.MIN_ODDS
							&& nb.getPrice() <= BaseServiceBean.MAX_ODDS && nb
							.getSize() >= BaseServiceBean.MIN_STAKE_AMOUNT)
							// And ((oBetCurrent.Price <> oBet.Price And
							// oBetCurrent.RequestedSize = oBet.RequestedSize)
							// Or (oBetCurrent.Price = oBet.Price And
							// oBetCurrent.RequestedSize <> oBet.RequestedSize))
							&& (!(isEqual(cb.getPrice(), nb.getPrice())) && isEqual(
									cb.getSize(), nb.getSize()))

							|| (isEqual(cb.getPrice(), nb.getPrice()) && !(isEqual(
									cb.getSize(), nb.getSize())))) {

						UpdateBets ub = new UpdateBets();
						ub.setBetId(cb.getBetId());
						ub.setOldBetPersistenceType(BetPersistenceTypeEnum.NONE);
						ub.setNewBetPersistenceType(BetPersistenceTypeEnum.NONE);

						ub.setOldPrice(cb.getPrice());
						ub.setOldSize(cb.getSize());
						ub.setNewPrice(nb.getPrice());
						ub.setNewSize(nb.getSize());
						cUpdates.add(ub);
						log.info(new StringBuilder(100)
								.append("add bet to update: ub.getNewPrice()=")
								.append(ub.getNewPrice())
								.append(", ub.getNewSize()=")
								.append(ub.getNewSize()).toString());
					}

					// If oBet.Price = FAKE_ODDS Or (oBetCurrent.Price <>
					// oBet.Price And oBetCurrent.RequestedSize <>
					// oBet.RequestedSize) Then
					if (isEqual(nb.getPrice(), BaseServiceBean.FAKE_ODDS)
							|| (!(isEqual(cb.getPrice(), nb.getPrice())) && !(isEqual(
									cb.getSize(), nb.getSize())))) {

						CancelBets cab = new CancelBets();
						cab.setBetId(cb.getBetId());
						cDeletes.add(cab);
						log.info(new StringBuilder(100)
								.append("add bet to delete: cb.getBetId()=")
								.append(cb.getBetId()).toString());
					}

					if (!betContains(nb, cInserts)
							&& (nb.getPrice() >= BaseServiceBean.MIN_ODDS && nb
									.getPrice() <= BaseServiceBean.MAX_ODDS)
							&& nb.getSize() >= BaseServiceBean.MIN_STAKE_AMOUNT

							&& (!(isEqual(cb.getPrice(), nb.getPrice())) && !(isEqual(
									cb.getSize(), nb.getSize())))) {
						cInserts.add(nb);
						log.info(new StringBuilder(100)
								.append("add bet to insert: nb.getPrice()=")
								.append(nb.getPrice())
								.append(", nb.getSize()=").append(nb.getSize())
								.toString());
					}

				}
			}
		}
		
			log.fine(new StringBuilder(100).append("cUpdates.size()=")
					.append(cUpdates.size()).append(", cDeletes.size()=")
					.append(cDeletes.size()).append(", cInserts.size()=")
					.append(cInserts.size()).toString());
	}

	private boolean betContains(PlaceBets bet, List<PlaceBets> bets) {
		boolean result = false;
		for (PlaceBets pb : bets) {
			if (pb.getSelectionId() == bet.getSelectionId()
					&& pb.getAsianLineId() == bet.getAsianLineId()) {
				result = true;
				System.out.println("bet " + bet
						+ " already contains in bets to insert!");
				break;
			}
		}
		return result;
	}

	private void updateBets(Uzer currentUser, Market currentMarket,

	Market4User market4User, List<MUBet> curBets) {

		/*
		 * Market4User market4User = currentMarket.getUserData4Market().get(
		 * currentUser.getId());
		 */

		Calendar now = Calendar.getInstance();
		Date marketStartTime = currentMarket.getMarketTime();
		
			log.fine("###@@@ MarketDisplayTime = " + marketStartTime);

		Calendar turnOffTime = Calendar.getInstance();
		turnOffTime.setTime(marketStartTime);

		Integer turnOffTimeOffsetHours = market4User
				.getTurnOffTimeOffsetHours();
		Integer turnOffTimeOffsetMinutes = market4User
				.getTurnOffTimeOffsetMinutes();

		turnOffTime.add(Calendar.HOUR, turnOffTimeOffsetHours);
		turnOffTime.add(Calendar.MINUTE, turnOffTimeOffsetMinutes);
		
			log.fine("* DO TURN OFF: Market Id: "
					+ currentMarket.getMarketId() + ", Market Name: "
					+ currentMarket.getMenuPath() + BS
					+ currentMarket.getName() + ", turnOffTime="
					+ turnOffTime.getTime() + ", now: " + now.getTime());

		if (now.after(turnOffTime)) {
			boolean isOnAir = market4User.isOnAir();
		
				log.fine("** DO TURN OFF: Market '" + currentMarket
						+ "' isOnAir=" + isOnAir);
			if (isOnAir) {
				market4User.setOnAir(false);
				market4User = marketService.merge(market4User);
				log.log(Level.WARNING, "*** DO TURN OFF: Market " + currentMarket
						+ " is set 'OnAir' OFF!!");
			}
		}

		boolean isOnAir = market4User.isOnAir();

			log.fine("*** for market " + currentMarket + " onAir is " + isOnAir);

		Exchange selected_exchange = currentMarket.getExchange() == 1 ? Exchange.UK
				: Exchange.AUS;

		List<CancelBets> cDeletes = new ArrayList<CancelBets>(100);

		if (!isOnAir) {
			if (curBets != null && !curBets.isEmpty()) {
				log.info("*** On Air is OFF for market " + currentMarket
						+ ",\n CANCEL ALL BETS!!");
				for (MUBet cb : curBets) {
					CancelBets cab = new CancelBets();
					cab.setBetId(cb.getBetId());
					cDeletes.add(cab);
				}
				CancelBetsResult[] cancelBetsResults = null;
				if (!cDeletes.isEmpty()) {
					try {
						cancelBetsResults = ExchangeAPI.cancelBets(
								selected_exchange, currentUser.getApiContext(),
								cDeletes);
					} catch (Exception e) {
						log.severe(new StringBuilder(100)
								.append("ExchangeAPI.cancelBets error: ")
								.append(e.getMessage()).append(", market: ")
								.append(currentMarket.getMenuPath()).append(BS)
								.append(currentMarket.getName()).toString());
					}
				}
				if (cancelBetsResults != null) {
					for (CancelBetsResult cbr : cancelBetsResults) {
						log.info(new StringBuilder(100).append("bet ")
								.append(cbr.getBetId()).append(" canceled ")
								.append(cbr.getSuccess()).append(", ")
								.append(cbr.getResultCode().getValue())
								.append(", market: ")
								.append(currentMarket.getMenuPath()).append(BS)
								.append(currentMarket.getName()).toString());
					}
				}
				log.info(curBets.size() + " bets canceled.");
			}
			return;
		}

			log.fine("*** On Air is ON for market " + currentMarket
					+ ",\n DOING BETS!!");

		List<UpdateBets> cUpdates = new ArrayList<UpdateBets>(64);
		List<PlaceBets> cInserts = new ArrayList<PlaceBets>(64);

		List<PlaceBets> newBets = new ArrayList<PlaceBets>(64);

		for (Runner runner : currentMarket.getRunners()) {
			Runner4User r4u = runner.getUserData4Runner().get(
					currentUser.getId());
			if (r4u.getSelectionPrice() > BaseServiceBean.MIN_ODDS
					&& r4u.getSelectionAmount() >= BaseServiceBean.MIN_STAKE_AMOUNT) {
				PlaceBets pb = new PlaceBets();

				pb.setMarketId(Long.valueOf(currentMarket.getMarketId())
						.intValue());
				pb.setSelectionId(Long.valueOf(runner.getSelectionId())
						.intValue());
				pb.setBetType(BetTypeEnum.L);
				pb.setPrice(r4u.getSelectionPrice());
				pb.setSize(r4u.getSelectionAmount());
				pb.setBetCategoryType(BetCategoryTypeEnum.E);
				pb.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
				pb.setAsianLineId(0);
				newBets.add(pb);
			}
		}

			log.fine("newBets.size()=" + newBets.size() + ", curBets.size()="
					+ curBets.size());
		makeUpdateList(curBets, newBets, cUpdates, cInserts, cDeletes);
		UpdateBetsResult[] updateBetResults = null;

		if (!cUpdates.isEmpty()) {
			for (UpdateBets ubs : cUpdates) {
				log.info(new StringBuilder(100).append("UpdateBets - BetId()=")
						.append(ubs.getBetId()).append(", old price=")
						.append(ubs.getOldPrice()).append(", old size=")
						.append(ubs.getOldSize()).append(", new price=")
						.append(ubs.getNewPrice()).append(", new size=")
						.append(ubs.getNewSize()).toString());
			}
			try {
				updateBetResults = ExchangeAPI.updateBets(selected_exchange,
						currentUser.getApiContext(), cUpdates);

			} catch (Exception e) {
				log.severe(new StringBuilder(100)
						.append("ExchangeAPI.updateBets error: ")
						.append(e.getMessage()).append(", market: ")
						.append(currentMarket.getMenuPath()).append(BS)
						.append(currentMarket.getName()).toString());
			}
		}

		if (updateBetResults != null) {
			for (UpdateBetsResult ubr : updateBetResults) {
				log.info(new StringBuilder(100).append("bet ")
						.append(ubr.getBetId()).append(" updated ")
						.append(ubr.getSuccess()).append(", ")
						.append(ubr.getResultCode().getValue())
						.append(", market: ")
						.append(currentMarket.getMenuPath()).append(BS)
						.append(currentMarket.getName()).toString());
			}
		}

		CancelBetsResult[] cancelBetsResults = null;
		if (!cDeletes.isEmpty()) {
			try {
				cancelBetsResults = ExchangeAPI.cancelBets(selected_exchange,
						currentUser.getApiContext(), cDeletes);
			} catch (Exception e) {
				log.severe(new StringBuilder(100)
						.append("ExchangeAPI.cancelBets error: ")
						.append(e.getMessage()).append(", market: ")
						.append(currentMarket.getMenuPath()).append(BS)
						.append(currentMarket.getName()).toString());
			}
		}

		if (cancelBetsResults != null) {
			for (CancelBetsResult cbr : cancelBetsResults) {
				log.info(new StringBuilder(100).append("bet ")
						.append(cbr.getBetId()).append(" canceled ")
						.append(cbr.getSuccess()).append(", ")
						.append(cbr.getResultCode().getValue())
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

		PlaceBetsResult[] placeBetsResults = null;
		List<PlaceBets> oBets;
		do {
			bMore = false;
			if (curBets.isEmpty()) {
				if (newBets.size() > _pageSize) {
					oBets = new ArrayList<PlaceBets>(64);
					copyBets(newBets, oBets, _startRecord, _endRecord);
				} else {
					oBets = new ArrayList<PlaceBets>(64);
					copyBets(newBets, oBets, 0, newBets.size());
				}

			} else {
				oBets = new ArrayList<PlaceBets>(64);
				oBets.addAll(cInserts);
			}

			if (!oBets.isEmpty()) {
				for (PlaceBets pbs : oBets) {
					log.info(new StringBuilder(100)
							.append("PlaceBets - AsianLineId=")
							.append(pbs.getAsianLineId()).append(", marketId=")
							.append(pbs.getMarketId()).append(", selectionId=")
							.append(pbs.getSelectionId()).append(", price=")
							.append(pbs.getPrice()).append(", size=")
							.append(pbs.getSize()).append(", betType=")
							.append(pbs.getBetType()).toString());
				}

				try {
					placeBetsResults = ExchangeAPI.placeBets(selected_exchange,
							currentUser.getApiContext(), oBets);
				} catch (Exception e) {
					log.severe(new StringBuilder(100)
							.append("ExchangeAPI.placeBets error: ")
							.append(e.getMessage()).append(", market: ")
							.append(currentMarket.getMenuPath()).append(BS)
							.append(currentMarket.getName()).toString());
				}
			}

			if (oBets.size() != newBets.size())
				bMore = (newBets.size() > _endRecord);

		} while (bMore);

		if (placeBetsResults != null) {
			for (PlaceBetsResult pbr : placeBetsResults) {
				log.info(new StringBuilder(100).append("bet ")
						.append(pbr.getBetId()).append(" placed, status: ")
						.append(pbr.getSuccess()).append(", message: ")
						.append(pbr.getResultCode().getValue())
						.append(", market: ")
						.append(currentMarket.getMenuPath()).append(BS)
						.append(currentMarket.getName()).toString());
			}
		}

	}

	void copyBets(List<PlaceBets> newBets, List<PlaceBets> oBets,
			int _startRecord, int _endRecord) {
		for (int i = _startRecord; i < _endRecord; i++) {
			oBets.add(newBets.get(i));
		}
	}

	private static final int K_MAX_BETS_GET = 100;
	private static final int K_MAX_BETS_SUBMIT = 60;

	private void loadAllActiveMarkets() {
	
			log.fine("* Loading active markets..");
		if (marketService == null) {
			log.log(Level.WARNING, "marketService is null!");
			
		} else {

			int i = 0;
			for (Market market : marketService.listMarkets()) {
				if (!serviceBean.getActiveMarkets().containsKey(
						market.getMarketId())) {

					serviceBean.getActiveMarkets().put(market.getMarketId(),
							market);
					log.info(new StringBuilder(100).append("market added: ")
							.append(market.getMenuPath()).append('/')
							.append(market.getName()).toString());
					i++;
				}
			}
			if (i > 0)
				log.info(i + " active market(s) loaded.");
		}
	}

	private void loadActiveUsers() {

			log.fine("* Loading active users..");
			
		if (marketService == null) {
			log.log(Level.WARNING, "marketService is null!");

		} else {
			int i = 0;
			for (Uzer uzer : marketService.getActiveUsers()) {
				if (!serviceBean.getActiveUsers().containsKey(uzer.getLogin())
						|| serviceBean.getActiveUsers().get(uzer.getLogin())
								.getMarket4Users().size() != uzer
								.getMarket4Users().size()) {
					boolean result = serviceBean.add2ActiveUsers(
							uzer.getLogin(), uzer);
					if (result) {
						log.info(new StringBuilder(100)
								.append("user added ok: ").append(uzer)
								.toString());
						i++;
					}
				}
			}
			if (i > 0)
				log.info(i + " active user(s) loaded.");

		}
	}

	private void processActionRequest(MapMessage message) throws JMSException {
		String act_name = message.getString(ACTION_PROPERTY);
		String market_id = message.getString(MARKET_ID_PROPERTY);
		String login = message.getString(LOGIN_PROPERTY);

		Long marketId = market_id == null ? null : Long.valueOf(market_id);

		if (act_name == null) {
			log.severe("Unknown action!");
			return;
		}

		Action act = Action.valueOf(act_name);
		
			log.fine("given message with action: " + act.toString());

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
				actionUpdateMarketStatus(login, marketId);
				// doUpdateMarketStatus(login, marketId);
				break;
			}

			case UPDATE_MARKET_PRICES: {
				actionUpdateMarketPrices(login, marketId);
				break;
			}

			default: {
				log.severe("Unknown action: " + act.name());
			}
			}
		} catch (Throwable t) {
			log.severe("processActionRequest: " + t);
		}
	}

}
