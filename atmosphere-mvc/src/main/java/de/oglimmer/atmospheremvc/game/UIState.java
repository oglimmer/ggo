package de.oglimmer.atmospheremvc.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.oglimmer.atmospheremvc.util.Json;

abstract public class UIState {

	private JsonNode lastSendData;

	abstract protected Object getState();

	public JsonNode getJSON() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode newJSON = mapper.valueToTree(getState());
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