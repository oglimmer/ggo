package de.oglimmer.ggo.atmospheremvc.com;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import de.oglimmer.ggo.atmospheremvc.game.Game;
import de.oglimmer.ggo.atmospheremvc.game.Player;
import de.oglimmer.ggo.websocket.WebSocketSessionCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MessageQueue {

	@NonNull
	private Game game;

	private Map<Player, JsonNode> messages = new HashMap<>();

	public void process() {
		prepareMessages();
		sendMessages();
	}

	private void prepareMessages() {
		game.getPlayers().forEach(this::addState);
	}

	private void addState(Player player) {
		JsonNode uiUpdate = player.getUiStates().getJSON();
		if (uiUpdate != null) {
			messages.put(player, uiUpdate);
		}
	}

	private void sendMessages() {
		messages.entrySet().forEach(this::sendMessage);
	}

	private void sendMessage(Entry<Player, JsonNode> en) {
		WebSocketSessionCache.INSTANCE.sendMessage(en.getKey(), en.getValue());
	}

}