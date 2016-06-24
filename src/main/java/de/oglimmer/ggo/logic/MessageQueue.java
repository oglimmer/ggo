package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.HashMap;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.com.AtmosphereResourceCache;
import de.oglimmer.ggo.com.Constants;
import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIConnectedState;
import de.oglimmer.ggo.ui.UIMessages;
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
		game.getPlayers().forEach(this::addMessage);
	}

	private void addMessage(Player player) {
		ObjectMapper mapper = new ObjectMapper();
		addMessageUIBoard(player, mapper);
		addMessageUIMessages(player, mapper);
		addMessageUIConnectState(player, mapper);
	}

	private void addMessageUIConnectState(Player player, ObjectMapper mapper) {
		UIConnectedState uiConnected = player.getUiStates().getConnected().calcStateAndDiff(player);
		if (uiConnected != null) {
			JsonNode messageJsonObject = mapper.valueToTree(uiConnected);
			addMessage(player, Constants.RESP_PLAYER_CONNECTION_STATUS, messageJsonObject);
		}
	}

	private void addMessageUIMessages(Player player, ObjectMapper mapper) {
		UIMessages uiMessages = player.getUiStates().getClientMessages().calcDiffMessages();
		if (uiMessages.hasChange()) {
			JsonNode messageJsonObject = mapper.valueToTree(uiMessages);
			addMessage(player, Constants.RESP_MESSAGE, messageJsonObject);
		}
	}

	private void addMessageUIBoard(Player player, ObjectMapper mapper) {
		UIBoard uiUpdate = player.getUiStates().getClientUIState().calcStateAndDiff(player);
		if (uiUpdate.hasChange()) {
			JsonNode boardJsonObject = mapper.valueToTree(uiUpdate);
			addMessage(player, Constants.RESP_BOARD, boardJsonObject);
		}
	}

}