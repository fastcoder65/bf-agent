package net.bir2.multitrade.ejb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import java.util.logging.*;
//import org.hibernate.annotations.Formula;

// join e.linkedDataFeedEvent.dataFeed df WHERE e.linkedUser.id = :userId and e.linkedMarket.id = :marketId  and df.name like :likeName
// left join fmu.linkedUser u left join fmu.linkedMarket m
//  WHERE u.id = :userId and m.id = :marketId
/*
 * 	@NamedQuery(name = "CountFeed4Market4User", query =  "select count(n) FROM Feed4Market4User n join n.linkedDataFeedEvent.dataFeed df where df.name like :likeName"),
	@NamedQuery(name = "CountFeed4Market4User5", query = "select count(fmu) FROM Feed4Market4User fmu "),
	@NamedQuery(name = "CountFeed4Market4User4", query = "select count(fmu) FROM Feed4Market4User fmu left join fmu.linkedDataFeedEvent.dataFeed df left join fmu.linkedUser u left join fmu.linkedMarket m WHERE u.id = :userId and m.id = :marketId  and df.name like :likeName"),
	@NamedQuery(name = "CountFeed4Market4User3", query = "select count(fmu) FROM Feed4Market4User fmu left join fmu.linkedDataFeedEvent dfe join dfe.dataFeed df WHERE fmu.linkedUser.id = :userId and fmu.linkedMarket.id = :marketId  and df.name like :likeName"),
	@NamedQuery(name = "CountFeed4Market4User2", query = "select count(fmu) FROM Feed4Market4User as fmu left join fmu.linkedDataFeedEvent.dataFeed as df  WHERE fmu.linkedUser.id = :userId and fmu.linkedMarket.id = :marketId  and df.name like :likeName"),

 */
@NamedQueries({
	@NamedQuery(name = "GetFeed4Market4User", query = "select m4u FROM Feed4Market4User m4u join m4u.linkedDataFeedEvent dfe join dfe.dataFeed df WHERE m4u.linkedUser.id = :userId and m4u.linkedMarket.id = :marketId  and df.name like :likeName ") 
})

/*
@NamedNativeQuery(name = "CountFeed4Market4UserNative", query = "{ select count(fmu.*) FROM Feed4Market4User fmu left join (DataFeedEvent dfe, DataFeed df ) on (fmu.DataFeedEvent_id = dfe.id AND dfe.DataFeed_id=df.id ) WHERE fmu.User_id = :userId and fmu.Market_id = :marketId and df.name like :likeName }")
*/


@Entity
@IdClass(Feed4Market4UserId.class)
public class Feed4Market4User  implements java.io.Serializable {

	private static final long serialVersionUID = 5875088975239377536L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger
	.getLogger(Feed4Market4User.class.getName());
	
	@Id
	@Column(name = "user_id", insertable = false, updatable = false)
	private int userId;

	public int getUserId() {
		return userId;
	}

	@Id
	@Column(name = "market_id", insertable = false, updatable = false)
	private long marketId;

	public long getMarketId() {
		return marketId;
	}

	@Id
	@Column(name = "dataFeedEvent_id", insertable = false, updatable = false)
	private long dataFeedEventId;

	public long getDataFeedEventId() {
		return dataFeedEventId;
	}
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Uzer linkedUser;

	public Uzer getLinkedUser() {
		return linkedUser;
	}

	@ManyToOne
	@JoinColumn(name = "market_id")
	private Market linkedMarket;

	public Market getLinkedMarket() {
		return linkedMarket;
	}
	
	@ManyToOne
	@JoinColumn(name = "dataFeedEvent_id")
	private DataFeedEvent linkedDataFeedEvent;

	public DataFeedEvent getDataFeedEvent() {
		return linkedDataFeedEvent;
	}
	
	protected Feed4Market4User() {
	}
	
	public Feed4Market4User(Uzer uzer, Market market, DataFeedEvent dataFeedEvent) {
		this.userId = uzer.getId();
		this.linkedUser = uzer;
		this.marketId = market.getId();
		this.linkedMarket = market;
		this.dataFeedEventId = dataFeedEvent.getId();
		this.linkedDataFeedEvent = dataFeedEvent; 
	}
	
//	@Formula("(select coalesce(1/sum(1/f4r4u.feedOdds), 0) from Feed4Runner4User f4r4u, MarketRunner r where f4r4u.user_id = userId  and r.id=f4r4u.runner_id and r.market_id=marketId and f4r4u.dataFeedEvent_id=dataFeedEventId )")

	@Transient
	private Double _sumReturnPercent;

	@Formula("(select coalesce(1/sum(1/f4r4u.feedOdds), 0) from Feed4Runner4User f4r4u, MarketRunner r where f4r4u.user_id = userId  and r.id=f4r4u.runner_id and r.market_id=marketId and f4r4u.dataFeedEvent_id=dataFeedEventId )")
	public Double getSumReturnPercent() {
		return _sumReturnPercent;	
	}

	
}
