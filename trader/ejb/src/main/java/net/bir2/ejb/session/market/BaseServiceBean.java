package net.bir2.ejb.session.market;

import java.math.BigDecimal;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.bir2.ejb.action.Action;
import net.bir2.ejb.action.ShedulerActivity;
import net.bir2.multitrade.ejb.entity.ValidOdds;

// import org.apache.log4j.Logger;

@Stateless
@Local( { BaseService.class })
public class BaseServiceBean implements BaseService, TimedObject {

	@Inject
    private Logger log;

	@Resource
	private EJBContext context;

	@PersistenceContext
	protected EntityManager em;

	@EJB
	ShedulerActivity serviceBean;
	
//	protected final Logger LOG = Logger.getLogger(this.getClass());

	public static final double MIN_ODDS = 1.01;
	public static final double MAX_ODDS = 1000.0;
	public static final double FAKE_ODDS = 1.0;
	public static final double FAKE_STAKE = 1.0;
	public static final double MIN_STAKE_AMOUNT = 4.0;
	
	private static final String  GetValidOddsRange = " select v FROM ValidOdds v where :value between v.lowMargin and v.highMargin ";

	private static final String GetMinLowMargin = " select min(vo.lowMargin) FROM ValidOdds vo where vo.lowMargin > :value ";

	private ValidOdds getValidOddsRange(Double rawValue) {
		return (ValidOdds) em.createQuery(GetValidOddsRange)
				.setParameter("value", rawValue).getSingleResult();
	}

	private static final String GetValidOddsRangeCount =  " select count(v) FROM ValidOdds v where :value between v.lowMargin and v.highMargin ";

	private Long getValidOddsRangeCount(Double value) {
		return (Long) em.createQuery(GetValidOddsRangeCount)
				.setParameter("value", value).getSingleResult();
	}

	private static final String GetMaxHighMargin = " select max(v.highMargin) FROM ValidOdds v where v.highMargin < :value ";

	private Double getMaxHighMargin(Double value) {
		return (Double) em.createQuery(GetMaxHighMargin)
				.setParameter("value", value)
				.getSingleResult();
	}

	private Double getMinLowMargin(Double value) {
		return (Double) em.createQuery(GetMinLowMargin)
				.setParameter("value", value)
				.getSingleResult();
	}

	public Double getUpOdds(Double cOdds) {
		Double result = MIN_ODDS;
		if (cOdds == null)
			return result;

		ValidOdds validOdds = getValidOddsRange(cOdds);

		if (validOdds != null) {
			if (cOdds < validOdds.getHighMargin()) {
				result = BigDecimal.valueOf(cOdds).add( BigDecimal.valueOf(validOdds.getStep() )).doubleValue();
			} else {
				if (validOdds.getHighMargin() < MAX_ODDS) {
					result = getMinLowMargin(validOdds.getLowMargin());
				} else {
					result = MAX_ODDS;
				}
			}
		}
	
		log.fine("getUpOdds(): cOdds=" + cOdds + ", result=" + result);
		return result;
	}

	public Double getDownOdds(Double cOdds) {
		Double result = MIN_ODDS;
		if (cOdds == null)
			return result;

		ValidOdds validOdds = getValidOddsRange(cOdds);
		if (validOdds != null) {
			if (cOdds > validOdds.getLowMargin()) {
				result = BigDecimal.valueOf(cOdds).subtract( BigDecimal.valueOf(validOdds.getStep())).doubleValue();
			} else {
				if (validOdds.getLowMargin() > MIN_ODDS) {
					result = getMaxHighMargin(validOdds.getLowMargin());
				} else {
					result = MIN_ODDS;
				}
			}
		}
		
		log.fine("getDownOdds(): cOdds=" + cOdds + ", result="
				+ result);
		return result;
	}

	// get the nearest lower 'odds' value (by modulus 'step')

