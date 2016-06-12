package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.util.RandomString;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Unit {

	@Getter
	private String id = RandomString.getRandomStringHex(8);

	@Getter
	@NonNull
	private Player player;

	@Getter
	@NonNull
	private UnitType type;

	@Getter
	@Setter
	private Field deployedOn;

	public JsonNode getJson() {
		ObjectNode jsonUnitObject = instance.objectNode();
		ObjectNode jsonPosObject = instance.objectNode();
		jsonPosObject.set("x", instance.numberNode((int) deployedOn.getPos().getX()));
		jsonPosObject.set("y", instance.numberNode((int) deployedOn.getPos().getY()));
		jsonUnitObject.set("pos", jsonPosObject);
		jsonUnitObject.set("unitType", instance.textNode(type.toString()));
		jsonUnitObject.set("side", instance.textNode(player.getSide().toString()));
		jsonUnitObject.set("unitId", instance.textNode(getId()));
		return jsonUnitObject;
	}

}
