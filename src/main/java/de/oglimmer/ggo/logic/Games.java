package de.oglimmer.ggo.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum Games {
	INSTANCE;

	Games() {
		// games.put("0", new Game());
	}

	private Map<String, Game> games = new HashMap<>();

	public Game getGameById(String gameId) {
		return games.get(gameId);
	}

	public Game getGameByPlayerId(String playerId) {
		return games.values().stream().filter(g -> g.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId)))
				.findFirst().get();
	}

	public void reset() {
		// games.put("0", new Game());
		games = new HashMap<>();
	}

	public Game createGame() {
		Game newGame = new Game();
		games.put(newGame.getId(), newGame);
		return newGame;
	}

	public Collection<Game> getOpenGames() {
		return games.values();
	}

	public Collection<Game> getAllGames() {
		return games.values();
	}

}
