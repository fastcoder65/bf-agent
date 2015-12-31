package net.bir2.multitrade.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries( {
	@NamedQuery(name = "ActiveDataFeeds", query = "SELECT df FROM DataFeed df WHERE df.enabled = 1"),
	@NamedQuery(name = "ActiveDataFeedById", query = "SELECT df  FROM DataFeed df WHERE df.enabled = 1 and df.id=:id"),
	@NamedQuery(name = "ActiveDataFeedLikeName", query = "SELECT df  FROM DataFeed df WHERE df.enabled = 1 and df.name like :name")
})

@Entity
public class DataFeed  implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -519693628255399383L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	private Integer enabled;

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	
	
}
