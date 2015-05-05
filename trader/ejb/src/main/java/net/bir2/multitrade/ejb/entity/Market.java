package net.bir2.multitrade.ejb.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

/**
 * Entity implementation class for Entity: Market
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "MarketCountByMarketId", query = "select count(m) FROM Market m where m.marketId = :marketId"),
		@NamedQuery(name = "MarketByMarketId", query = "select m  FROM Market m where m.marketId = :marketId"),
		@NamedQuery(name = "MyActiveMarkets", query = "select m FROM Market m join m.market4Users m4u join m4u.linkedUser u  where m.marketStatus <> :marketStatus and u.login = :login") })

// @NamedNativeQuery(name = "MyActiveMarkets", query =
// "select m.* from market4user m4u join user u on m4u.user_id=u.id join market m on m4u.market_id=m.id where u.login=:login",
// resultClass = Market.class )

public class Market implements java.io.Serializable {

	// private static final Logger log = Logger.getLogger(Market.class);
	@Transient
	@Inject
	private Logger log;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String marketId;

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	private String country;
	private boolean canTurnInplay;
	private int delay;
	private int eventTypeId;
	private String marketDescription;
	private Date marketDisplayTime;
	private String marketStatus;
	private Date marketTime;
	private String marketType;
	private String menuPath;
	private String name;
	private int numberOfWinners;
	private Long parentEventId;
	private Long asianLineId;
	private String handicap;
	private Boolean runnersMayBeAdded;
	private String timeZone;
	private String marketInfo;
	private String removedRunners;
	private Integer noOfWinners;
	private Integer exchange;

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "linkedMarket")
	private Set<Market4User> market4Users = new HashSet<Market4User>(10);

	public Set<Market4User> getMarket4Users() {
		return market4Users;
	}

	public void setMarket4Users(Set<Market4User> market4Users) {
		this.market4Users = market4Users;
	}

	// Feed4Market4User
	@OneToMany(mappedBy = "linkedMarket")
	private Set<Feed4Market4User> feed4Market4Users = new HashSet<Feed4Market4User>(
			10);

	public Set<Feed4Market4User> getFeed4Market4Users() {
		return feed4Market4Users;
	}

	public void setFeed4Market4User(Set<Feed4Market4User> feed4Market4Users) {
		this.feed4Market4Users = feed4Market4Users;
	}

	@Transient
	private Map<Integer, Market4User> userData4Market = null;

	public Map<Integer, Market4User> getUserData4Market() {
		if (userData4Market == null) {
			userData4Market = new HashMap<Integer, Market4User>(10);
			for (Market4User m4u : market4Users) {
				userData4Market.put(m4u.getUserId(), m4u);
			}
		}
		log.info("userData4Market.size()=" + userData4Market.size());
		return userData4Market;
	}

	@OneToMany(mappedBy = "market")
	private Set<Runner> runners = new HashSet<Runner>();

	public Set<Runner> getRunners() {
		return runners;
	}

	@Transient
	private Map<Long, Runner> runnersMap = null;

	public Map<Long, Runner> getRunnersMap() {
		if (runnersMap == null) {
			runnersMap = new HashMap<Long, Runner>(20);
			for (Runner runner : runners) {
				Long key = Long.valueOf(runner.getSelectionId());
				runnersMap.put(key, runner);
			}
		}
		return runnersMap;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isCanTurnInplay() {
		return canTurnInplay;
	}

	public void setCanTurnInplay(boolean canTurnInplay) {
		this.canTurnInplay = canTurnInplay;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(int eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public String getMarketDescription() {
		return marketDescription;
	}

	public void setMarketDescription(String marketDescription) {
		this.marketDescription = marketDescription;
	}

	public Date getMarketDisplayTime() {
		return marketDisplayTime;
	}

	public void setMarketDisplayTime(Date marketDisplayTime) {
		this.marketDisplayTime = marketDisplayTime;
	}

	@Transient
	public String getStatusClass() {
		String statusClass;
		if ("ACTIVE".equals(marketStatus))
			statusClass = "statusGreen";
		else if ("CLOSED".equals(marketStatus))
			statusClass = "statusBlack";
		else
			statusClass = "statusRed";
		return statusClass;
	}

	public String getMarketStatus() {
		return marketStatus;
	}

	public void setMarketStatus(String marketStatus) {
		this.marketStatus = marketStatus;
	}

	public Date getMarketTime() {
		return marketTime;
	}

	public void setMarketTime(Date marketTime) {
		this.marketTime = marketTime;
	}

	public String getMarketType() {
		return marketType;
	}

	public void setMarketType(String marketType) {
		this.marketType = marketType;
	}

	public String getMenuPath() {
		return menuPath;
	}

	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfWinners() {
		return numberOfWinners;
	}

	public void setNumberOfWinners(int numberOfWinners) {
		this.numberOfWinners = numberOfWinners;
	}

	public Long getParentEventId() {
		return parentEventId;
	}

	public void setParentEventId(Long parentEventId) {
		this.parentEventId = parentEventId;
	}

	public Long getAsianLineId() {
		return asianLineId;
	}

	public void setAsianLineId(Long asianLineId) {
		this.asianLineId = asianLineId;
	}

	public String getHandicap() {
		return handicap;
	}

	public void setHandicap(String handicap) {
		this.handicap = handicap;
	}

	public Boolean getRunnersMayBeAdded() {
		return runnersMayBeAdded;
	}

	public void setRunnersMayBeAdded(Boolean runnersMayBeAdded) {
		this.runnersMayBeAdded = runnersMayBeAdded;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getMarketInfo() {
		return marketInfo;
	}

	public void setMarketInfo(String marketInfo) {
		this.marketInfo = marketInfo;
	}

	public String getRemovedRunners() {
		return removedRunners;
	}

	public void setRemovedRunners(String removedRunners) {
		this.removedRunners = removedRunners;
	}

	public Integer getNoOfWinners() {
		return noOfWinners;
	}

	public void setNoOfWinners(Integer noOfWinners) {
		this.noOfWinners = noOfWinners;
	}

	public Integer getExchange() {
		return exchange;
	}

	public void setExchange(Integer exchange) {
		this.exchange = exchange;
	}

	public String toString() {
		return "Market:  id=" + id + ", marketId=" + marketId + ", menuPath="
				+ menuPath + ", name=" + name;
	}

	public Market prefetchAll() {
		Hibernate.initialize(this.getMarket4Users());
		Hibernate.initialize(this.getRunners());
		return this;
	}

}
