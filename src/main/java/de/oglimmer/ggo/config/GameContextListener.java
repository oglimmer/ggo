package de.oglimmer.ggo.config;

import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.util.GameCleaner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Application Lifecycle Listener implementation class GameContextListener
 */
@Component
public class GameContextListener {

    @EventListener
    public void handleContextStart(ContextRefreshedEvent event) {
        Games.getGames().loadAll();
        GameCleaner.INSTANCE.start();
    }

    @EventListener
    public void handleContextStop(ContextClosedEvent event) {
        Games.getGames().saveAll();
        GameCleaner.INSTANCE.stop();
    }
}
