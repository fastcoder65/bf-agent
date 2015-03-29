package net.bir2.multitrade.ejb.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class UserRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToMany(mappedBy="userRoles", cascade=CascadeType.MERGE) // fetch = FetchType.EAGER,
	private Set<Uzer> uzers = new HashSet<Uzer>(10);

	public Set<Uzer> getUsers() {
		return uzers;
	}

	public void setUsers(Set<Uzer> uzers) {
		this.uzers = uzers;
	}


}
