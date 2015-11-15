package net.bir.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.swing.tree.TreeNode;

import com.betfair.aping.entities.*;
import net.bir.ejb.session.settings.SettingsService;
import net.bir.util.WebUtils;
import net.bir.web.beans.treeModel.EventNode;
import net.bir.web.beans.treeModel.MarketNode;
import net.bir.web.beans.treeModel.SportNode;
import net.bir2.ejb.session.market.BaseServiceBean;
import net.bir2.handler.GlobalAPI;
import net.bir2.multitrade.ejb.entity.JPASettings;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.ejb.entity.Market4User;
import net.bir2.multitrade.ejb.entity.Runner;
import net.bir2.multitrade.ejb.entity.Runner4User;
import net.bir2.multitrade.ejb.entity.SystemSettings;
import net.bir2.multitrade.ejb.entity.Uzer;
import net.bir2.multitrade.util.APIContext;

import org.richfaces.component.AbstractExtendedDataTable;
import org.richfaces.component.AbstractTree;
import org.richfaces.component.UITree;
import org.richfaces.component.UITreeNode;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.richfaces.event.TreeToggleEvent;

import com.betfair.aping.enums.MatchProjection;
import com.betfair.aping.enums.OrderProjection;
import com.betfair.aping.exceptions.APINGException;

