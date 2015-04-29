package net.bir.web.beans;

import generated.global.BFGlobalServiceStub.MarketSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import net.bir.ejb.session.settings.SettingsService;
import net.bir.util.WebUtils;
import net.bir.web.beans.treeModel.EventNode;
import net.bir.web.beans.treeModel.MarketNode;
import net.bir.web.beans.treeModel.SportNode;
import net.bir2.ejb.session.market.BaseServiceBean;
import net.bir2.handler.ExchangeAPI;
import net.bir2.handler.ExchangeAPI.Exchange;
import net.bir2.handler.GlobalAPI;
import net.bir2.multitrade.ejb.entity.JPASettings;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Market4User;
import net.bir2.multitrade.ejb.entity.Runner;
import net.bir2.multitrade.ejb.entity.Runner4User;
import net.bir2.multitrade.ejb.entity.SystemSettings;
import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir2.multitrade.util.APIContext;

import org.ajax4jsf.component.html.HtmlAjaxSupport;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlScrollableDataTable;
import org.richfaces.component.html.HtmlTreeNode;
import org.richfaces.component.state.TreeState;
import org.richfaces.event.NodeExpandedEvent;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeRowKey;
import org.richfaces.model.selection.SimpleSelection;

import com.betfair.aping.entities.Event;
import com.betfair.aping.entities.EventTypeResult;
import com.betfair.aping.exceptions.APINGException;

public class MarketBean extends BaseBean {

	private Market currentMarket = null;
	private Market4User market4User = null;

	private APIContext apiContext = null;

	private final Object lock = new Object();

	@EJB(name = "BIR/SettingsServiceBean/local")
	protected SettingsService settingsService;

	/*
	 * private static final BFEvent[] BF_EVENT0 = new BFEvent[0]; private static
	 * final MarketSummary[] MARKET_SUMMARY0 = new MarketSummary[0];
	 */

	private static final String MARKET_BEAN = "marketBean";

	public static MarketBean getInstance() {
		return (MarketBean) WebUtils.getManagedBean(MARKET_BEAN);
	}

	public static MarketBean getInstance(FacesContext context) {
		return (MarketBean) WebUtils.getManagedBean(MARKET_BEAN, context);
	}

	private ScheduledExecutorService scheduler;

	@PostConstruct
	public void init() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@PreDestroy
	public void destroy() {
		scheduler.shutdownNow();
	}

	public void logout () {
		try {
			String exLogin = currentUser.getExLoginDec();
			GlobalAPI.logout(apiContext);
			getLog().info("Uzer " + exLogin + " log out.");
		} catch (Exception e) {
			getLog().log(Level.SEVERE, "Logout failed ", e);		
		}
	}
	
	public APIContext getApiContext() {
		if (apiContext == null) {
			apiContext = new APIContext();
			try {
				if (marketService == null) {
					throw new Exception("marketService is unavailable!");
				}

				currentUser = marketService.getCurrentUser();
				getLog().info("get ApiContext for user: " + currentUser);
				String exLogin = currentUser.getExLoginDec();

				GlobalAPI.login(apiContext, exLogin,
						currentUser.getExPasswordDec());

				scheduler.scheduleAtFixedRate(new KeepAliveJob(apiContext), 1,
						10, TimeUnit.MINUTES);

				getLog().info("Uzer " + exLogin + " log in.");
			} catch (Exception e) {
				getLog().log(Level.SEVERE, "*** Failed to log in: ", e);
			}
		}
		return apiContext;
	}

	private String nodeTitle;
	private SportNode sports;

	// Print some data to the output, appending a carriage return.
	public void println(String value) {
		getLog().info(value);
	}

	// Print some data to the output.
	public void print(String value) {
		getLog().info(value);
	}

	public Boolean adviseNodeOpened(UITree tree) {
		return Boolean.TRUE;
	}

	private MarketNode selectedMarketNode;

	public MarketNode getSelectedMarketNode() {
		return selectedMarketNode;
	}

