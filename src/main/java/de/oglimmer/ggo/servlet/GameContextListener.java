package de.oglimmer.ggo.servlet;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.util.GameCleaner;
import de.oglimmer.ggo.websocket.game.Games;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class GameContextListener
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
        GameCleaner.INSTANCE.stop();
    }

}
