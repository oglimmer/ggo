package de.oglimmer.ggo.atmospheremvc.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.oglimmer.ggo.websocket.WebSocketSessionCache;
import de.oglimmer.ggo.websocket.WebSocketSessionCacheItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Games<T extends Game> {

	private static Games<? extends Game> INSTANCE;

	public static <T extends Game> void setGames(Games<T> INSTANCE) {
		assert Games.INSTANCE == null;
		Games.INSTANCE = INSTANCE;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Game> Games<T> getGames() {
		return (Games<T>) INSTANCE;
	}

	@NonNull
	private Class<T> gameClass;

	private Map<String, T> games = new HashMap<>();

	public T getGameById(String gameId) {
		return games.get(gameId);
	}

	public T getGameByPlayerId(String playerId) {
		Optional<T> findFirst = games.values().stream()
				.filter(g -> g.getPlayers().stream().anyMatch(p -> p.getId().equals(playerId))).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

	public void reset() {
		games = new HashMap<>();
	}

	public T createGame() {
		try {
			T newGame = gameClass.newInstance();
			games.put(newGame.getId(), newGame);
			return newGame;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Collection<T> getOpenGames() {
		return games.values().stream().filter(g -> g.getPlayers().size() != 2).collect(Collectors.toSet());
	}

	public Collection<T> getAllGames() {
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
				games = (Map<String, T>) ois.readObject();
				ois.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			log.error("Failed to load all games", e);
		}
	}

	public void removeGame(String gameId) {
		T removedGame = games.remove(gameId);
		if (removedGame != null) {
			Collection<? extends Player> players = removedGame.getPlayers();
			WebSocketSessionCache.INSTANCE.getItems().stream().filter(i -> players.contains(i.getPlayer()))
					.map(WebSocketSessionCacheItem::getSessionId).forEach(WebSocketSessionCache.INSTANCE::remove);
		} else {
			log.error("removed Game but not found");
		}
	}
}
