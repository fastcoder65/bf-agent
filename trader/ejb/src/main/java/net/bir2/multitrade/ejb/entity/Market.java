package net.bir2.multitrade.ejb.entity;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

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

	private long marketId;

	public long getMarketId() {
		return marketId;
	}

	public void setMarketId(long marketId) {
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

	@OneToMany(mappedBy = "linkedMarket", cascade = CascadeType.MERGE)
	// fetch = FetchType.EAGER,
	private Set<Market4User> market4Users = new HashSet<Market4User>(10);

	public Set<Market4User> getMarket4Users() {
		return market4Users;
	}

	public void setMarket4Users(Set<Market4User> market4Users) {
		this.market4Users = market4Users;
	}

	// Feed4Market4User
	@OneToMany(mappedBy = "linkedMarket", cascade = CascadeType.MERGE)
	// fetch = FetchType.EAGER,
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

	@OneToMany(mappedBy = "market", cascade = CascadeType.MERGE)
	// fetch = FetchType.EAGER,
	private Set<Runner> runners = new HashSet<Runner>(20);

	public Set<Runner> getRunners() {
		String runnersInfo = "Market: "
				+ (runners == null ? "runners is null" : "runners.size()="
						+ runners.size());
		log.info(runnersInfo);
		return runners;
	}

	@Transient
	private Map<Long, Runner> runnersMap = null;

	public Map<Long, Runner> getRunnersMap() {
		if (runnersMap == null) {
			runnersMap = new HashMap<Long, Runner>(20);
			for (Runner runner : runners) {

				Long key = Long.valueOf(runner.getSelectionId());
				// System.out.println ("key =" + key);
				runnersMap.put(key, runner);
			}
			// System.out.println ("Market.getRunnersMap(): " +
			// runnersMap.size());
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

	public String toStringFull() {
		return "Market [asianLineId=" + asianLineId + ", canTurnInplay="
				+ canTurnInplay + ", country=" + country + ", delay=" + delay
				+ ", eventTypeId=" + eventTypeId + ", exchange=" + exchange
				+ ", handicap=" + handicap + ", id=" + id
				+ ", marketDescription=" + marketDescription
				+ ", marketDisplayTime=" + marketDisplayTime + ", marketInfo="
				+ marketInfo + ", marketStatus=" + marketStatus
				+ ", marketTime=" + marketTime + ", marketType=" + marketType
				+ ", menuPath=" + menuPath + ", name=" + name
				+ ", noOfWinners=" + noOfWinners + ", numberOfWinners="
				+ numberOfWinners + ", parentEventId=" + parentEventId
				+ ", removedRunners=" + removedRunners + ", runnersMayBeAdded="
				+ runnersMayBeAdded + ", timeZone=" + timeZone + ']';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((asianLineId == null) ? 0 : asianLineId.hashCode());
		result = prime * result + (canTurnInplay ? 1231 : 1237);
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + delay;
		result = prime * result + eventTypeId;
		result = prime * result
				+ ((exchange == null) ? 0 : exchange.hashCode());
		result = prime * result
				+ ((handicap == null) ? 0 : handicap.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime
				* result
				+ ((marketDescription == null) ? 0 : marketDescription
						.hashCode());
		result = prime
				* result
				+ ((marketDisplayTime == null) ? 0 : marketDisplayTime
						.hashCode());
		result = prime * result + (int) (marketId ^ (marketId >>> 32));
		result = prime * result
				+ ((marketInfo == null) ? 0 : marketInfo.hashCode());
		result = prime * result
				+ ((marketStatus == null) ? 0 : marketStatus.hashCode());
		result = prime * result
				+ ((marketTime == null) ? 0 : marketTime.hashCode());
		result = prime * result
				+ ((marketType == null) ? 0 : marketType.hashCode());
		result = prime * result
				+ ((menuPath == null) ? 0 : menuPath.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((noOfWinners == null) ? 0 : noOfWinners.hashCode());
		result = prime * result + numberOfWinners;
		result = prime * result
				+ ((parentEventId == null) ? 0 : parentEventId.hashCode());
		result = prime * result
				+ ((removedRunners == null) ? 0 : removedRunners.hashCode());
		result = prime * result + ((runners == null) ? 0 : runners.hashCode());
		result = prime
				* result
				+ ((runnersMayBeAdded == null) ? 0 : runnersMayBeAdded
						.hashCode());
		result = prime * result
				+ ((timeZone == null) ? 0 : timeZone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Market other = (Market) obj;
		if (asianLineId == null) {
			if (other.asianLineId != null)
				return false;
		} else if (!asianLineId.equals(other.asianLineId))
			return false;
		if (canTurnInplay != other.canTurnInplay)
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (delay != other.delay)
			return false;
		if (eventTypeId != other.eventTypeId)
			return false;
		if (exchange == null) {
			if (other.exchange != null)
				return false;
		} else if (!exchange.equals(other.exchange))
			return false;
		if (handicap == null) {
			if (other.handicap != null)
				return false;
		} else if (!handicap.equals(other.handicap))
			return false;
		if (id != other.id)
			return false;
		if (marketDescription == null) {
			if (other.marketDescription != null)
				return false;
		} else if (!marketDescription.equals(other.marketDescription))
			return false;
		if (marketDisplayTime == null) {
			if (other.marketDisplayTime != null)
				return false;
		} else if (!marketDisplayTime.equals(other.marketDisplayTime))
			return false;
		if (marketId != other.marketId)
			return false;
		if (marketInfo == null) {
			if (other.marketInfo != null)
				return false;
		} else if (!marketInfo.equals(other.marketInfo))
			return false;
		if (marketStatus == null) {
			if (other.marketStatus != null)
				return false;
		} else if (!marketStatus.equals(other.marketStatus))
			return false;
		if (marketTime == null) {
			if (other.marketTime != null)
				return false;
		} else if (!marketTime.equals(other.marketTime))
			return false;
		if (marketType == null) {
			if (other.marketType != null)
				return false;
		} else if (!marketType.equals(other.marketType))
			return false;
		if (menuPath == null) {
			if (other.menuPath != null)
				return false;
		} else if (!menuPath.equals(other.menuPath))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (noOfWinners == null) {
			if (other.noOfWinners != null)
				return false;
		} else if (!noOfWinners.equals(other.noOfWinners))
			return false;
		if (numberOfWinners != other.numberOfWinners)
			return false;
		if (parentEventId == null) {
			if (other.parentEventId != null)
				return false;
		} else if (!parentEventId.equals(other.parentEventId))
			return false;
		if (removedRunners == null) {
			if (other.removedRunners != null)
				return false;
		} else if (!removedRunners.equals(other.removedRunners))
			return false;
		if (runners == null) {
			if (other.runners != null)
				return false;
		} else if (!runners.equals(other.runners))
			return false;
		if (runnersMayBeAdded == null) {
			if (other.runnersMayBeAdded != null)
				return false;
		} else if (!runnersMayBeAdded.equals(other.runnersMayBeAdded))
			return false;
		if (timeZone == null) {
			if (other.timeZone != null)
				return false;
		} else if (!timeZone.equals(other.timeZone))
			return false;
		return true;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Market prefetchAll() {
		log.fine("" + this.getRunners().size());
		for (Runner runner : this.getRunners()) {
			runner.getId();

			log.fine(new StringBuilder(100)
					.append(MessageFormat.format(
							"{0}, runner.getRunner4Users().size()=", runner))
					.append(runner.getRunner4Users().size()).toString());

		}
		return this;
	}
	/*
	 * public Ticket prefetchAll() {
	 * 
	 * for (Signer s : getSigners()) {
	 * 
	 * s.getId();
	 * 
	 * if (s.isItsGroup())
	 * 
	 * for (User u : s.getGroup().getUsers())
	 * 
	 * u.getId();
	 * 
	 * }
	 * 
	 * for (SignResult s : getSignResults()) {
	 * 
	 * s.getId();
	 * 
	 * if (s.isItsGroup())
	 * 
	 * for (User u : s.getGroup().getUsers())
	 * 
	 * u.getId();
	 * 
	 * 
	 * 
	 * }
	 * 
	 * return this;
	 * 
	 * }
	 */

}
