package net.bir.demo;
import generated.exchange.BFExchangeServiceStub.BetCategoryTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetPersistenceTypeEnum;
import generated.exchange.BFExchangeServiceStub.BetTypeEnum;
import generated.exchange.BFExchangeServiceStub.CancelBets;
import generated.exchange.BFExchangeServiceStub.CancelBetsResult;
import generated.exchange.BFExchangeServiceStub.GetAccountFundsResp;
import generated.exchange.BFExchangeServiceStub.MUBet;
import generated.exchange.BFExchangeServiceStub.Market;
import generated.exchange.BFExchangeServiceStub.PlaceBets;
import generated.exchange.BFExchangeServiceStub.PlaceBetsResult;
import generated.exchange.BFExchangeServiceStub.Runner;
import generated.exchange.BFExchangeServiceStub.UpdateBets;
import generated.exchange.BFExchangeServiceStub.UpdateBetsResult;
import generated.global.BFGlobalServiceStub.BFEvent;
import generated.global.BFGlobalServiceStub.EventType;
import generated.global.BFGlobalServiceStub.GetEventsResp;
import generated.global.BFGlobalServiceStub.MarketSummary;
import net.bir2.handler.ExchangeAPI;
import net.bir2.handler.GlobalAPI;
import net.bir2.handler.ExchangeAPI.Exchange;
import net.bir2.multitrade.util.APIContext;
import net.bir.util.*;
import net.bir2.multitrade.util.*;


/** 
 * Demonstration of the Betfair API.
 * 
 * This is the main control class for running the Betfair API demo. 
 * User display and input is handled by the Display class
 * API Management is handled by the classes in the apihandler package 
 */ 
public class APIDemo {

	// Menus
	private static final String[] MAIN_MENU = new String[] 
	    {"View account", "Choose Market", "View Market", "Bet Management", "View Usage", "Exit"};
	   
	private static final String[] BETS_MENU = new String[] 
 	    {"Place Bet", "Update Bet", "Cancel Bet", "Back"};

	// The session token
	private static APIContext apiContext = new APIContext();
	
	// the current chosen market and Exchange for that market
	private static Market selectedMarket;
	private static Exchange selectedExchange;
    private static final MarketSummary[] MARKET_SUMMARY0 = new MarketSummary[0];
    private static final BFEvent[] BF_EVENT0 = new BFEvent[0];

    // Fire up the API demo
	public static void main(String[] args)  throws Exception {
		Display.println("Welcome to the Betfair API Demo");
		String username = args.length < 1 ? Display.getStringAnswer("Betfair username:") : args[0];
		String password = args.length < 2 ? Display.getStringAnswer("Betfair password:") : args[1];
		
		// Perform the login before anything else.
		try
		{
			GlobalAPI.login(apiContext, username, password);
		}
		catch (Exception e)
		{
			// If we can't log in for any reason, just exit.
			Display.showException("*** Failed to log in", e);
			System.exit(1);
		}
		
		boolean finished = false;
		
		while (!finished) {
			try	{
				int choice = Display.getChoiceAnswer("\nChoose an operation", MAIN_MENU);
				switch (choice) {
					case 0: // View account
						showAccountFunds(Exchange.UK);
						showAccountFunds(Exchange.AUS);
						break;
					case 1: // Choose Market 
						chooseMarket();
						break;
					case 2: // View Market 
						viewMarket();
						break;
					case 3: // Show Bets 
						manageBets();
						break;
					case 4: // Show Usage
						int type = Display.getChoiceAnswer("\nType of stats required", new String[] {"Combined", "Timed"});
						if (type == 0) {
							Display.showCombinedUsage(apiContext.getUsage());
						} else {
							Display.showTimedUsage(apiContext.getUsage());
						}
						break;
					case 5: // Exit
						finished = true;
						break;
				}
			} catch (Exception e) {
				// Print out the exception and carry on.
				Display.showException("*** Failed to call API", e);
			}
			
		}
		
		// Logout before shutting down.
		try
		{
			GlobalAPI.logout(apiContext);
		}
		catch (Exception e)
		{
			// If we can't log out for any reason, there's not a lot to do.
			Display.showException("Failed to log out", e);
		}
		Display.println("Logout successful");
	}

	// Check if a market is selected
	private static boolean isMarketSelected() {
		if (selectedMarket == null) {
			Display.println("You must select a market");
			return false;
		}
		return true;
	}

	// Retrieve and display the account funds for the specified exchange
	private static void showAccountFunds(Exchange exch) throws Exception {
		GetAccountFundsResp funds = ExchangeAPI.getAccountFunds(exch, apiContext);
		Display.showFunds(exch, funds);
	}
	
