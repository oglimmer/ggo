package de.oglimmer.atmospheremvc.com;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.atmospheremvc.game.Game;
import de.oglimmer.atmospheremvc.game.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageQueue {

	private Map<Player, ObjectNode> messages = new HashMap<>();

	public void addMessage(Player target, String name, JsonNode msg) {
		ObjectNode root = messages.getOrDefault(target, instance.objectNode());
		assert !root.has(name);
		root.set(name, msg);
		messages.put(target, root);
	}

	public void sendMessages() {
		messages.entrySet().forEach(en -> {
			AtmosphereResource r = AtmosphereResourceCache.INSTANCE.get(en.getKey());
			if (r != null) {
				r.getBroadcaster().broadcast(en.getValue(), r);
			} else {
				log.warn("Discard messages as player " + en.getKey().getSide() + " not connected");
			}
		});
	}

	public void addUpdateUIMessages(Game game) {
		game.getPlayers().forEach(this::addMessageUIState);
	}

	private void addMessageUIState(Player player) {
		JsonNode uiUpdate = player.getUiStates().getJSON();
		if (uiUpdate != null) {
			Iterator<String> keysIt = uiUpdate.fieldNames();
			while (keysIt.hasNext()) {
				String key = keysIt.next();
				addMessage(player, key, uiUpdate.get(key));
			}
		}
	}

}