	public void setSelectedMarketNode(MarketNode selectedMarketNode) {
		this.selectedMarketNode = selectedMarketNode;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	public Market merge(Market market) {
		return getMarketService().merge(market);
	}

	public Runner merge(Runner runner) {
		return getMarketService().merge(runner);
	}

	public Market4User merge(Market4User market4User) {
		return getMarketService().merge(market4User);
	}

	@SuppressWarnings("rawtypes")
	public void changeExpandListener(final NodeExpandedEvent event) {
		setPollEnabled(false);
		getLog().info("*** enter to changeExpandListener ***");
		Object source = event.getSource();
		if (source instanceof HtmlTreeNode) {
			UITree tree = ((HtmlTreeNode) source).getUITree();
			if (tree == null) {
				return;
			}
			// get the row key i.e. id of the given node.
			Object rowKey = tree.getRowKey();
			// Object data = tree.getRowData(rowKey);

			// get the model node of this node.
			TreeRowKey key = (TreeRowKey) tree.getRowKey();
			getLog().fine("key=" + key);
			TreeState state = (TreeState) tree.getComponentState();
			if (state.isExpanded(key)) {
				getLog().fine(rowKey + " - expanded");
			} else {
				getLog().fine(rowKey + " - collapsed");
			}
		}
	}

	public void selectNodeListener(NodeSelectedEvent evt) throws APINGException {
		getLog().info("* enter to nodeSelectListener, event:" + evt);
		setPollEnabled(false);
		Object source = evt.getSource();
		getLog().info("source:" + source);

		UITree tree = ((HtmlTreeNode) source).getUITree();
		if (tree == null) {
			getLog().info("tree == null ! ");
			return;
		}

		if (source instanceof HtmlTreeNode) {
			Object rowData = tree.getRowData();
			getLog().info("rowData is: " + rowData);
			SportNode sportNode = null;
			EventNode eventNode = null;
			MarketNode marketNode = null;

			if (rowData instanceof SportNode) {
				sportNode = (SportNode) rowData;
				getLog().info("sportNode: " + sportNode);
			}

			if (rowData instanceof EventNode) {
				eventNode = (EventNode) rowData;
				getLog().info("eventNode: " + eventNode);
			}

			if (rowData instanceof MarketNode) {
				marketNode = (MarketNode) rowData;
				getLog().info("marketNode: " + marketNode);
			}

			if (marketNode != null) {
				selectedMarketNode = marketNode;
				getLog().info("marketNode is:" + marketNode);
			} else {

				try {
					getLog().info("else...");
					Set<String> eventTypeIds = new HashSet<String>();

					if (sportNode != null) {
						eventTypeIds.add(String.valueOf(sportNode.getId()));
					}

					getLog().info("eventTypeIds.size()= " + eventTypeIds.size());

					Set<String> eventIds = new HashSet<String>();

					if (eventNode != null) {
						eventIds.add(String.valueOf(eventNode.getId()));
					}
					getLog().info("eventIds.size()= " + eventIds.size());

					List<Event> listEvents = GlobalAPI.getEvents(apiContext,
							eventTypeIds, eventIds);

					/*
					 * BFEvent[] bfEvents = resp.getEventItems().getBFEvent() ==
					 * null ? BF_EVENT0 : resp.getEventItems().getBFEvent();
					 * 
					 * MarketSummary[] bfMarkets = resp.getMarketItems()
					 * .getMarketSummary() == null ? MARKET_SUMMARY0 :
					 * resp.getMarketItems().getMarketSummary();
					 */

					if (listEvents != null) {
						getLog().info(
								"## listEvents.size()= " + listEvents.size());
						int i = 0;
						for (Event bfEvent : listEvents) {
							i++;
							log.info(i + ")");

							if (bfEvent != null && bfEvent.getId() != null
									&& bfEvent.getId().trim().length() > 0) {

								getLog().info(
										i + ") " + "sportEvent: " + bfEvent);

								EventNode sportEvent = new EventNode(bfEvent);

								if (sportNode != null)
									sportNode.addEntry(sportEvent);

								if (eventNode != null)
									eventNode.addEntry(sportEvent);

							}
						}
					}
					/*
					 * for (MarketSummary bfMarket : bfMarkets) {
					 * getLog().fine("\t market:" + bfMarket.getMarketName());
					 * MarketNode aMarketNode = new MarketNode(bfMarket); if
					 * (eventNode != null) eventNode.addEntry(aMarketNode); }
					 */
					// Collections.sort(eventNode.getMarkets(), new
					// MarketNode.MarketNodeComparator());
				} catch (Exception e) {
					getLog().log(Level.SEVERE, "error in selectNodeListener: ",
							e);
				}
			} // if marketnode != null
		}
	}

	public static final String NT_SPORT = "S-";
	public static final String NT_EVENT = "E-";
	public static final String NT_MARKET = "M-";

	@SuppressWarnings("rawtypes")
	public Map<Object, TreeNode> getSportNodes() throws APINGException {
		if (sports == null) {
			sports = new SportNode(0, "All sports");
			try {
				for (EventTypeResult et : GlobalAPI
						.getActiveEventTypes(getApiContext())) {
					SportNode sport = new SportNode(et.getEventType());
					sports.addEntry(sport);
				}
			} catch (Exception e) {
				getLog().severe(e.getMessage());
			}
		}
		return sports.getEventTypes();
	}

	public List<Market> getActiveMarkets() {

		List<Market> result;
		synchronized (lock) {
			result = getMarketService().getMyActiveMarkets();
			getLog().fine(
					"getActiveMarkets(): read " + result.size()
							+ " active markets..");
		}
		return result;
	}

	public void setCurrentMarket(Market currentMarket) {
		this.currentMarket = currentMarket;
		getLog().fine("this.currentMarket=" + this.currentMarket);
		if (this.currentMarket != null) {
			Market4User m4u = this.currentMarket.getUserData4Market().get(
					getCurrentUser().getId());
			setCurrentMarket4User(m4u);
		}
	}

	public Market getCurrentMarket() {
		return currentMarket;
	}

	private JPASettings settings;

	public JPASettings getSettings() {
		if (settings == null) {
			settings = settingsService.getJPASettings();
			if (settings == null) {
				getLog().fine("initialize system settings..");
				settings = new JPASettings();
				SystemSettings systemSettings = new SystemSettings();
				getLog().fine(systemSettings.toString());
				settings.setSystemSettings(systemSettings);
				settingsService.saveJPASettings(settings);
				settings = settingsService.getJPASettings();
			}
			getLog().info("load settings: " + settings);
		}
		return settings;
	}

	List<Runner> runners = new ArrayList<Runner>();

	public List<Runner> getRunners() {
		Iterator<Object> iterator = masterSelection.getKeys();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			getLog().fine("master key is:" + key);

			masterTable.setRowKey(key);
			if (masterTable.isRowAvailable()) {
				Object data = masterTable.getRowData();
				// System.out.println("master data:" + data);

				if (data instanceof Market) {
					Market _market = (Market) data;
					if (getCurrentMarket() == null
							|| getCurrentMarket().getMarketId() != _market
									.getMarketId()) {
						setCurrentMarket(_market);

						runners.clear();
						getLog().info(
								"read runners for " + currentMarket.getId());

						runners.addAll(getMarketService().listRunners(
								currentMarket.getMarketId()));
						Collections
								.sort(runners, new Runner.RunnerComparator());
						getLog().info(
								"method getRunners() completed, "
										+ runners.size() + " runners found.");
						break;
					}
				}
			}
		}
		return runners;
	}

