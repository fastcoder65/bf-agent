package net.bir2.multitrade.ejb.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@NamedQueries({
	@NamedQuery(name = "DataFeedEventCountByEventURI", query = "select count(e) FROM DataFeedEvent e WHERE e.uri = :uri"),
	@NamedQuery(name = "DataFeedEventCountByEventName", query = "select count(e) FROM DataFeedEvent e WHERE e.name = :eventName"),
	@NamedQuery(name = "DataFeedEventCountByEventId", query = "select count(e) FROM DataFeedEvent e WHERE e.eventId = :eventId"),
	@NamedQuery(name = "ActiveDataFeedEventById", query = "SELECT dfe FROM DataFeedEvent dfe WHERE  dfe.eventId = :eventId"),
	@NamedQuery(name = "ActiveDataFeedEventByURI", query = "SELECT dfe  FROM DataFeedEvent  dfe WHERE  dfe.uri = :uri"),
	@NamedQuery(name = "ActiveDataFeedEventByName", query = "SELECT dfe  FROM DataFeedEvent dfe  WHERE  dfe.name = :eventName"),
	@NamedQuery(name = "ActiveDataFeedsEventByDataFeedName", query = "SELECT dfe  FROM DataFeedEvent dfe join fetch dfe.dataFeed  WHERE dfe.dataFeed.name like :likeName and dfe.eventTime > :eventTime"),
	@NamedQuery(name = "ActiveDataFeedEventLikeName", query = "SELECT dfe  FROM DataFeedEvent dfe WHERE dfe.name like :likeName")
})
@Entity
public class DataFeedEvent  implements java.io.Serializable {

	private static final long serialVersionUID = -5646934134477675004L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private int eventId;
	
	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	@ManyToOne
	private DataFeed dataFeed;

	public DataFeed getDataFeed() {
		return dataFeed;
	}

	public void setDataFeed(DataFeed dataFeed) {
		this.dataFeed = dataFeed;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String uri;
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Temporal(TemporalType.TIMESTAMP)
	private Date eventTime;
	
	
	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	@Lob
	private String eventContent;

	public String getEventContent() {
		return eventContent;
	}

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

}
