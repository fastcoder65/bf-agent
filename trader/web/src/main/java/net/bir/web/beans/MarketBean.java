package net.bir.web.beans;

import com.betfair.aping.entities.*;
import com.betfair.aping.enums.MatchProjection;
import com.betfair.aping.enums.OrderProjection;
import com.betfair.aping.exceptions.APINGException;
import net.bir.ejb.session.settings.SettingsService;
import net.bir.util.WebUtils;
import net.bir.web.beans.treeModel.Entry;
import net.bir.web.beans.treeModel.EventNode;
import net.bir.web.beans.treeModel.MarketNode;
import net.bir.web.beans.treeModel.SportNode;
import net.bir2.ejb.session.market.BaseServiceBean;
import net.bir2.handler.GlobalAPI;
import net.bir2.multitrade.ejb.entity.*;
import net.bir2.multitrade.ejb.entity.Market;
import net.bir2.multitrade.util.APIContext;

import org.richfaces.component.AbstractExtendedDataTable;
import org.richfaces.component.AbstractTree;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.richfaces.event.TreeToggleEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.richfaces.component.*;


@ManagedBean
@SessionScoped
public class MarketBean extends BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Market currentMarket = null;
    private Market4User market4User = null;

    private APIContext apiContext = null;


    private UIForm runnerForm;

    private UIForm marketForm;

    public UIForm getRunnerForm() {
        return runnerForm;
    }

    public void setRunnerForm(UIForm runnerForm) {
        this.runnerForm = runnerForm;
    }

    public UIForm getMarketForm() {
        return marketForm;
    }

    public void setMarketForm(UIForm marketForm) {
        this.marketForm = marketForm;
    }

    private HashMap<String, Entry> cachedEntries = new HashMap<String, Entry>();


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

    public MarketRunner merge(MarketRunner runner) {
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

    public List<Double> allValidOdds=null;

    public List<Double> getAllValidOdds() {

          if (allValidOdds == null) {
            if (marketService != null && marketService.getServiceBean() != null) {
                log.info("getting valid odds from singleton..");
                allValidOdds = marketService.getServiceBean().getAllValidOdds();
            }
              if (allValidOdds != null)
                  log.info("singleton valid odds found " + allValidOdds.size() + " odds.");

          }
        return allValidOdds;
    }

    public  List<String> oddsAutoComplete(String prefix) {
        log.info("oddsAutoComplete - prefix: " + prefix);

        List<Double> _allValidOdds = getAllValidOdds();

        ArrayList<String> result = new ArrayList<String>();

        if (_allValidOdds == null) {
            log.warning("!! no items selected for autocomplete (");
            return result;
        }
        if ((prefix == null) || (prefix.length() == 0)) {
            for (int i = 0; i < 10; i++) {
                result.add(String.valueOf(_allValidOdds.get(i)));
            }
        } else {
            Iterator<Double> iterator = _allValidOdds.iterator();
            while (iterator.hasNext()) {
                Double elem = iterator.next();
                if ((elem != null && String.valueOf(elem).indexOf(prefix) == 0)
                        || "".equals(prefix)) {
                    result.add(String.valueOf(elem));
                }
            }
        }
        /*
        for (String sItem: result) {
           log.info("autocomplete  selected: " + sItem );
        }
        */
        log.info("autocomplete  selected items: " + result.size() );
        return result;
    }

    public synchronized void toggleListener(TreeToggleEvent ttEvent) {
        UITree tree = (UITree) ttEvent.getSource();
        currentSelection = (TreeNode) tree.getRowData();

        if (currentSelection == null)
            return;

        if (currentSelection instanceof SportNode) {
            SportNode sportNode = (SportNode) currentSelection;

            // getLog().info("sportNode: " + sportNode);

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

                        getLog().fine("(parent is sportNode) sportEvent: " + _comp);

                        sportNode.addEntry(_comp);

                        if (!cachedEntries.containsKey(_comp.getId()))
                            cachedEntries.put(_comp.getId(), _comp);

                    }
                }
            }

            if (listEvents != null) {
                getLog().fine("#listEvents.size()= " + listEvents.size());

                for (EventResult er : listEvents) {

                    if (er != null
                            && er.getEvent().getId() != null
                            && er.getEvent().getId().trim().length() > 0) {

                        EventNode _event = new EventNode(er.getEvent());

                        getLog().fine("(parent is sportNode) sportEvent: " + _event);

                        sportNode.addEntry(_event);

                        if (!cachedEntries.containsKey(_event.getId()))
                            cachedEntries.put(_event.getId(), _event);

                    }
                }
            }
        }

        if (currentSelection instanceof EventNode) {
            EventNode curEventNode = (EventNode) currentSelection;
         //   getLog().info("## current event Node: " + curEventNode);

            Set<String> eventTypeIds = new HashSet<String>();
            Set<String> eventIds = new HashSet<String>();
            Set<String> competitionIds = new HashSet<String>();

            if (curEventNode != null && "competition".equals(curEventNode.getType())) {

                // getLog().info(" added to competitionIds: " + curEventNode.getId());
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
             //   getLog().info("## listEvents.size()= " + listEvents.size());

                int i = 0;
                for (EventResult er : listEvents) {
                    i++;
                    if (er != null
                            && er.getEvent().getId() != null
                            && er.getEvent().getId().trim().length() > 0) {


                        EventNode _eventNode = new EventNode(er.getEvent());

                        curEventNode.addEntry(_eventNode);
                        if (!cachedEntries.containsKey(_eventNode.getId()))
                            cachedEntries.put(_eventNode.getId(), _eventNode);
                    }
                }
            }

            if (curEventNode != null && "event".equals(curEventNode.getType())) {
                List<MarketCatalogue> listMarkets = null;

                if (curEventNode != null && "event".equals(curEventNode.getType())) {

                 //   getLog().info("added to eventIds: " + curEventNode.getId());
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

                        MarketNode aMarketNode = new MarketNode(mc);

                        if (curEventNode != null) {
                            curEventNode.addEntry(aMarketNode);
                            if (!cachedEntries.containsKey(aMarketNode.getId()))
                                cachedEntries.put(aMarketNode.getId(), aMarketNode);
                            //  getLog().info(""+ aMarketNode);
                        }
                    }
            }
        }

        if (currentSelection instanceof MarketNode) {
            MarketNode marketNode = (MarketNode) currentSelection;
         //   getLog().info("marketNode: " + marketNode);


            if (marketNode != null) {
                selectedMarketNode = marketNode;
              //  getLog().info("marketNode is:" + marketNode);
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

                    if (!cachedEntries.containsKey(sport.getId()))
                        cachedEntries.put(sport.getId(), sport);

                    Set<String> eventTypeIds = new HashSet<String>();
                    eventTypeIds.add(String.valueOf(sport.getId()));

                }
            } catch (Exception e) {
                getLog().severe(e.getMessage());
            }

            getLog().fine("sports.getEventTypes().size(): " + allSports.getEventTypes().size());
        }

        return allSports.getEventTypes();
    }

    public List<Market> getActiveMarkets() {

        List<Market> result;

        result = getMarketService().getMyActiveMarkets();
        getLog().fine(
                "getActiveMarkets(): read " + result.size()
                        + " active markets..");

        return result;
    }

    public void setCurrentMarket(Market currentMarket) {
        this.currentMarket = currentMarket;
        getLog().info("this.currentMarket=" + this.currentMarket);
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
                getLog().info(systemSettings.toString());
                settings.setSystemSettings(systemSettings);
                settingsService.saveJPASettings(settings);
                settings = settingsService.getJPASettings();
            }
            getLog().info("load settings: " + settings);
        }
        return settings;
    }

    List<MarketRunner> runners = new ArrayList<MarketRunner>();

    public List<MarketRunner> getRunners() {

        runners.clear();

        if (currentMarket != null) {
            long startTime = System.currentTimeMillis();
            runners.addAll(getMarketService().listRunners(currentMarket.getMarketId()));
            long endTime = System.currentTimeMillis();

            log.fine("runners with price data reloaded, time consumed: " + (endTime - startTime) / 1000.0);

            Collections.sort(runners, new MarketRunner.RunnerComparator());
        }

        return runners;
    }

    private Long selectionId;

    public Long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(Long selectionId) {
        this.selectionId = selectionId;
    }

    public void saveOdds(AjaxBehaviorEvent event) {

        this.setPollEnabled(false);

        log.info("*** saveOdds fired, event: " + event);

        UIAutocomplete uiOddsAutoComplete = (UIAutocomplete) event.getSource();

        Long _selectionId = getSelectionId();

        if (_selectionId == null ) {
            log.info("*** _selectionId is null, odds not saved, exit. ");
            return;
        }

        MarketRunner runner = currentMarket.getRunnersMap().get(_selectionId);
        log.info("runner is : " + runner);

        if (runner != null)
        try {

                    Double _odds = Double.valueOf(uiOddsAutoComplete.getValue().toString());

                    log.info("_odds : " + _odds);

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
                    getLog().info(m4u.toString());

                    //this.setPollEnabled(false);

                    m4u.setOnAir(false);
                    r4u.setOdds(_odds);

                    getLog().info(
                            "** user odds " + _odds + " for runner "
                                    + runner.getSelectionId() + " saved.");
                    getMarketService().merge(m4u);
                    getMarketService().merge(r4u);

        } catch (Exception e) {
            getLog().severe(
                    "Error saving user odds, message: " + e.getMessage());
        }

    }

    private Uzer currentUser = null;

    public Uzer getCurrentUser() {
        return currentUser;
    }

    private String getMenuPath(String marketId) {
        List<String> results = new ArrayList<String>();
        String currentId = marketId;
        Entry foundEntry;
        while ((foundEntry = cachedEntries.get(currentId)) != null) {
            log.fine("foundEntry: " + foundEntry);
            currentId = foundEntry.getParent().getId();
            results.add(foundEntry.getName());
        }
        String menuPath = "";
        for (int i = results.size(); i > 1; i--) {
            menuPath += ("/" + String.valueOf(results.get(i - 1)));
        }

        log.fine("menuPath: " + menuPath);
        return menuPath;
    }

    public void add2ActiveMarkets() throws APINGException {

        if (selectedMarketId == null) {
            getLog().warning("add2ActiveMarkets - selectedMarketId is null!");
            return;
        }

        setPollEnabled(false);

//        MarketCatalogue _mc = (MarketCatalogue) selectedMarketNode.getMarket();

        Set<String> marketIds = new HashSet<String>();
        marketIds.add(selectedMarketId);

        List<MarketCatalogue> _listMarkets = GlobalAPI.getMarkets(apiContext,
                null, marketIds);
        Iterator<MarketCatalogue> mi = _listMarkets.iterator();

        MarketCatalogue mc = (mi.hasNext() ? mi.next() : null);
        getLog().info(" MarketCatalogue: " + mc.getMarketId());

        MarketDescription md = mc.getDescription();
        getLog().info("MarketDescription.rules: " + md.getRules());

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
            for (MarketRunner runner : market2add.getRunners()) {
                Runner4User r4u = new Runner4User(currentUser, runner);
                r4u = getMarketService().merge(r4u);
                runner.getRunner4Users().add(r4u);
                getMarketService().merge(runner);
            }

        } else {
            Market market = new Market();
            market.setName(mc.getMarketName());
            market.setCountry(mc.getEvent().getCountryCode());

            // market.setCountry("country is: "+ mc.getEvent());

            market.setMarketStatus(mb.getStatus());
            // getLog().info(selectedMarket.getMarketDisplayTime().getTime());
            market.setMarketDisplayTime(md.getMarketTime());

            // market.setExchange(mc.getDescription().getRegulator().
            market.setDelay(mb.getBetDelay());
            market.setMarketId(mc.getMarketId());

            // market.setMarketDescription(mc.getDescription().toString());
/*
            TimeZone myTimeZone = java.util.TimeZone.getDefault();
            int offset = myTimeZone.getOffset((new Date()).getTime());
            log.info("myTimeZone is: " + myTimeZone.getDisplayName() + ", offset=" + offset);
*/

            Calendar c = Calendar.getInstance();
            c.setTime(md.getMarketTime());
            c.add(Calendar.MILLISECOND, getServerTimeZoneOffset());
            market.setMarketTime(c.getTime());


            market.setNoOfWinners(mb.getNumberOfWinners()); // selectedMarket.getNumberOfWinners());

            // market.setRunnersMayBeAdded(m
            // selectedMarket.getRunnersMayBeAdded());

            market.setMenuPath(getMenuPath(market.getMarketId()));
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
                    MarketRunner runner = new MarketRunner();

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
        //   setPollEnabled(true);
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
        //  setPollEnabled(true);
    }

    private boolean pollEnabled = true;

    public boolean isPollEnabled() {
        return pollEnabled;
    }

    public void setPollEnabled(boolean pollEnabled) {

        this.pollEnabled = pollEnabled;
        getLog().info("pollEnabled =" + pollEnabled);
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
        //@@@
        Iterator<Object> iterator = masterSelectionItems.iterator();
        while (iterator.hasNext()) {
            Object data = iterator.next();
            log.fine("master data:" + data);

            if (data instanceof Market) {
                Market _market = (Market) data;
                if (getCurrentMarket() == null
                        || !getCurrentMarket().getMarketId().equals(_market.getMarketId())) {

                    setCurrentMarket(_market);

                    runners.clear();

                    getLog().fine("read runners for " + currentMarket.getId());

                    runners.addAll(getMarketService().listRunners(currentMarket.getMarketId()));

                    Collections.sort(runners, new MarketRunner.RunnerComparator());
                    getLog().fine(
                            "method getRunners() completed, " + runners.size()
                                    + " runners found.");
                    break;
                }
            }
        }

    }

    public String getSelectedMarketId() {
        return selectedMarketId;
    }

    public void setSelectedMarketId(String selectedMarketId) {
        this.selectedMarketId = selectedMarketId;
    }

    private String selectedMarketId = null;

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
        //   setPollEnabled(true);
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
                log.fine("call GlobalAPI.keepAlive(" + apiContext + ")");
                GlobalAPI.keepAlive(apiContext);
            } catch (Exception e) {
                log.log(Level.SEVERE, "error on keep-alive request", e);
            }
            log.fine("run finished");
        }
    }

    private String runnerTableState = null;

    private final String CN_RUNNER_TABLE_STATE = "runnerTabelState";

    public String getRunnerTableState() {
        if ( runnerTableState == null) {
            // try to get state from cookies
            Cookie[] cookies = ((HttpServletRequest) FacesContext
                    .getCurrentInstance().getExternalContext().getRequest())
                    .getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals(CN_RUNNER_TABLE_STATE)) {
                        log.info("found state for client table: [" + c.getName() + "=" + c.getValue() + "] ");
                        runnerTableState = c.getValue();
                        break;
                    }
                }
            }
        }
        log.info("tableState: " + runnerTableState);
        return runnerTableState;
    }

    public void setRunnerTableState(String tableState) {
        this.runnerTableState = tableState;
        // save state in cookies
        Cookie stateCookie = new Cookie(CN_RUNNER_TABLE_STATE, this.runnerTableState);
        log.info("save state for client table: [" + stateCookie.getName() + "=" + this.runnerTableState + "]: " );
        stateCookie.setMaxAge(Integer.MAX_VALUE);
        ((HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse()).addCookie(stateCookie);
    }

    private  static Integer serverTimeZoneOffset=null;

    public static Integer getServerTimeZoneOffset() {

        if (serverTimeZoneOffset == null) {
            TimeZone myTimeZone = java.util.TimeZone.getDefault();

            serverTimeZoneOffset = myTimeZone.getOffset((new Date()).getTime());

            // log.info("myTimeZone is: " + myTimeZone.getDisplayName() + ", offset=" + offset);
        }

        return serverTimeZoneOffset;
    }

}
