package de.oglimmer.atmospheremvc.com;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.atmosphere.cpr.AtmosphereResource;

import com.fasterxml.jackson.databind.JsonNode;

import de.oglimmer.atmospheremvc.game.Game;
import de.oglimmer.atmospheremvc.game.Player;
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
		AtmosphereResource r = AtmosphereResourceCache.INSTANCE.get(en.getKey());
		if (r != null) {
			r.getBroadcaster().broadcast(en.getValue(), r);
		} else {
			log.warn("Discard messages as player " + en.getKey().getSide() + " not connected");
		}
	}

}