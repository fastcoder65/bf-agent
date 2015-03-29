package net.bir.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.util.logging.*;

public class BIRApplicationContextListener implements ServletContextListener {
	
	static private final Logger log = Logger
			.getLogger(BIRApplicationContextListener.class.getName());

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		String servletContextName = event.getServletContext()
				.getServletContextName();

		log.info(servletContextName + " context has been destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String servletContextName = event.getServletContext()
				.getServletContextName();

		log.info(servletContextName + " context has been initialized");
	}

}
