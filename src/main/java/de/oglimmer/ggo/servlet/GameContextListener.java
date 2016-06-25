package de.oglimmer.ggo.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.util.GridGameOneProperties;

// import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class GameContextListener
 * 
 */
// @WebListener
public class GameContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		GridGameOneProperties.INSTANCE.shutdown();
		EmailService.INSTANCE.shutdown();
	}

}
