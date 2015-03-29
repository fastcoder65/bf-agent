package net.bir.web.beans;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

import javax.naming.Context;
import javax.naming.InitialContext;

import net.bir2.multitrade.ejb.entity.Market;

import net.bir.util.WebUtils;

public class ServiceBean extends BaseBean {

	//static private ServiceBean __instance = new ServiceBean();
	private static final String SERVICE_BEAN = "serviceBean";
	public static ServiceBean getInstance() {
		return (ServiceBean) WebUtils.getManagedBean(SERVICE_BEAN);
	//	return __instance;	
	}
	
	
	public ServiceBean(){
		getLog().info("ServiceBean is created.");
	}
	
	public List<Market> getActiveMarkets() {
		return activeMarkets;
	}

	public void setActiveMarkets(List<Market> activeMarkets) {
		this.activeMarkets = activeMarkets;
	}

	private List<Market> activeMarkets = new ArrayList<Market>();


/*	
	public void checkScheduler() throws ParseException, SchedulerException  {
		FacesContext ctx = FacesContext.getCurrentInstance();
		StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getExternalContext().getApplicationMap().get("org.quartz.impl.StdSchedulerFactory.KEY");
		Scheduler scheduler = null;
		try {
			scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		JobDetail jDetail = new JobDetail("Newsletter", "NJob", MyJob.class);

        //"0 0 12 * * ?" Fire at 12pm (noon) every day
        //"0/2 * * * * ?" Fire at every 2 seconds every day

		CronTrigger crTrigger = new CronTrigger("cronTrigger", "NJob", "0/2 * * * * ?");

 		scheduler.scheduleJob(jDetail, crTrigger);
	}
*/
	
	public InitialContext getInitialContext() throws Exception {
		Hashtable<String, String> props = getInitialContextProperties();
		return new InitialContext(props);
	}

	private Hashtable<String, String> getInitialContextProperties() {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");
		props.put(Context.URL_PKG_PREFIXES,
				"org.jboss.naming:org.jnp.interfaces");
		return props;
	}

	public void loadAllActiveMarkets() {
		log.info("Loading active markets..");
		if (marketService != null) {
		  if (activeMarkets.size() == 0)	
			activeMarkets.addAll(marketService.listMarkets());
			
		} else {
		 getLog().log(Level.WARNING, "marketService is null!");	
		}
	}

	public void dispose() {
		clearMarkets();
	}

	public void clearMarkets() {
		activeMarkets.clear();
	}


}