	public void saveOdds(javax.faces.event.ActionEvent event) {
		getLog().fine(
				"*** saveOdds fired, event: " + event.toString() + ", source:"
						+ event.getSource().toString());
		UIComponent component = event.getComponent();
		UIComponent parentComponent = component.getParent();

		getLog().fine("component: " + component);
		getLog().fine("parentComponent: " + parentComponent);

		HtmlAjaxSupport has = (HtmlAjaxSupport) component;
		HtmlInputText inputText = (HtmlInputText) parentComponent;
		getLog().fine("Processing inputText with id: " + has.getData());

		if (has.getData() != null) {
			Long _id = (Long) has.getData();
			try {
				for (Runner runner : runners) {
					if (runner.getSelectionId().equals(_id)
							&& inputText.getValue() != null) {

						Double _odds = (Double) inputText.getValue();

						_odds = (_odds == null || _odds == 0.0) ? BaseServiceBean.FAKE_ODDS
								: _odds;

						Runner4User r4u = runner.getUserData4Runner().get(
								currentUser.getId());
						/*
						 * Market4User m4u = r4u.getLinkedRunner().getMarket()
						 * .getUserData4Market().get(currentUser.getId());
						 */
						Market4User m4u = getCurrentMarket()
								.getUserData4Market().get(currentUser.getId());
						getLog().fine(m4u.toString());
						this.setPollEnabled(false);
						m4u.setOnAir(false);
						r4u.setOdds(_odds);

						getLog().info(
								"** user odds " + _odds + " for runner "
										+ runner.getSelectionId() + " saved.");
						getMarketService().merge(m4u);
						getMarketService().merge(r4u);
						break;

					}
				}
			} catch (Exception e) {
				getLog().severe(
						"Error saving user odds, message: " + e.getMessage());
			}
		}
	}

