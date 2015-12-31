package net.bir2.multitrade.ejb.entity;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import java.util.logging.*;

import org.hibernate.annotations.Formula;

@Entity
@IdClass(Market4UserId.class)
public class Market4User implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5769032763323594143L;

	@Transient
	@Inject
    private Logger log;

	@Id
	@Column(name = "market_id", insertable = false, updatable = false)
	private long marketId;

	public long getMarketId() {
		return marketId;
	}

	@Id
	@Column(name = "user_id", insertable = false, updatable = false)
	private int userId;
	
	public int getUserId() {
		return userId;
	}



    public Market4User() {}

	public Market4User(Uzer uzer, Market market, 
			Integer turnOnTimeOffsetHours, 
			Integer turnOnTimeOffsetMinutes, 
			Integer turnOffTimeOffsetHours, 
			Integer turnOffTimeOffsetMinutes, 
			Double maxLossPerSelection) {
		
		this.userId = uzer.getId();
		this.linkedUser = uzer;
		this.marketId = market.getId();
		this.linkedMarket = market;
		
		this.maxLossPerSelection = (uzer.getMaxLossPerSelection() != null ? uzer.getMaxLossPerSelection() : maxLossPerSelection );
		this.volumeStake = uzer.getVolumeStake();
		this.preCosmeticValue =uzer.getPreCosmeticValue();
		
		this.pseudoStakeVolume =uzer.getPseudoStakeVolume();
		this.pseudoPinkStakeVolume = uzer.getPseudoPinkStakeVolume();
		
		this.turnOffTimeOffsetHours  = turnOffTimeOffsetHours;
		this.turnOffTimeOffsetMinutes = turnOffTimeOffsetMinutes;
		
		this.turnOnTimeOffsetHours = turnOnTimeOffsetHours;
		this.turnOnTimeOffsetMinutes = turnOnTimeOffsetMinutes;

	}

	private Boolean onAir = false;
	
	public Boolean isOnAir() {
		return onAir;
	}

	public Boolean getOnAir() {
		return onAir;
	}

	public void setOnAir(Boolean onAir) {
	//	System.out.println ("set on air to \"" + onAir +"\" for " + this.toString());
		this.onAir = onAir;
	}

	private Double  volumeStake;
	
	public Double getVolumeStake() {
		if (volumeStake == null) {
			volumeStake = 1.0; //minVolumeStake;
		}
		return volumeStake;
	}

	public void setVolumeStake(Double volumeStake) {
		this.volumeStake = volumeStake;
	}


	public void setLinkedUser(Uzer linkedUser) {
		
	}

	public void setLinkedMarket(Market linkedMarket) {
		
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

	@Transient
	private Double _sumReturnPercent;

	@Formula("(select coalesce(1/sum(1/r4u.odds), 0) from Runner4User r4u, Runner r where r4u.user_id = userId  and r.id=r4u.runner_id and r.market_id=marketId )")
	public Double getSumReturnPercent() {
		return _sumReturnPercent;	
	}

/*	@Transient
	private Double sumRetPercent = 0.0;

	public Double getSumRetPercent() {
		BigDecimal _result = BigDecimal.valueOf(0);
		for (Runner runner : linkedMarket.getRunners()) {
			if (runner.getUserData4Runner() != null) {
				if (runner.getUserData4Runner().size() > 0) {
					_result = _result.add(BigDecimal.valueOf( runner.getUserData4Runner().get(this.userId).getReturnPercent()));
				} else {
					log.warn("getSumRetPercent(): this.userId=" + this.userId
							+ ", runner.getUserData4Runner().size()==0!");
				}

			} else {
				log.warn("getSumRetPercent(): this.userId=" + this.userId
						+ ", runner.getUserData4Runner() is null!");
			}
		}
		sumRetPercent = 1 / _result.doubleValue();
	    log.warn("sumRetPercent as sum: "+ sumRetPercent);
		return sumRetPercent;
	}
*/
	public Double getSumPrcWin() {
		BigDecimal _result = BigDecimal.valueOf(0);
		for (Runner runner : linkedMarket.getRunners()) {
			if (runner.getUserData4Runner() != null) {
				if (!runner.getUserData4Runner().isEmpty()) {
					_result=_result.add(BigDecimal.valueOf( runner.getUserData4Runner().get(this.userId)
							.getPrcWin()));
				} else {
					log.log(Level.WARNING, new StringBuilder(100).append("getPrcWin(): this.userId=").append(this.userId).append(", runner.getUserData4Runner().size()==0!").toString());
				}

			} else {
				log.log(Level.WARNING, new StringBuilder(100).append("getPrcWin(): this.userId=").append(this.userId).append(", runner.getUserData4Runner() is null!").toString());
			}
		}
		
	  return _result.doubleValue();
	  
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
	
	private Double preCosmeticValue;
	public Double getPreCosmeticValue() {
		
		if (preCosmeticValue == null) {
			preCosmeticValue = 1.0;
		}
		
		return preCosmeticValue;	
	}

	public void setPreCosmeticValue(Double preCosmeticValue) {
		this.preCosmeticValue = preCosmeticValue;
	}
	
	public Double getPseudoStakeVolume() {
		if (pseudoStakeVolume == null)
			pseudoStakeVolume = 1.0; // PSEUDO_STAKE_VOLUME;
		return pseudoStakeVolume;
	}

	public void setPseudoStakeVolume(Double pseudoStakeVolume) {
		this.pseudoStakeVolume = pseudoStakeVolume;
	}

	private Double maxLossPerSelection;

	private Integer turnOnTimeOffsetHours;
	private Integer turnOnTimeOffsetMinutes;
	
	private Integer turnOffTimeOffsetHours;
	private Integer turnOffTimeOffsetMinutes;

	private Double pseudoStakeVolume;
	
	private Double pseudoPinkStakeVolume;
	
	public Double getPseudoPinkStakeVolume() {
		if (pseudoPinkStakeVolume == null)
			pseudoPinkStakeVolume = 1.0 ; // PSEUDOPINKSTAKEVOL;
		return pseudoPinkStakeVolume;
	}

	public void setPseudoPinkStakeVolume(Double pseudoPinkStakeVolume) {
		this.pseudoPinkStakeVolume = pseudoPinkStakeVolume;
	}

	@Transient
	private Double sumPercentWinPink = 0.0;

	public Double getSumPercentWinPink() {
    	BigDecimal _result = BigDecimal.valueOf(0);
		for (Runner runner : linkedMarket.getRunners()) {
			if (runner.getUserData4Runner() != null) {
				if (!runner.getUserData4Runner().isEmpty()) {
					BigDecimal _psr = BigDecimal.valueOf( runner.getUserData4Runner().get(this.userId).getPinkStakeReturn());
					// System.out.println ("_psr=" + _psr);
					_result = _result.add(_psr);
				} else {
					log.log(Level.WARNING, new StringBuilder(100).append("getSumPercentWinPink(): this.userId=").append(this.userId).append(", runner.getUserData4Runner().size()==0!").toString());
				}

			} else {
				log.log(Level.WARNING, new StringBuilder(100).append("getSumPercentWinPink(): this.userId=").append(this.userId).append(", runner.getUserData4Runner() is null!").toString());
			}
		}
			
		sumPercentWinPink =  1 / _result.doubleValue();
		log.info("sumPercentWinPink: "+ sumPercentWinPink);
		return sumPercentWinPink;
	}
	
    @Transient
	public Double getSumPrcWinPinkStakes() {
    	BigDecimal _result = BigDecimal.valueOf(0);
		for (Runner runner : linkedMarket.getRunners()) {
			if (runner.getUserData4Runner() != null) {
				if (!runner.getUserData4Runner().isEmpty()) {
					_result =_result.add(BigDecimal.valueOf(runner.getUserData4Runner().get(this.userId)
							.getPrcWinPinkStakes()));
				} else {
					log.log(Level.WARNING, new StringBuilder(100).append("getSumPrcWinPinkStakes(): this.userId=").append(this.userId).append(", runner.getUserData4Runner().size()==0!").toString());
				}

			} else {
				log.log(Level.WARNING, new StringBuilder(100).append("getSumPrcWinPinkStakes(): this.userId=").append(this.userId).append(", runner.getUserData4Runner() is null!").toString());
			}
		}
	  return _result.doubleValue();	
	}

    @Transient
	public Double getSummOfPsRealStPinkStakeHoldWithNR() {
		BigDecimal _result = BigDecimal.valueOf(0);
		for (Runner runner : linkedMarket.getRunners()) {
			if (runner.getUserData4Runner() != null) {
				if (!runner.getUserData4Runner().isEmpty()) {
					_result =_result.add(BigDecimal.valueOf(runner.getUserData4Runner().get(this.userId)
							.getSumPseudoRealPseudoPinkStakesHoldWithNR()));
				} else {
					log.log(Level.WARNING, new StringBuilder(100).append("getSummOfPsRealStPinkStakeHoldWithNR(): this.userId=").append(this.userId).append(", runner.getUserData4Runner().size()==0!").toString());
				}

			} else {
				log.log(Level.WARNING, new StringBuilder(100).append("getSummOfPsRealStPinkStakeHoldWithNR(): this.userId=").append(this.userId).append(", runner.getUserData4Runner() is null!").toString());
			}
		}
	   return _result.doubleValue();	
	}

	@Transient	
	public Double getSummPercWinSglajivWithNR() {
		BigDecimal _result = BigDecimal.valueOf(0);
		for (Runner runner : linkedMarket.getRunners()) {
			if (runner.getUserData4Runner() != null) {
				if (!runner.getUserData4Runner().isEmpty()) {
					_result =_result.add(BigDecimal.valueOf( runner.getUserData4Runner().get(this.userId)
							.getPercWinSglajivWithNR()));
				} else {
					log.log(Level.WARNING, new StringBuilder(100).append("getSummPercWinSglajivWithNR(): this.userId=").append(this.userId).append(", runner.getUserData4Runner().size()==0!").toString());
				}

			} else {
				log.log(Level.WARNING, new StringBuilder(100).append("getSummPercWinSglajivWithNR(): this.userId=").append(this.userId).append(", runner.getUserData4Runner() is null!").toString());
			}
		}
	  return _result.doubleValue();	
	}
    
}
