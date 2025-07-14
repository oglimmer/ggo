package de.oglimmer.ggo.websocket.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.oglimmer.ggo.websocket.util.Json;

abstract public class UIState implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient JsonNode lastSendData;

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

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		ObjectMapper mapper = new ObjectMapper();
		oos.writeUTF(mapper.writeValueAsString(lastSendData));
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		ObjectMapper mapper = new ObjectMapper();
		lastSendData = mapper.readTree(ois.readUTF());
	}
}
