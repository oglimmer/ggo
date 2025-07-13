package de.oglimmer.ggo.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import de.oglimmer.ggo.atmospheremvc.game.Games;
import de.oglimmer.ggo.db.ConnectionPool;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.util.GameCleaner;
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
		GameCleaner.INSTANCE.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Games.getGames().saveAll();
		GridGameOneProperties.PROPERTIES.shutdown();
		EmailService.EMAIL.shutdown();
		GameCleaner.INSTANCE.stop();
		ConnectionPool.INSTANCE.shutdown();
	}

}
