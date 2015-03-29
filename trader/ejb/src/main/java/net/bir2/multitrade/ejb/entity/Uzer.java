package net.bir2.multitrade.ejb.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.bir2.multitrade.util.APIContext;

@Entity
@NamedQueries({ 
	@NamedQuery(name = "ActiveUsers", 		query = "select u FROM Uzer u join fetch u.market4Users" ),
	@NamedQuery(name = "Users", 		query = "select u FROM Uzer u order by u.fio" ),
	@NamedQuery(name = "UserByLogin", 	query = "select u FROM Uzer u WHERE u.login = :login ")
	})

public class Uzer implements Serializable {

	@Override
	public String toString() {
		return "Uzer [fio=" + fio + ", id=" + id + ", login=" + login + ']';
	}

	@Transient
	private APIContext apiContext = null;
	
	
	public APIContext getApiContext() {
		return apiContext;
	}

	public void setApiContext(APIContext apiContext) {
		this.apiContext = apiContext;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String login;
	private String password;
	private String exLogin;
	private String exPassword;
	
	@Transient
	private String exLoginDec;
	
	public String getExLoginDec() {
		return exLoginDec;
	}

	public void setExLoginDec(String exLoginDec) {
		this.exLoginDec = exLoginDec;
	}

	@Transient
	private String exPasswordDec;

	public String getExPasswordDec() {
		return exPasswordDec;
	}

	public void setExPasswordDec(String exPasswordDec) {
		this.exPasswordDec = exPasswordDec;
	}

	private static final long serialVersionUID = 1L;
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	@OneToMany(mappedBy="linkedUser", fetch=FetchType.EAGER)
    private Set<Market4User> market4Users = new HashSet<Market4User>(10);

	public Set<Market4User> getMarket4Users() {
		return market4Users;
	}

	public void setMarket4Users(Set<Market4User> market4Users) {
		this.market4Users = market4Users;
	}

	
/*	// Feed4Market4User
	@OneToMany(mappedBy = "linkedUser",  cascade = CascadeType.MERGE) // fetch = FetchType.EAGER,
	private Set<Feed4Market4User> feed4Market4Users = new HashSet<Feed4Market4User>();

	public Set<Feed4Market4User> getFeed4Market4Users() {
		return feed4Market4Users;
	}

	public void setFeed4Market4User(Set<Feed4Market4User> feed4Market4Users) {
		this.feed4Market4Users = feed4Market4Users;
	}
*/
	
	@ManyToMany( cascade=CascadeType.MERGE) // fetch = FetchType.EAGER,
	private Set<UserRole> userRoles = new HashSet<UserRole>(10);
	
	public Set<UserRole> getRoles() {
		return userRoles;
	}

	public void setRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@OneToMany(mappedBy="linkedUser", fetch=FetchType.EAGER)
    private Set<Runner4User> runner4Users = new HashSet<Runner4User>(10);
	
	
	public Set<Runner4User> getUser4Runners() {
		return runner4Users;
	}

	public void setUser4Runners(Set<Runner4User> runner4Users) {
		this.runner4Users = runner4Users;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private String fio;
	private String email;
	
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}   
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}   

	public String getExLogin() {
		return this.exLogin;
	}

	public void setExLogin(String exLogin) {
		this.exLogin = exLogin;
	}
	
	public String getExPassword() {
		return this.exPassword;
	}

	public void setExPassword(String exPassword) {
		this.exPassword = exPassword;
	}

	public Double getMaxLossPerSelection() {
		return maxLossPerSelection;
	}

	public void setMaxLossPerSelection(Double maxLossPerSelection) {
		this.maxLossPerSelection = maxLossPerSelection;
	}

	public Integer getTurnOnTimeOffsetHours() {
		return turnOnTimeOffsetHours;
	}

	public void setTurnOnTimeOffsetHours(Integer turnOnTimeOffsetHours) {
		this.turnOnTimeOffsetHours = turnOnTimeOffsetHours;
	}

	public Integer getTurnOnTimeOffsetMinutes() {
		return turnOnTimeOffsetMinutes;
	}

	public void setTurnOnTimeOffsetMinutes(Integer turnOnTimeOffsetMinutes) {
		this.turnOnTimeOffsetMinutes = turnOnTimeOffsetMinutes;
	}

	public Integer getTurnOffTimeOffsetHours() {
		return turnOffTimeOffsetHours;
	}

	public void setTurnOffTimeOffsetHours(Integer turnOffTimeOffsetHours) {
		this.turnOffTimeOffsetHours = turnOffTimeOffsetHours;
	}

	public Integer getTurnOffTimeOffsetMinutes() {
		return turnOffTimeOffsetMinutes;
	}

	public void setTurnOffTimeOffsetMinutes(Integer turnOffTimeOffsetMinutes) {
		this.turnOffTimeOffsetMinutes = turnOffTimeOffsetMinutes;
	}

	private Double maxLossPerSelection;

	private Integer turnOnTimeOffsetHours;
	private Integer turnOnTimeOffsetMinutes;
	
	private Integer turnOffTimeOffsetHours;
	private Integer turnOffTimeOffsetMinutes;

	private static final Double minVolumeStake = 100.0d;
	private static final Double ODDS_PRECOSMETIC_VAL = 0.55;
	private static final Double PSEUDO_STAKE_VOLUME = 800.0;
	private final static Double PSEUDOPINKSTAKEVOL = 200.0;
	
	public Double getVolumeStake() {
		if (volumeStake == null) 
			volumeStake = minVolumeStake;
		return volumeStake;
	}

	public void setVolumeStake(Double volumeStake) {
		this.volumeStake = volumeStake;
	}

	public Double getPseudoPinkStakeVolume() {
		if (pseudoPinkStakeVolume == null)
			pseudoPinkStakeVolume = PSEUDOPINKSTAKEVOL;
		return pseudoPinkStakeVolume;
	}

	public void setPseudoPinkStakeVolume(Double pseudoPinkStakeVolume) {
		this.pseudoPinkStakeVolume = pseudoPinkStakeVolume;
	}

	public Double getPseudoStakeVolume() {
		if (pseudoStakeVolume == null)
			pseudoStakeVolume = PSEUDO_STAKE_VOLUME;
		
		return pseudoStakeVolume;
	}

	public void setPseudoStakeVolume(Double pseudoStakeVolume) {
		this.pseudoStakeVolume = pseudoStakeVolume;
	}

	public Double getPreCosmeticValue() {
		if (preCosmeticValue == null)
			preCosmeticValue = ODDS_PRECOSMETIC_VAL;
		return preCosmeticValue;
	}

	public void setPreCosmeticValue(Double preCosmeticValue) {
		this.preCosmeticValue = preCosmeticValue;
	}

	private Double  volumeStake; // amount will be divided by odds
	
	private Double pseudoPinkStakeVolume; // pseudo pink stake volume
	
	private Double pseudoStakeVolume; // pseudo stake volume
	
	private Double preCosmeticValue; // pre-cosmetic value
	
}
