package de.oglimmer.ggo.logic;

import org.atmosphere.cpr.AtmosphereResource;

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

	public void removeAtmosphereResource(AtmosphereResource r) {
		FAKE.getChannelRegistry().remove(r);
	}

}
