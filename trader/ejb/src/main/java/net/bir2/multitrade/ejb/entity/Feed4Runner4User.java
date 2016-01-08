package net.bir2.multitrade.ejb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.Formula;

@NamedQueries({
	@NamedQuery(name = "GetFeed4Runner4User", query = "select e FROM Feed4Runner4User e inner join e.linkedDataFeedEvent.dataFeed df WHERE e.linkedUser.id = :userId and e.linkedRunner.id = :runnerId and df.name like :likeName")
})

@Entity
@IdClass(Feed4Runner4UserId.class)
public class Feed4Runner4User implements java.io.Serializable {

	private static final long serialVersionUID = 2886355253320702051L;

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

	@Id
	@Column(name = "dataFeedEvent_id", insertable = false, updatable = false)
	private long dataFeedEventId;

	public long getDataFeedEventId() {
		return dataFeedEventId;
	}
	
	protected Feed4Runner4User() {
	}

	public Feed4Runner4User(Uzer uzer, MarketRunner runner, DataFeedEvent dataFeedEvent) {
		this.userId = uzer.getId();
		this.linkedUser = uzer;
		this.runnerId = runner.getId();
		this.linkedRunner = runner;
		this.dataFeedEventId = dataFeedEvent.getId();
		this.linkedDataFeedEvent = dataFeedEvent; 
	}

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Uzer linkedUser;

	public Uzer getLinkedUser() {
		return linkedUser;
	}

	@ManyToOne
	@JoinColumn(name = "runner_id")
	private MarketRunner linkedRunner;

	public MarketRunner getLinkedRunner() {
		return linkedRunner;
	}

	@ManyToOne
	@JoinColumn(name = "dataFeedEvent_id")
	private DataFeedEvent linkedDataFeedEvent;

	public DataFeedEvent getDataFeedEvent() {
		return linkedDataFeedEvent;
	}

	public Double getFeedOdds() {
		return feedOdds;
	}

	public void setFeedOdds(Double feedOdds) {
		this.feedOdds = feedOdds;
	}

	private Double feedOdds;

 	
	private Double retPercent;

	@Formula("(select coalesce(1/f4r4u.feedOdds, 0) from Feed4Runner4User f4r4u  where f4r4u.user_id = userId  and f4r4u.runner_Id=runnerId and f4r4u.dataFeedEvent_id=dataFeedEventId)")
	public Double getRetPercent() {
		return retPercent;
	}
	
	
	
}
