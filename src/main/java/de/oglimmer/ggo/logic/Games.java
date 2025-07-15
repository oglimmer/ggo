package de.oglimmer.ggo.logic;

import de.oglimmer.ggo.websocket.WebSocketSessionCache;
import de.oglimmer.ggo.websocket.WebSocketSessionCacheItem;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Games {

	private static Games INSTANCE = new Games();

	public static Games getGames() {
		return INSTANCE;
	}

	private Class<Game> gameClass = Game.class;

	private Map<String, Game> games = new HashMap<>();

	public Game getGameById(String gameId) {
		return games.get(gameId);
	}

	public Game getGameByPlayerId(String playerId) {
		Optional<Game> findFirst = games.values().stream()
				.filter(g -> g.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId))).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

	public void reset() {
		games = new HashMap<>();
	}

	public Game createGame() {
		try {
			Game newGame = gameClass.newInstance();
			games.put(newGame.getId(), newGame);
			return newGame;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Collection<Game> getOpenGames() {
		return games.values().stream().filter(g -> g.getPlayers().size() != 2).collect(Collectors.toSet());
	}

	public Collection<Game> getAllGames() {
		return new ArrayList<>(games.values());
	}

	public void removeAbandonedGame(String gameId) {
		Game game = games.get(gameId);
		if (game != null && game.getPlayers().size() != 2) {
			games.remove(gameId);
		}
	}

	public void saveAll() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/tmp/all-games.ser"));
			oos.writeObject(games);
			oos.close();
		} catch (IOException e) {
			log.error("Failed to save all games", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadAll() {
		try {
			if (new File("/tmp/all-games.ser").exists()) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/tmp/all-games.ser"));
				games = (Map<String, Game>) ois.readObject();
				ois.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			log.error("Failed to load all games", e);
		}
	}

	public void removeGame(String gameId) {
		Game removedGame = games.remove(gameId);
		if (removedGame != null) {
			Collection<? extends Player> players = removedGame.getPlayers();
			WebSocketSessionCache.INSTANCE.getItems().stream().filter(i -> players.contains(i.getPlayer()))
					.map(WebSocketSessionCacheItem::getSessionId).forEach(WebSocketSessionCache.INSTANCE::remove);
		} else {
			log.error("removed Game but not found");
		}
	}
}
