package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.HashMap;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIMessages;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageQueue {

	private ThreadLocal<Map<Player, ObjectNode>> messages = new ThreadLocal<>();

	public void addMessage(Player target, String name, JsonNode msg) {
		ObjectNode root = getMap().getOrDefault(target, instance.objectNode());
		assert !root.has(name);
		root.set(name, msg);
		getMap().put(target, root);
	}

//	public void addMessage(Game game, String name, JsonNode msg) {
//		game.getPlayers().forEach(p -> {
//			ObjectNode root = getMap().getOrDefault(p, instance.objectNode());
//			root.set(name, msg);
//			getMap().put(p, root);
//		});
//	}

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

	public void addMessage(Player player, UIBoard uiUpdate, UIMessages uiMessages) {
		ObjectMapper mapper = new ObjectMapper();
		if (uiUpdate.hasChange()) {
			JsonNode boardJsonObject = mapper.valueToTree(uiUpdate);
			addMessage(player, Constants.RESP_BOARD, boardJsonObject);
		}
		if(uiMessages.hasChange()) {
			JsonNode messageJsonObject = mapper.valueToTree(uiMessages);
			addMessage(player, Constants.RESP_MESSAGE, messageJsonObject);
		}
	}

}