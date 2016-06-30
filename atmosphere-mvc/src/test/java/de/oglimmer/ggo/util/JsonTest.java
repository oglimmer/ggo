package de.oglimmer.ggo.util;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import de.oglimmer.atmospheremvc.util.Json;

public class JsonTest {

	@Test
	public void tests() throws Exception {
		processFile("/jsonTests/simple1.json");
		processFile("/jsonTests/simple2.json");
		processFile("/jsonTests/simple3.json");
	}

	private void processFile(String filename) throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(this.getClass().getResourceAsStream(filename));
		if (root.isObject()) {
			process(root);
		} else {
			root.forEach(this::process);
		}
	}

	private void process(JsonNode ele) {
		JsonNode actual = Json.INSTANCE.diff(ele.get("NEW"), ele.get("OLD"));
		JsonNode expected = ele.get("DIFF");
		Assert.assertEquals(expected, actual == null ? NullNode.getInstance() : actual);
	}

}
