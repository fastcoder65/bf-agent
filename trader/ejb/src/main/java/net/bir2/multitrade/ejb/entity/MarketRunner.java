package net.bir2.multitrade.ejb.entity;

import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.*;

import java.util.logging.*;

@Entity
@NamedQueries( { 
	@NamedQuery(name = "RunnerBySelectionId", query = "select r FROM MarketRunner r where r.market.id = :marketId and r.selectionId = :selectionId")
	})
@Table(name="runner")
public class MarketRunner implements Serializable {
	
	private static final long serialVersionUID = 3898851916983929758L;

	// private static  final Logger log = Logger.getLogger( MarketRunner.class );
	@Transient
	@Inject
    private Logger log;

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    public MarketRunner() {
        runner4Users = new HashSet<Runner4User>(10);
        feed4Runner4Users = new HashSet<Feed4Runner4User>(10);
    }

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private String name;
	private Double handicap;
	private Integer asianLineId;
	private Long selectionId;

	@Transient 
	private boolean nonRunner = false;
	

	public boolean isNonRunner() {
		return nonRunner;
	}

	public void setNonRunner(boolean nonRunner) {
		this.nonRunner = nonRunner;
	}

	public Long getSelectionId() {
		return selectionId;
	}

	public void setSelectionId(Long selectionId) {
		this.selectionId = selectionId;
	}

	@OneToMany(mappedBy = "linkedRunner", fetch = FetchType.EAGER)
	private   Set<Runner4User> runner4Users;

	public Set<Runner4User> getRunner4Users() {
		return runner4Users;
	}

	public void setRunner4Users(Set<Runner4User> runner4Users) {
		this.runner4Users = runner4Users;
	}

	@OneToMany(mappedBy = "linkedRunner") 
	private   Set<Feed4Runner4User> feed4Runner4Users;

	public Set<Feed4Runner4User> getFeed4Runner4Users() {
		return feed4Runner4Users;
	}

	public void setFeed4Runner4Users(Set<Feed4Runner4User> feed4Runner4Users) {
		this.feed4Runner4Users = feed4Runner4Users;
	}
	
	@Transient
	private   Map<Integer, Runner4User> userData4Runner = null;

	public Map<Integer, Runner4User> getUserData4Runner() {

		if (userData4Runner == null) {
			prefetchAll();

		//	log.info("getUserData4Runner(): runner4Users.size()=" + runner4Users);

			if (runner4Users != null) {
				userData4Runner = new HashMap<Integer, Runner4User>();

				for (Runner4User u4r : runner4Users) {
					userData4Runner.put(u4r.getUserId(), u4r);
				}
			}

		//	log.info("userData4Runner.size()= " + userData4Runner);
		}
		return userData4Runner;
	}

	
	@ManyToOne
	private Market market;

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getHandicap() {
		return handicap;
	}

	public void setHandicap(Double handicap) {
		this.handicap = handicap;
	}

	public Integer getAsianLineId() {
		return asianLineId;
	}

	public void setAsianLineId(Integer asianLineId) {
		this.asianLineId = asianLineId;
	}

	
	@Override
	public String toString() {
		return "MarketRunner [id=" + id + ", name=" + name + ", selectionId="
				+ selectionId + ']';
	}

	public static class RunnerComparator implements Comparator<MarketRunner>, Serializable {
        private static final long serialVersionUID = -4683692958234206933L;

        public int compare(MarketRunner o1, MarketRunner o2) {
		//	return o1.getSelectionId().compareTo(o2.getSelectionId());
			return Long.valueOf(o1.getId()).compareTo(o2.getId());
		}
	}
	
  //  @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MarketRunner prefetchAll() {
		Hibernate.initialize(this.runner4Users);

		if (this.runner4Users == null)
			log.info("this.runner4Users is NULL!");

	return this;
    }

	
}
