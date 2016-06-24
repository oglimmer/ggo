package de.oglimmer.ggo.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
		return games.values().stream().filter(g -> g.getPlayers().size() != 2).collect(Collectors.toSet());
	}

	public Collection<Game> getAllGames() {
		return games.values();
	}

	public void removeAbandonedGame(String gameId) {
		Game game = games.get(gameId);
		if (game != null && game.getPlayers().size() != 2) {
			games.remove(gameId);
		}
	}

}
