package net.bir.web.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;

import net.bir2.ejb.session.market.MarketService;

import java.util.logging.*;

public abstract class BaseBean {
	protected final Logger log = Logger.getLogger(this.getClass().getName());

	//public static final NumberFormat currencyFormat = new NumberFormat()
	public static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public static final DateFormat dateFormatObj = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public static final DateFormat shortDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	
	public static String dateFormat = "dd-MM-yyyy HH:mm";

	public String getDateFormat() {
		return dateFormat;
	}

	public String getDateFormatValue() {
		return  "{0, date, " + getDateFormat() + "}";		
	}

	
	public Logger getLog() {
		return log;
	}
	

	@EJB(name = "BIR/MarketServiceBean/local")
	protected MarketService marketService;

	public MarketService getMarketService() {
		return marketService;
	}

}
