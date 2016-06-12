package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.HashMap;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageQueue {

	private ThreadLocal<Map<Player, ObjectNode>> messages = new ThreadLocal<>();

	protected void addMessage(Player target, String name, JsonNode msg) {
		ObjectNode root = getMap().getOrDefault(target, instance.objectNode());
		assert !root.has(name);
		root.set(name, msg);
		getMap().put(target, root);
	}

	protected void addMessage(Game game, String name, JsonNode msg) {
		game.getPlayers().forEach(p -> {
			ObjectNode root = getMap().getOrDefault(p, instance.objectNode());
			root.set(name, msg);
			getMap().put(p, root);
		});
	}

	public void clearMessages() {
		getMap().clear();
	}

	public void sendMessages() {
		getMap().entrySet().forEach(en -> {
			AtmosphereResource r = en.getKey().getGame().getChannelRegistry().get(en.getKey().getId());
			if (r != null) {
				r.getBroadcaster().broadcast(en.getValue(), r);
			} else {
				log.warn("Discard messages as player " + en.getKey() + " not connected");
			}
		});
	}

	private Map<Player, ObjectNode> getMap() {
		Map<Player, ObjectNode> map = messages.get();
		if (map == null) {
			map = new HashMap<>();
			messages.set(map);
		}
		return map;
	}

}