	public Double getNearestOdds(Double rawOdds) {

		// Double result = MIN_ODDS;
		if (rawOdds < MIN_ODDS)
			return MIN_ODDS;
		if (rawOdds > MAX_ODDS)
			return MAX_ODDS;

		Long cnt = getValidOddsRangeCount(rawOdds);
		if (cnt == 0) {
			Double _rawOdds = getMinLowMargin(rawOdds);
			log.fine("correct rawOdds from : " + rawOdds + " to:" + _rawOdds);
			rawOdds = _rawOdds;
		}

		ValidOdds validOdds = getValidOddsRange(rawOdds);

		BigDecimal _result = BigDecimal.valueOf(validOdds.getLowMargin());

		while (Math.abs((rawOdds - _result.doubleValue())) > validOdds
				.getStep()) {
			_result = _result.add(BigDecimal.valueOf(validOdds.getStep()));
			// System.out.println ("getNearestOdds: _result=" +
			// _result.doubleValue() + ", step=" + validOdds.getStep());
		}

		// LOG.info("raw odds: "+rawOdds + ", result: " + _result.doubleValue()
		// + ", validOdds: " + validOdds);
		return _result.doubleValue();
	}

	public  boolean isEqual(Double arg1, Double arg2, Double prec) {
		return (Math.abs(arg1 - arg2) < prec);	
	}

	public  boolean isEqual(Double arg1, Double arg2) {
		return isEqual(arg1, arg2, 0.001);	
	}

	public Double getCosmeticOdds(Double blueOdds1, Double blueAmount1,
			Double blueOdds2, Double pinkOdds, Double precosmOdds,
			Double _myOdds, Double _myAmount) {

		Double myOdds = (_myOdds == null? 0.0: _myOdds); 
		Double myAmount = (_myAmount==null? 0.0: _myAmount);
		
	    log.info("getCosmeticOdds( blueOdds1=" + blueOdds1 + ", blueAmount1=" + blueAmount1 + ", blueOdds2=" + blueOdds2 + ", pinkOdds="+pinkOdds+", precosmOdds=" + precosmOdds + ", myOdds=" + myOdds + ", myAmount=" + myAmount+')');

		Double cosmOdds1;
		Double cosmOdds2;
		Double result = precosmOdds;
		if (blueOdds1 == 0) {
			result = MIN_ODDS;
			return result;
		}

		// cosmOdds1 =  getUpOdds(blueOdds1);
		// cosmOdds2 =  getUpOdds(blueOdds2);


		cosmOdds1 = blueAmount1 > myAmount / 4.0 ? getUpOdds(blueOdds1)
				: blueOdds1;
				
//		System.out.println ("===*** calc cosmOdds1=" + cosmOdds1);

		cosmOdds2 = blueAmount1 > myAmount / 4.0 ? getUpOdds(blueOdds2) : blueOdds2;
				
//		System.out.println ("===*** calc cosmOdds2=" + cosmOdds2);

/*
 *  cosmOdds1 = getUpOdds(ThisWorkbook.Names(K_NAME_VALID_ODDS).RefersToRange, blueOdds1.Value)
    cosmOdds2 = getUpOdds(ThisWorkbook.Names(K_NAME_VALID_ODDS).RefersToRange, blueOdds2.Value)
 */
		
		
	//	System.out.println ("===*** calc myOdds=" + myOdds + ", blueOdds1="+blueOdds1);		
		
		if ( !isEqual(myOdds, blueOdds1)) { // !=
		//	System.out.println ("1.\t (myOdds != blueOdds1)");
			// If cosmOdds1 <= precosmOdds.Value Then
			if (cosmOdds1 <= precosmOdds) {
			//	 System.out.println ("2.\t (cosmOdds1 <= precosmOdds)");
				// If (IsEmpty(pinkOdds) Or (cosmOdds1 < pinkOdds.Value)) Then
				if (pinkOdds == null || pinkOdds == 0 || cosmOdds1 < pinkOdds) {
				//	 System.out.println ("3.\t (pinkOdds == null || cosmOdds1 < pinkOdds)");
					result = cosmOdds1;
				} else {
					// If cosmOdds2 <= precosmOdds.Value Then
					if (cosmOdds2 <= precosmOdds) {
					//	 System.out.println ("4.\t (cosmOdds2 <= precosmOdds)");
						if (cosmOdds2 < pinkOdds) {
						//	 System.out.println ("5.\t (pinkOdds == null || cosmOdds2 < pinkOdds)");							
							result = cosmOdds2;
						}
					}
				}
			} else {
//				 System.out.println ("6.\t !(cosmOdds1 <= precosmOdds)");
				if (cosmOdds2 <= precosmOdds) {
	//				 System.out.println ("7.\t (cosmOdds2 <= precosmOdds)");					
					if (pinkOdds == null || pinkOdds == 0.0 || cosmOdds2 < pinkOdds) {
		//				 System.out.println ("8.\t (pinkOdds == null || cosmOdds2 < pinkOdds)");						
						result = cosmOdds2;
					}
				}
			}
		} else { // ' myOdds.Value = blueOdds1.Value
			// System.out.println ("9.\t (myOdds == blueOdds1)");
			if (cosmOdds2 <= precosmOdds) {
				// System.out.println ("10.\t (cosmOdds2 <= precosmOdds)");
				if (pinkOdds == null || pinkOdds == 0.0 || cosmOdds2 < pinkOdds) {
					// System.out.println ("11.\t (pinkOdds == null || cosmOdds2 < pinkOdds)");
					result = cosmOdds2;
				}
			}
		}

		log.info("precosmOdds=" + precosmOdds + ", cosmOdds=" + result);
		
		if (result > precosmOdds ) {
		 log.severe ("!!!===!!! cosmOdds ("+result+") > precosmOdds("+precosmOdds+") !!!");
		} 
		return result;
	}
	