	private Uzer currentUser = null;

	public Uzer getCurrentUser() {
		return currentUser;
	}

	public void addToActiveMarkets(javax.faces.event.ActionEvent event) {
		getLog().info("*** enter to double click event handler ***" + event);
		UIComponent component = event.getComponent().getParent();
		getLog().info("component: " + component);
		UITree tree = ((HtmlTreeNode) component).getUITree();
		if (tree == null) {
			return;
		}
		Object rowData = tree.getRowData();
		MarketNode marketNode = null;
		if (rowData instanceof MarketNode) {
			marketNode = (MarketNode) rowData;
		}
		if (marketNode != null) {
			selectedMarketNode = marketNode;
			getLog().info("marketNode is:" + marketNode);
		}
		synchronized (lock) {
			add2ActiveMarkets();
		}
	}

	public void add2ActiveMarkets() {
		generated.exchange.BFExchangeServiceStub.Market selectedMarket = null;

		if (selectedMarketNode == null)
			return;

		getLog().info(
				"adding to selected markets - id: "
						+ selectedMarketNode.getId() + ", name: "
						+ selectedMarketNode.getName());
		setPollEnabled(false);
		MarketSummary ms = (MarketSummary) selectedMarketNode.getData();
		Exchange selectedExchange = ms.getExchangeId() == 1 ? Exchange.UK
				: Exchange.AUS;
		try {

			selectedMarket = ExchangeAPI.getMarket(selectedExchange,
					getApiContext(), ((MarketSummary) selectedMarketNode
							.getData()).getMarketId());

		} catch (Exception e) {
			getLog().severe(
					"Error selecting market, messsage:" + e.getMessage());
		}

		Uzer currentUser = getMarketService().getCurrentUser();
		boolean alreadySelected = false;
		if (selectedMarket != null) {
			alreadySelected = marketService
					.isMarketAlreadyExistsByMarketId(selectedMarket
							.getMarketId());
		}
		if (alreadySelected) {
			getLog().severe("@@ Market already selected!! @@");
			Market market2add = marketService
					.getMarketByMarketId(selectedMarket.getMarketId());
			market2add.getMarket4Users().add(
					new Market4User(currentUser, market2add, getSettings()
							.getSystemSettings().getTurnOnTimeOffsetHours(),
							getSettings().getSystemSettings()
									.getTurnOnTimeOffsetMinutes(),
							getSettings().getSystemSettings()
									.getTurnOffTimeOffsetHours(), getSettings()
									.getSystemSettings()
									.getTurnOffTimeOffsetMinutes(),
							getSettings().getSystemSettings()
									.getMaxLossPerSelection()));

			getMarketService().merge(market2add);
			for (Runner runner : market2add.getRunners()) {
				Runner4User r4u = new Runner4User(currentUser, runner);
				r4u = getMarketService().merge(r4u);
				runner.getRunner4Users().add(r4u);
				getMarketService().merge(runner);
			}

		} else {
			Market market = new Market();
			market.setName(selectedMarket.getName());
			market.setCountry(selectedMarket.getCountryISO3());
			market.setMarketStatus(selectedMarket.getMarketStatus().getValue());
			// getLog().info(selectedMarket.getMarketDisplayTime().getTime());
			market.setMarketDisplayTime(selectedMarket.getMarketDisplayTime()
					.getTime());
			market.setExchange(ms.getExchangeId());
			market.setDelay(ms.getBetDelay());
			market.setMarketId(selectedMarket.getMarketId());
			// market.setId(selectedMarket.getMarketId());
			// market.setMarketDescription(selectedMarket.getMarketDescription());
			market.setMarketTime(selectedMarket.getMarketTime().getTime());
			market.setNoOfWinners(selectedMarket.getNumberOfWinners());
			market.setRunnersMayBeAdded(selectedMarket.getRunnersMayBeAdded());
			market.setMenuPath(selectedMarket.getMenuPath());
			market.setMarketType(selectedMarket.getMarketType().getValue());
			market.setTimeZone(selectedMarket.getTimezone());
			market = merge(market);
			Set<Market4User> market4users = new HashSet<Market4User>();

			Market4User market4User = new Market4User(currentUser, market,

			getSettings().getSystemSettings().getTurnOnTimeOffsetHours(),
					getSettings().getSystemSettings()
							.getTurnOnTimeOffsetMinutes(),

					getSettings().getSystemSettings()
							.getTurnOffTimeOffsetHours(), getSettings()
							.getSystemSettings().getTurnOffTimeOffsetMinutes(),

					getSettings().getSystemSettings().getMaxLossPerSelection());

			market4User = merge(market4User);
			market4users.add(market4User);
			market.setMarket4Users(market4users);
			market = merge(market);

			generated.exchange.BFExchangeServiceStub.Runner[] runners = selectedMarket
					.getRunners().getRunner();

			if (runners != null) {
				for (generated.exchange.BFExchangeServiceStub.Runner bfRunner : runners) {
					Runner runner = new Runner();

					runner.setSelectionId((long) bfRunner.getSelectionId());
					runner.setMarket(market);
					runner.setName(bfRunner.getName());
					runner.setHandicap(bfRunner.getHandicap());
					runner.setAsianLineId(bfRunner.getAsianLineId());
					runner = getMarketService().merge(runner);
					// System.out.println("==runner " + runner + " merged!");
					Runner4User r4u = new Runner4User(currentUser, runner);
					r4u = getMarketService().merge(r4u);
					Set<Runner4User> runner4users = new HashSet<Runner4User>();
					runner4users.add(r4u);
					runner.setRunner4Users(runner4users);
					getMarketService().merge(runner);
					// getLog().info("saved runner: " + runner);
				}
			} else {
				getLog().log(Level.WARNING,
						selectedMarket.getName() + ": no runners found!");
			}
		}
		setPollEnabled(true);
	}