@ManagedBean
@SessionScoped
public class MarketBean extends BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Market currentMarket = null;
    private Market4User market4User = null;

    private APIContext apiContext = null;

    private TreeNode currentSelection = null;

    public TreeNode getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(TreeNode currentSelection) {
        this.currentSelection = currentSelection;
    }

    @EJB(name = "BIR/SettingsServiceBean/local")
    protected SettingsService settingsService;


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
        logout();
        scheduler.shutdownNow();
    }

    public void logout() {
        if (apiContext != null)
            try {
                String exLogin = currentUser.getExLoginDec();
                GlobalAPI.logout(apiContext);
                apiContext = null;
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
    private SportNode allSports;

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

    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        getLog().info(
                "TREE.selectionChanged - selectionChangeEvent: "
                        + selectionChangeEvent);

        // considering only single selection
        List<Object> selection = new ArrayList<Object>(
                selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        AbstractTree tree = (AbstractTree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        log.info("TREE.selectionChanged - storedKey: " + storedKey);
        tree.setRowKey(currentSelectionKey);
        currentSelection = (TreeNode) tree.getRowData();
        tree.setRowKey(storedKey);

        getLog().info("currentSelection: " + currentSelection);
        SportNode sportNode = null;
        EventNode eventNode = null;
        MarketNode marketNode = null;

        if (currentSelection instanceof SportNode) {
            sportNode = (SportNode) currentSelection;
            getLog().info("sportNode: " + sportNode);
        }

        if (currentSelection instanceof EventNode) {
            eventNode = (EventNode) currentSelection;
            getLog().info("eventNode: " + eventNode);
        }

        if (currentSelection instanceof MarketNode) {
            marketNode = (MarketNode) currentSelection;
            getLog().info("marketNode: " + marketNode);
        }

    }

    public synchronized void toggleListener(TreeToggleEvent ttEvent) {
        UITree tree = (UITree) ttEvent.getSource();
        currentSelection = (TreeNode) tree.getRowData();

        if (currentSelection == null)
            return;

        if (currentSelection instanceof SportNode) {
            SportNode sportNode = (SportNode) currentSelection;
            getLog().info("sportNode: " + sportNode);

            Set<String> eventTypeIds = new HashSet<String>();
            Set<String> eventIds = new HashSet<String>();
            Set<String> competitionIds = new HashSet<String>();

            eventTypeIds.add(String.valueOf(sportNode.getId()));

            List<EventResult> listEvents = null;
            List<CompetitionResult> listCompetitions = null;
            try {

                listCompetitions = GlobalAPI.getCompetitions(apiContext, eventTypeIds, eventIds);

                if (listCompetitions == null || listCompetitions.size() == 0)
                listEvents = GlobalAPI.getEvents(apiContext, eventTypeIds, competitionIds, eventIds);

            } catch (APINGException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (listCompetitions != null) {
                getLog().info("#listCompetitions.size()= " + listCompetitions.size());

                for (CompetitionResult cr : listCompetitions) {

                    if (cr != null
                            && cr.getCompetition().getId() != null
                            && cr.getCompetition().getId().trim().length() > 0) {

                        EventNode _comp = new EventNode(cr.getCompetition());

                        getLog().info("(parent is sportNode) sportEvent: " + _comp);

                        sportNode.addEntry(_comp);

                    }
                }
            }

            if (listEvents != null) {
                getLog().info("#listEvents.size()= " + listEvents.size());

                for (EventResult er : listEvents) {

                    if (er != null
                            && er.getEvent().getId() != null
                            && er.getEvent().getId().trim().length() > 0) {

                        EventNode _event = new EventNode(er.getEvent());

                        getLog().info("(parent is sportNode) sportEvent: " + _event);

                        sportNode.addEntry(_event);

                    }
                }
            }
        }

        if (currentSelection instanceof EventNode) {
            EventNode curEventNode = (EventNode) currentSelection;
            getLog().info("## current event Node: " + curEventNode);

            Set<String> eventTypeIds = new HashSet<String>();
            Set<String> eventIds = new HashSet<String>();
            Set<String> competitionIds = new HashSet<String>();
/*
            if ( curEventNode != null && "event".equals(curEventNode.getType())) {

				getLog().info("added to eventIds: " + curEventNode.getId());
				eventIds.add("" + curEventNode.getId() );

			}
*/
            if (curEventNode != null && "competition".equals(curEventNode.getType())) {

                getLog().info(" added to competitionIds: " + curEventNode.getId());
                competitionIds.add(String.valueOf(curEventNode.getId()));

            }

            List<EventResult> listEvents = null;

            if (eventTypeIds.size() > 0 || competitionIds.size() > 0 || eventIds.size() > 0)

                try {
                    listEvents = GlobalAPI.getEvents(apiContext, eventTypeIds, competitionIds, eventIds);
                } catch (APINGException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            if (listEvents != null) {
                getLog().info("## listEvents.size()= " + listEvents.size());

                int i = 0;
                for (EventResult er : listEvents) {
                    i++;
                    if (er != null
                            && er.getEvent().getId() != null
                            && er.getEvent().getId().trim().length() > 0) {


                        EventNode _eventNode = new EventNode(er.getEvent());

                       // getLog().info(i + ") (parent is eventNode)" + "sport Event: " + _eventNode.getEvent());

                        curEventNode.addEntry(_eventNode);

                    }
                }
            }

            if (curEventNode != null && "event".equals(curEventNode.getType())) {
                List<MarketCatalogue> listMarkets = null;

                if (curEventNode != null && "event".equals(curEventNode.getType())) {

                    getLog().info("added to eventIds: " + curEventNode.getId());
                    eventIds.add("" + curEventNode.getId());

                }

                try {
                    listMarkets = GlobalAPI.getMarkets(apiContext, eventIds, null);
                } catch (APINGException e) {
                    // TODO Auto-generated catch block
                    getLog().log(Level.SEVERE, "error getting markets: ", e);
                }

                if (listMarkets != null)
                    for (MarketCatalogue mc : listMarkets) {
                       // getLog().info("\n market:" + mc.getMarketName());
                        MarketNode aMarketNode = new MarketNode(mc);
                        if (curEventNode != null) {
                            curEventNode.addEntry(aMarketNode);
                          //  getLog().info(""+ aMarketNode);
                        }
                    }
            }
        }

        if (currentSelection instanceof MarketNode) {
            MarketNode marketNode = (MarketNode) currentSelection;
            getLog().info("marketNode: " + marketNode);


            if (marketNode != null) {
                selectedMarketNode = marketNode;
                getLog().info("marketNode is:" + marketNode);
            }
        }
    }

    public static final String NT_SPORT = "S-";
    public static final String NT_EVENT = "E-";
    public static final String NT_MARKET = "M-";
    public static final String NT_GROUP = "G-";
    public static final String NT_COMPETITION = "C-";
    public static final String NT_RACE = "R-";

    public synchronized List<TreeNode> getRootNodes() throws APINGException {
        if (allSports == null) {
            allSports = new SportNode("0", "All sports");
            try {
                int i = 0;
                for (EventTypeResult et : GlobalAPI.getActiveEventTypes(getApiContext())) {
                    i++;
                    SportNode sport = new SportNode(et.getEventType());
                    allSports.addEntry(sport);
                    Set<String> eventTypeIds = new HashSet<String>();
                    eventTypeIds.add(String.valueOf(sport.getId()));

                }
            } catch (Exception e) {
                getLog().severe(e.getMessage());
            }

            getLog().info("sports.getEventTypes().size(): " + allSports.getEventTypes().size());
        }

        return allSports.getEventTypes();
    }

    public synchronized List<Market> getActiveMarkets() {

        List<Market> result;

        result = getMarketService().getMyActiveMarkets();
        getLog().fine(
                "getActiveMarkets(): read " + result.size()
                        + " active markets..");

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
        Iterator<Object> iterator = masterSelectionItems.iterator();
        while (iterator.hasNext()) {
            Object data = iterator.next();
            System.out.println("master data:" + data);

            if (data instanceof Market) {
                Market _market = (Market) data;
                if (getCurrentMarket() == null
                        || getCurrentMarket().getMarketId() != _market
                        .getMarketId()) {
                    setCurrentMarket(_market);

                    runners.clear();
                    getLog().info("read runners for " + currentMarket.getId());

                    runners.addAll(getMarketService().listRunners(
                            currentMarket.getMarketId()));
                    Collections.sort(runners, new Runner.RunnerComparator());
                    getLog().info(
                            "method getRunners() completed, " + runners.size()
                                    + " runners found.");
                    break;
                }
            }
        }
        return runners;
    }

    public synchronized void saveOdds(AjaxBehaviorEvent event) {
        getLog().info(
                "*** saveOdds fired, event: " + event.toString() + ", source:"
                        + event.getSource().toString());

        UIComponent component = event.getComponent();
        UIComponent parentComponent = component.getParent();

        getLog().info("component: " + component);
        getLog().info("parentComponent: " + parentComponent);

        // HtmlAjaxSupport has = (HtmlAjaxSupport) component;
        HtmlInputText inputText = (HtmlInputText) parentComponent;
        // getLog().fine("Processing inputText with id: " + has.getData());
		/*
		 * if (has.getData() != null) { Long _id = (Long) has.getData();
		 */
        Long _id = null;
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
                    Market4User m4u = getCurrentMarket().getUserData4Market()
                            .get(currentUser.getId());
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
        // }
    }

	/*
	 * public synchronized void saveOdds_old(javax.faces.event.ActionEvent
	 * event) { getLog().info( "*** saveOdds fired, event: " + event.toString()
	 * + ", source:" + event.getSource().toString());
	 * 
	 * UIComponent component = event.getComponent(); UIComponent parentComponent
	 * = component.getParent();
	 * 
	 * getLog().info("component: " + component);
	 * getLog().info("parentComponent: " + parentComponent);
	 * 
	 * HtmlAjaxSupport has = (HtmlAjaxSupport) component; HtmlInputText
	 * inputText = (HtmlInputText) parentComponent;
	 * getLog().fine("Processing inputText with id: " + has.getData());
	 * 
	 * if (has.getData() != null) { Long _id = (Long) has.getData(); try { for
	 * (Runner runner : runners) { if (runner.getSelectionId().equals(_id) &&
	 * inputText.getValue() != null) {
	 * 
	 * Double _odds = (Double) inputText.getValue();
	 * 
	 * _odds = (_odds == null || _odds == 0.0) ? BaseServiceBean.FAKE_ODDS :
	 * _odds;
	 * 
	 * Runner4User r4u = runner.getUserData4Runner().get( currentUser.getId());
	 * 
	 * Market4User m4u = getCurrentMarket()
	 * .getUserData4Market().get(currentUser.getId());
	 * getLog().fine(m4u.toString()); this.setPollEnabled(false);
	 * m4u.setOnAir(false); r4u.setOdds(_odds);
	 * 
	 * getLog().info( "** user odds " + _odds + " for runner " +
	 * runner.getSelectionId() + " saved."); getMarketService().merge(m4u);
	 * getMarketService().merge(r4u); break;
	 * 
	 * } } } catch (Exception e) { getLog().severe(
	 * "Error saving user odds, message: " + e.getMessage()); } } }
	 */

    private Uzer currentUser = null;

    public Uzer getCurrentUser() {
        return currentUser;
    }

    public synchronized void addToActiveMarkets(
            javax.faces.event.ActionEvent event) {
        getLog().info("*** enter to double click event handler ***" + event);
        UIComponent component = event.getComponent().getParent();
        getLog().info("component: " + component);
        UITree tree = null; // ((HtmlTreeNode) component).getUITree();
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
        // synchronized (lock) {
        try {
            add2ActiveMarkets();
        } catch (APINGException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // }
    }

    public void add2ActiveMarkets() throws APINGException {
        // generated.exchange.BFExchangeServiceStub.Market selectedMarket =
        // null;

        if (selectedMarketNode == null) {
            getLog().warning("selectedMarketNode is null!");
            return;
        }

        getLog().info(
                "adding to selected markets - id: "
                        + selectedMarketNode.getId() + ", name: "
                        + selectedMarketNode.getName());
        setPollEnabled(false);

        MarketCatalogue _mc = (MarketCatalogue) selectedMarketNode.getMarket();

        Set<String> marketIds = new HashSet<String>();
        marketIds.add(_mc.getMarketId());

        List<MarketCatalogue> _listMarkets = GlobalAPI.getMarkets(apiContext,
                null, marketIds);
        Iterator<MarketCatalogue> mi = _listMarkets.iterator();

        MarketCatalogue mc = (mi.hasNext() ? (MarketCatalogue) mi.next() : null);
        getLog().info("MarketCatalogue: " + mc);

        MarketDescription md = mc.getDescription();
        getLog().info("MarketDescription: " + md);

        // get marketbook there
        List<String> listMarketIds = new ArrayList<String>();
        listMarketIds.add(mc.getMarketId());

        PriceProjection pp = new PriceProjection();
        pp.setVirtualise(false);

        List<MarketBook> listMarketBook = GlobalAPI.listMarketBook(apiContext,
                listMarketIds, pp, OrderProjection.ALL,
                MatchProjection.NO_ROLLUP, "USD");

        Iterator<MarketBook> mbi = listMarketBook.iterator();
        MarketBook mb = (mbi.hasNext() ? mbi.next() : null);

        Uzer currentUser = getMarketService().getCurrentUser();

        boolean alreadySelected = false;
        if (mc != null) {
            alreadySelected = marketService.isMarketAlreadyExistsByMarketId(mc
                    .getMarketId());

        }

        if (alreadySelected) {
            getLog().severe("@@ Market already selected!! @@");
            Market market2add = marketService.getMarketByMarketId(mc
                    .getMarketId());

            market2add
                    .getMarket4Users()
                    .add(new Market4User(currentUser, market2add, getSettings()
                            .getSystemSettings().getTurnOnTimeOffsetHours(),

                            getSettings().getSystemSettings()
                                    .getTurnOnTimeOffsetMinutes(),

                            getSettings().getSystemSettings()
                                    .getTurnOffTimeOffsetHours(),

                            getSettings().getSystemSettings()
                                    .getTurnOffTimeOffsetMinutes(),

                            getSettings().getSystemSettings().getMaxLossPerSelection()));

            getMarketService().merge(market2add);
            for (Runner runner : market2add.getRunners()) {
                Runner4User r4u = new Runner4User(currentUser, runner);
                r4u = getMarketService().merge(r4u);
                runner.getRunner4Users().add(r4u);
                getMarketService().merge(runner);
            }

        } else {
            Market market = new Market();
            market.setName(mc.getMarketName());
            market.setCountry(mc.getEvent().getCountryCode());

            market.setMarketStatus(mb.getStatus());
            // getLog().info(selectedMarket.getMarketDisplayTime().getTime());
            market.setMarketDisplayTime(md.getMarketTime());

            // market.setExchange(mc.getDescription().getRegulator().
            market.setDelay(mb.getBetDelay());
            market.setMarketId(mc.getMarketId());

            // market.setMarketDescription(mc.getDescription().toString());
            market.setMarketTime(md.getMarketTime());
            market.setNoOfWinners(mb.getNumberOfWinners()); // selectedMarket.getNumberOfWinners());
            // market.setRunnersMayBeAdded(m
            // selectedMarket.getRunnersMayBeAdded());
            // market.setMenuPath(//selectedMarket.getMenuPath());
            market.setMarketType(md.getMarketType());
            market.setTimeZone(mc.getEvent().getTimezone());// selectedMarket.getTimezone());
            market = merge(market);
            Set<Market4User> market4users = new HashSet<Market4User>();

            Market4User market4User = null;

            if (currentUser == null || market == null) {
                if (currentUser == null)
                    log.warning("currentUser is null!");

                if (market == null)
                    log.warning("currentUser is null!");

            } else {

                market4User = new Market4User(currentUser, market,
                        getSettings().getSystemSettings()
                                .getTurnOnTimeOffsetHours(), getSettings()
                        .getSystemSettings()
                        .getTurnOnTimeOffsetMinutes(), getSettings()
                        .getSystemSettings()
                        .getTurnOffTimeOffsetHours(), getSettings()
                        .getSystemSettings()
                        .getTurnOffTimeOffsetMinutes(), getSettings()
                        .getSystemSettings().getMaxLossPerSelection());

                market4User = merge(market4User);

                market4users.add(market4User);

                market.setMarket4Users(market4users);

                market = merge(market);
            }

            List<RunnerCatalog> runners = mc.getRunners();
            // .getRunners().getRunner();

            if (runners != null) {
                for (RunnerCatalog rc : runners) {
                    Runner runner = new Runner();

                    runner.setSelectionId((long) rc.getSelectionId());
                    runner.setMarket(market);
                    runner.setName(rc.getRunnerName());
                    runner.setHandicap(rc.getHandicap()); // bfRunner.getHandicap());
                    // runner.setAsianLineId(rc. bfRunner.getAsianLineId());
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
                        mc.getMarketName() + ": no runners found!");
            }
        }
        setPollEnabled(true);
    }

    public void changeSelection() {
        getLog().info("changeSelection fired!");
    }

    // protected SimpleSelection masterSelection = new SimpleSelection();
    private Collection<Object> masterTableSelection;

    public Collection<Object> getMasterTableSelection() {
        return masterTableSelection;
    }

    public void setMasterTableSelection(Collection<Object> masterTableSelection) {
        this.masterTableSelection = masterTableSelection;
        getLog().info(
                "this.masterTableSelection = " + this.masterTableSelection);
        setPollEnabled(true);
    }

    private boolean pollEnabled = true;

    public boolean isPollEnabled() {
        return pollEnabled;
    }

    public void setPollEnabled(boolean pollEnabled) {

        this.pollEnabled = pollEnabled;
        getLog().fine("pollEnabled =" + pollEnabled);
    }

    private List<Object> masterSelectionItems = new ArrayList<Object>();

    public void masterSelectionListener(AjaxBehaviorEvent event) {

        AbstractExtendedDataTable dataTable = (AbstractExtendedDataTable) event
                .getComponent();
        Object originalKey = dataTable.getRowKey();
        masterSelectionItems.clear();
        for (Object selectionKey : masterTableSelection) {
            dataTable.setRowKey(selectionKey);
            if (dataTable.isRowAvailable()) {
                masterSelectionItems.add(dataTable.getRowData());
            }
        }
        dataTable.setRowKey(originalKey);
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
        Iterator<Object> iterator = masterSelectionItems.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Object data = iterator.next();
            if (data instanceof Market) {
                Market _currentMarket = (Market) data;
                Market4User m4u = _currentMarket.getUserData4Market().get(
                        currentUser.getId());
                m4u.setOnAir(b);
                getMarketService().merge(m4u);
            }
            i++;
        }
        getLog().info("setting onAir=\"" + b + "\" for " + i + " markets..");
        setPollEnabled(true);
    }

    public void deleteMarket() {
        getLog().info("*** enter to delete markets...");
        setPollEnabled(false);
        Iterator<Object> iterator = masterSelectionItems.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Object data = iterator.next();
            if (data instanceof Market) {
                Market _currentMarket = (Market) data;
                _currentMarket = getMarketService().getMarket(
                        _currentMarket.getId());
                if (_currentMarket != null) {
                    getMarketService().remove(_currentMarket);
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
		 */
        return this.market4User;
    }

    public void setCurrentMarket4User(Market4User _currentMarket4User) {
        if (_currentMarket4User == null)
            return;
        this.market4User = _currentMarket4User;
        // copyMarketProperties();
        getLog().info("this.market4User set to: " + this.market4User);
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
                log.log(Level.INFO, "call GlobalAPI.keepAlive(" + apiContext
                        + ")");
                GlobalAPI.keepAlive(apiContext);
            } catch (Exception e) {
                log.log(Level.SEVERE, "error on keep-alive request", e);
            }
            log.log(Level.INFO, "run finished");
        }
    }
}
