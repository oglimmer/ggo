package de.oglimmer.ggo.logic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Games {
	INSTANCE;

	private Game FAKE = new Game();

	public Game getGameById(String gameId) {
		return FAKE;
	}

	public Game getGameByPlayerId(String playerId) {
		return FAKE;
	}

}