	// Select a market by the following process
	// * Select a type of event
	// * Recursively select an event of this type
	// * Select a market within this event if one exists.
	private static void chooseMarket() throws Exception {
		// Get available event types.
		EventType[] types = GlobalAPI.getActiveEventTypes(apiContext);
		int typeChoice = Display.getChoiceAnswer("Choose an event type:", types);

		// Get available events of this type
		selectedMarket = null;
		int eventId = types[typeChoice].getId();
		while (selectedMarket == null) {
			GetEventsResp resp = GlobalAPI.getEvents(apiContext, eventId);
			
			
			// An event can have both markets and sub-events
			BFEvent[] events = resp.getEventItems().getBFEvent() == null 
									? BF_EVENT0 : resp.getEventItems().getBFEvent();
									
			MarketSummary[] markets = resp.getMarketItems().getMarketSummary() == null 
									? MARKET_SUMMARY0 : resp.getMarketItems().getMarketSummary();

			int choice = Display.getChoiceAnswer("Choose an event or market:", events, markets);
			if (choice == -1) {
				// Probably a coupon event, which are not supported in this demo.				Display.println("No choices available - returning to root");
				eventId = types[typeChoice].getId();
			} else if (choice < events.length) {
				// Always display events before markets, so if it's less that events.length, it's an event.
				eventId = events[choice].getEventId();
			} else {
				// A market. Select it and drop out of the loop
				int marketChoice = choice - events.length;

				// Exchange ID of 1 is the UK, 2 is AUS
				selectedExchange = markets[marketChoice].getExchangeId() == 1 ? Exchange.UK : Exchange.AUS;
				selectedMarket = ExchangeAPI.getMarket(selectedExchange, apiContext, markets[marketChoice].getMarketId());
			}
		}		
	}
	
	// Retrieve and view information about the selected market
	private static void viewMarket() throws Exception {
		if (isMarketSelected()) {
			InflatedMarketPrices prices = ExchangeAPI.getMarketPrices(selectedExchange, apiContext, selectedMarket.getMarketId());
			
			// Now show the inflated compressed market prices.
			Display.showMarket(selectedExchange, selectedMarket, prices);
		}
	}

	// show all my matched and unmatched bets specified market.
	private static void manageBets() throws Exception {
		if (isMarketSelected()) {
			boolean finished = false;
			while (!finished) {
				// show current bets
				MUBet[] bets = ExchangeAPI.getMUBets(selectedExchange, apiContext, selectedMarket.getMarketId());
				Display.showBets(selectedMarket, bets);
				
				int choice = Display.getChoiceAnswer("Choose an operation", BETS_MENU);
				switch (choice) {
					case 0: // Place Bet
						placeBet();
						break;
					case 1: // Update Bet
						updateBet(bets[Display.getIntAnswer("Choose a bet:", 1, bets.length) - 1]);
						break;
					case 2: // Cancel Bet
						cancelBet(bets[Display.getIntAnswer("Choose a bet:", 1, bets.length) - 1]);
						break;
					case 3: // Back
						finished = true;
						break;
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	private static void placeBet() throws Exception {
		if (isMarketSelected()) {
			Runner[] runners = selectedMarket.getRunners().getRunner();
			int choice = Display.getChoiceAnswer("Choose a Runner:", runners);
			
			// Set up the individual bet to be placed
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(selectedMarket.getMarketId());
			bet.setSelectionId(runners[choice].getSelectionId());
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.Factory.fromValue(Display.getStringAnswer("Bet type:")));
			bet.setPrice(Display.getDoubleAnswer("Price:", false));
			bet.setSize(Display.getDoubleAnswer("Size:", false));
			
			if (Display.confirm("This action will actually place a bet on the Betfair exchange")) {
				// We can ignore the array here as we only sent in one bet.
				PlaceBetsResult betResult = ExchangeAPI.placeBets(selectedExchange, apiContext, new PlaceBets[] {bet})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+" placed. "+betResult.getSizeMatched() +" matched @ "+betResult.getAveragePriceMatched());
				} else {
					Display.println("Failed to place bet: Problem was: "+betResult.getResultCode());
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	private static void updateBet(MUBet bet) throws Exception {
		if (isMarketSelected()) {
			double newPrice = Display.getDoubleAnswer("New Price [Unchanged - "+bet.getPrice()+"]:", true);
			double newSize = Display.getDoubleAnswer("New Size [Unchanged - "+bet.getSize()+"]:", true);

			if (newPrice == 0.0d) {
				newPrice = bet.getPrice();
			}
			if (newSize == 0.0d) {
				newSize = bet.getSize();
			}

			// Set up the individual bet to be edited
			UpdateBets upd = new UpdateBets(); 
			upd.setBetId(bet.getBetId());
			upd.setOldBetPersistenceType(bet.getBetPersistenceType());
			upd.setOldPrice(bet.getPrice());
			upd.setOldSize(bet.getSize());
			upd.setNewBetPersistenceType(bet.getBetPersistenceType());
			upd.setNewPrice(newPrice);
			upd.setNewSize(newSize);
			
			if (Display.confirm("This action will actually edit a bet on the Betfair exchange")) {
				// We can ignore the array here as we only sent in one bet.
				UpdateBetsResult betResult = ExchangeAPI.updateBets(selectedExchange, apiContext, new UpdateBets[] {upd})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+" updated. New bet is "+betResult.getNewSize() +" @ "+betResult.getNewPrice());
				} else {
					Display.println("Failed to update bet: Problem was: "+betResult.getResultCode());
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	private static void cancelBet(MUBet bet) throws Exception {
		if (isMarketSelected()) {
			if (Display.confirm("This action will actually cancel a bet on the Betfair exchange")) {
				CancelBets canc = new CancelBets();
				canc.setBetId(bet.getBetId());
				
				// We can ignore the array here as we only sent in one bet.
				CancelBetsResult betResult = ExchangeAPI.cancelBets(selectedExchange, apiContext, new CancelBets[] {canc})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+" cancelled.");
				} else {
					Display.println("Failed to cancel bet: Problem was: "+betResult.getResultCode());
				}
			}
		}
	}
}
