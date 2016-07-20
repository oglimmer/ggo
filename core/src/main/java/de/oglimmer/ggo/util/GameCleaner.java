package de.oglimmer.ggo.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum GameCleaner {
	INSTANCE;

	private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	public void start() {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, -1);
				log.debug("Compare games against {}", cal.getTime());

				Collection<Game> allGames = new ArrayList<>(Games.<Game> getGames().getAllGames());
				allGames.forEach(game -> {
					if (game.getCreatedOn().before(cal.getTime())) {
						Games.<Game> getGames().removeGame(game.getId());
					}
				});
			}

		}, 1, 1, TimeUnit.MINUTES);
	}

	public void stop() {
		service.shutdown();
	}

}