	public Double getSelectionPrice ( Double finalOdds, Double sourceOdds, Double volumeStake, Double maxLoss, Double _profitLoss, Integer inplayDelay, String marketStatus, Boolean isNonRunner) {

		log.info("getSelectionPrice(): finalOdds: " + finalOdds+", sourceOdds: "+ sourceOdds+", volumeStake: "+volumeStake+", maxLoss: " + maxLoss+", profitLoss: "+ _profitLoss+", inplayDelay: " + inplayDelay+ ", marketStatus: "+marketStatus+ ", isNonRunner: " + isNonRunner);
		Double profitLoss = (_profitLoss==null? 0.0: _profitLoss);

		Double result = FAKE_ODDS;
		if ("OPEN".equals(marketStatus) && finalOdds > 0 && finalOdds >= MIN_ODDS && volumeStake > 0 && sourceOdds != null && sourceOdds >= MIN_ODDS ) {
		  if (!isNonRunner && (volumeStake / finalOdds > MIN_STAKE_AMOUNT) && (!(maxLoss + profitLoss < 0)) && inplayDelay == 0 ) {
			  result = finalOdds;
			  log.info("$$ getSelectionPrice(): finalOdds=" + finalOdds + ", result="+result);
		  }
		} 
	   return result;	
	}
	
	
	
/*
Public Function GetSelectionPrice(ByVal finalOdds As Variant, ByVal sourceOdds As Variant, ByVal Volume_Stake As Variant, ByVal maxLoss As Variant, ByVal profitLoss As Variant, ByVal InPlay_Delay As Variant, ByVal B_Status As Variant, ByVal IsNR) As Double
On Error GoTo Err
If Not IsEmpty(B_Status) And B_Status.Value = "ACTIVE" And Not IsEmpty(finalOdds.Value) And Not finalOdds.Value < MIN_ODDS And Not IsEmpty(Volume_Stake.Value) And Application.WorksheetFunction.IsNumber(Volume_Stake.Value) And Volume_Stake.Value > 0 And Not IsEmpty(sourceOdds.Value) And Application.WorksheetFunction.IsNumber(sourceOdds.Value) And Not sourceOdds.Value < MIN_ODDS Then
 If Not IsNR And Volume_Stake.Value / finalOdds.Value > MIN_STAKE_AMOUNT And Not ((maxLoss + profitLoss) < 0) And Not IsEmpty(InPlay_Delay) And InPlay_Delay.Value = NO_INPLAY Then
    GetSelectionPrice = finalOdds
 Else
    GetSelectionPrice = FAKE_ODDS
 End If
Else
    GetSelectionPrice = FAKE_ODDS
End If
Exit Function
Err:
 MsgBox "Invalid price," & Err.Description
 GetSelectionPrice = FAKE_ODDS
End Function
*/