	public void changeSelection() {
		getLog().info("changeSelection fired!");
	}

	protected SimpleSelection masterSelection = new SimpleSelection();

	protected HtmlScrollableDataTable masterTable = null;

	public HtmlScrollableDataTable getMasterTable() {
		return masterTable;
	}

	public void setMasterTable(HtmlScrollableDataTable masterTable) {
		this.masterTable = masterTable;
	}

	private boolean pollEnabled = true;

	public boolean isPollEnabled() {
		return pollEnabled;
	}

	public void setPollEnabled(boolean pollEnabled) {

		this.pollEnabled = pollEnabled;
		getLog().fine("pollEnabled =" + pollEnabled);
	}

	public SimpleSelection getMasterSelection() {
		return masterSelection;
	}

	public void setMasterSelection(SimpleSelection masterSelection) {
		this.masterSelection = masterSelection;
		getLog().fine("this.masterSelection =" + this.masterSelection);
		setPollEnabled(true);
	}

	public long getSelectedMarketId() {
		return selectedMarketId;
	}

	public void setSelectedMarketId(long selectedMarketId) {
		this.selectedMarketId = selectedMarketId;
	}

	private long selectedMarketId = 0;

	public void toggleRefresh() {
		boolean b = this.pollEnabled;
		setPollEnabled(!b);
	}

	public void setOnAirOn() {
		setOnAir(true);
	}

	public void setOnAirOff() {
		setOnAir(false);
	}

