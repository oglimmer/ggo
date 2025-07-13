package de.oglimmer.ggo.util;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.oglimmer.ggo.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum GameCleaner {
	INSTANCE;

	private static final int SECONDS_UNTIL_GAME_EXPIRES = 60 * 60 * 24;

	private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	public void start() {
		service.scheduleAtFixedRate(new Runnable() {

			private Calendar cal;

			@Override
			public void run() {
				cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, -SECONDS_UNTIL_GAME_EXPIRES);
				log.debug("Compare games against {}", cal.getTime());
				Games.<Game> getGames().getAllGames().stream().filter(this::expiredGames).map(Game::getId)
						.forEach(Games.<Game> getGames()::removeGame);
			}

			private boolean expiredGames(Game game) {
				return game.getCreatedOn().before(cal.getTime());
			}

		}, 1, 1, TimeUnit.MINUTES);
	}

	public void stop() {
		service.shutdown();
	}

}
