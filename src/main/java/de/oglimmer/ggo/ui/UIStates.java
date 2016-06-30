package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Messages;
import de.oglimmer.ggo.util.Json;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIStates {

	private Player forPlayer;

	private JsonNode lastSendData;

	private States states;

	class States {

		@Getter
		private UIBoardStateProvider boardState;

		@Getter
		private Messages messagesState;

		@Getter
		private UIConnectionStateProvider connectionState;

	}

	public UIBoardStateProvider getBoardState() {
		return states.getBoardState();
	}

	public Messages getMessagesState() {
		return states.getMessagesState();
	}

	public UIConnectionStateProvider getConnectionState() {
		return states.getConnectionState();
	}

	public UIStates(Player player) {
		this.states = new States();
		this.states.boardState = new UIBoardStateProvider(player);
		this.states.connectionState = new UIConnectionStateProvider(player);
		this.states.messagesState = player.getMessages();
	}

	public JsonNode getJSON() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode newJSON = mapper.valueToTree(states);
		JsonNode diff;
		if (lastSendData != null) {
			diff = Json.INSTANCE.diff(newJSON, lastSendData);
		} else {
			diff = newJSON;
		}
		lastSendData = newJSON;
		return diff;
	}

}
