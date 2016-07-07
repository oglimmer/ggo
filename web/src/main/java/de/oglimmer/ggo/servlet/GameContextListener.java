package de.oglimmer.ggo.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.util.GridGameOneProperties;

/**
 * Application Lifecycle Listener implementation class GameContextListener
 * 
 */
@WebListener
public class GameContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Games.setGames(new Games<>(Game.class));
		Games.getGames().loadAll();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Games.getGames().saveAll();
		GridGameOneProperties.PROPERTIES.shutdown();
		EmailService.EMAIL.shutdown();
	}

}
