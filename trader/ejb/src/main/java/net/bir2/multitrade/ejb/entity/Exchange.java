package net.bir2.multitrade.ejb.entity;

import javax.persistence.*;

@NamedQueries( {
	@NamedQuery(name = "ActiveExchanges", query = "SELECT df FROM Exchange df WHERE df.enabled = 1"),
	@NamedQuery(name = "ActiveExchangesById", query = "SELECT df  FROM Exchange df WHERE df.enabled = 1 and df.id=:id")
})

@Entity
public class Exchange implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -519693628255399383L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