	public Double getSelectionAmount (Double finalOdds, Double sourceOdds, Double volumeStake, String marketStatus) {
		
		log.fine("** getSelectionAmount(): finalOdds: "+ finalOdds + ", sourceOdds: "+ sourceOdds + ", volumeStake: " + volumeStake + ", marketStatus: " + marketStatus);

		Double result = FAKE_STAKE;
		if ( marketStatus != null && "OPEN".equals(marketStatus) && finalOdds != null && finalOdds >= MIN_ODDS && volumeStake != null && volumeStake > 0 && sourceOdds != null && sourceOdds >= MIN_ODDS ) {
			if (volumeStake / finalOdds > MIN_STAKE_AMOUNT) {
			  result = Math.floor(volumeStake/finalOdds); 
			  log.fine("getSelectionAmount(): finalOdds=" + finalOdds + ", result="+result);		  	
			}
		}
		return result;
	}
/*	
Public Function GetSelectionAmount(ByVal finalOdds As Variant, ByVal sourceOdds As Variant, ByVal Volume_Stake As Variant, ByVal B_Status As Variant) As Double
On Error GoTo Err
If Not IsEmpty(B_Status) And B_Status.Value = "ACTIVE" And Not IsEmpty(finalOdds.Value) And Application.WorksheetFunction.IsNumber(finalOdds.Value) And Not finalOdds.Value < MIN_ODDS And Not IsEmpty(Volume_Stake.Value) And Application.WorksheetFunction.IsNumber(Volume_Stake.Value) And Volume_Stake.Value > 0 And Not IsEmpty(sourceOdds.Value) And Application.WorksheetFunction.IsNumber(sourceOdds.Value) And Not sourceOdds.Value < MIN_ODDS Then
 If Volume_Stake.Value / finalOdds.Value > MIN_STAKE_AMOUNT Then
    GetSelectionAmount = Application.WorksheetFunction.Floor(Volume_Stake.Value / finalOdds.Value, 1#)
 Else
    GetSelectionAmount = FAKE_STAKE
 End If
Else
    GetSelectionAmount = FAKE_STAKE
End If
Exit Function
Err:
 MsgBox "Invalid amount, " & Err.Description
 GetSelectionAmount = 1#
End Function
 
 
 
 */
	
	
	
	public void createTimer(EJBContext context, String eventId, long duration) {
		TimerService timerService;
		try {
			TimerConfig tconfig = new TimerConfig();
			tconfig.setPersistent(false);
			tconfig.setInfo(eventId);
			timerService = context.getTimerService();

			timerService.createSingleActionTimer(duration, tconfig);

			log.info(this.getClass().getName() + ": createTimer (" + context
					+ ") timer initialized for event  [eventId=" + eventId
					+ "] - hashcode: " + this.hashCode());
		} catch (Exception e) {
			String msg = e.getMessage();
			log.severe("error creating timer: " + ((msg != null) ? msg : ""));
		}
	}

	public void sendDelayedRequest(Action action, String userLogin,
			String sMarketId, int index) {
		String eventId = new StringBuffer(25).append(action.toString()).append(
				'=').append(userLogin).append(ITEM_SEPARATOR).append(sMarketId)
				.toString();

		long delayByIndex = index * 100L;

		// LOG.info("delayByIndex: " + delayByIndex/1000.0);
		createTimer(context, eventId, delayByIndex);

	}

	public static final String ITEM_SEPARATOR = "~";


	public void ejbTimeout(Timer timer) {
		// TODO Auto-generated method stub
		String timerInfo = (String) timer.getInfo();
		try {
			timer.cancel();
		} catch (Exception e) {
			log.severe("error while timer \"" + timerInfo + "\" cancellation, "
					+ e.getMessage());
		}

		log.info("ejbTimeout() - timerInfo: " + timerInfo);
	//	ShedulerActivity serviceBean = ShedulerActivityBean.getInstance();
		if (timerInfo.startsWith(Action.UPDATE_MARKET.toString())) {
			String[] procInfo = timerInfo.split("=");
			String[] procArgs = procInfo[1].split(ITEM_SEPARATOR);
			serviceBean.sendRequest(Action.UPDATE_MARKET, procArgs[0],
					procArgs[1]);

		} else if (timerInfo.startsWith(Action.UPDATE_MARKET_PRICES.toString())) {
			String[] procInfo = timerInfo.split("=");
			String[] procArgs = procInfo[1].split(ITEM_SEPARATOR);
			serviceBean.sendRequest(Action.UPDATE_MARKET_PRICES, procArgs[0],
					procArgs[1]);
		}
	}

	public String getCurrencySymbol(String currencyAlias) {
	  String result = "$";
	  if ("USD".equals(currencyAlias)) result = "$";
	  if ("EUR".equals(currencyAlias)) result = "\u20AC";
	  if ("GBP".equals(currencyAlias)) result = "\u00A3";
	  return result;
	}
	
}