	public void setOnAir(boolean b) {
		getLog().info("*** enter to switch OnAir...");
		setPollEnabled(false);
		Iterator<Object> iterator = masterSelection.getKeys();
		int i = 0;
		while (iterator.hasNext()) {
			Object key = iterator.next();
			masterTable.setRowKey(key);
			if (masterTable.isRowAvailable()) {
				Object data = masterTable.getRowData();
				if (data instanceof Market) {
					Market _currentMarket = (Market) data;
					Market4User m4u = _currentMarket.getUserData4Market().get(
							currentUser.getId());
					m4u.setOnAir(b);
					getMarketService().merge(m4u);
				}
			}
			i++;
		}
		getLog().info("setting onAir=\"" + b + "\" for " + i + " markets..");
		setPollEnabled(true);
	}

	public void deleteMarket() {
		getLog().info("*** enter to delete markets...");
		setPollEnabled(false);
		Iterator<Object> iterator = masterSelection.getKeys();
		int i = 0;
		while (iterator.hasNext()) {
			Object key = iterator.next();
			masterTable.setRowKey(key);
			if (masterTable.isRowAvailable()) {
				Object data = masterTable.getRowData();
				if (data instanceof Market) {
					Market _currentMarket = (Market) data;
					_currentMarket = getMarketService().getMarket(
							_currentMarket.getId());
					if (_currentMarket != null) {
						getMarketService().remove(_currentMarket);
					}
				}
			}
			i++;
		}
		currentMarket = null;
		runners.clear();
		getLog().info(i + " markets were deleted");
		setPollEnabled(true);
	}

	public Market4User getCurrentMarket4User() {
		/*
		 * if (this.market4User == null) { //
		 * getLog().info("market4User is null! " + this.hashCode()); } else { //
		 * getLog().info("market4User is NOT null! " + this.hashCode()); }
		 */return this.market4User;
	}

	public void setCurrentMarket4User(Market4User _currentMarket4User) {
		if (_currentMarket4User == null)
			return;
		this.market4User = _currentMarket4User;
		// copyMarketProperties();
		getLog().fine("this.market4User set to: " + this.market4User);
	}

	public String customMyMarketProperties() {
		getLog().fine("* edit my market properties: " + getCurrentMarket4User());
		setPollEnabled(false);

		return null;
	}

	private boolean runnersOpened = false;

	public boolean isRunnersOpened() {
		return runnersOpened;
	}

	public void setRunnersOpened(boolean runnersOpened) {
		this.runnersOpened = runnersOpened;
	}

	private Set<Object> ajaxSetRunners;

	public Set<Object> getAjaxSetRunners() {
		return ajaxSetRunners;
	}

	public void setAjaxSetRunners(Set<Object> ajaxSetRunners) {
		this.ajaxSetRunners = ajaxSetRunners;
	}

	public String getServerTime() {
		return timeFormat.format(new Date());
	}

	public static String currencyFormat = "#,##0.0#";

	public String getCurrencyFormat() {
		return currencyFormat;
	}

	public String getCurrencyFormatValue() {
		return "{0}{1,number," + getCurrencyFormat() + "}";
	}

	public String saveMyMarketProperties() {
		getLog().info("*** enter to save my market properties...");
		if (getCurrentMarket() == null)
			return null;
		getLog().info("*** saving my market properties...");
		Market4User m4u = getCurrentMarket4User();
		m4u = getMarketService().merge(m4u);
		setCurrentMarket4User(m4u);
		getLog().info(m4u.toString());
		setPollEnabled(true);
		getLog().info("*** my market properties saved.");
		return null;
	}

	public String getToggleTree() {
		return toggleTree;
	}

	public void setToggleTree(String toggleTree) {
		this.toggleTree = toggleTree;
	}

	private String toggleTree = "on";

	/*
	 * 
	 * To access the SSL session ID from the request, use: String sslID =
	 * (String)request.getAttribute("javax.servlet.request.ssl_session"); For
	 * additional discussion on this area, please see Bugzilla.
	 */

	class KeepAliveJob implements Runnable {

		private final Logger log = Logger.getLogger(this.getClass().getName());

		private APIContext apiContext;

		public KeepAliveJob(APIContext apiContext) {
			this.apiContext = apiContext;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				log.log(Level.INFO, "call GlobalAPI.keepAlive(" + apiContext+ ")");
				GlobalAPI.keepAlive(apiContext);
			} catch (Exception e) {
				log.log(Level.SEVERE, "error on keep-alive request", e);
			}
			log.log(Level.INFO, "run finished");
		}
	}
}
