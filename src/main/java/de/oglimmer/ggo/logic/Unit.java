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
	private UnitType unitType;

	@Getter
	@Setter
	private Field deployedOn;

	public JsonNode getJson() {
		ObjectNode jsonUnitObject = instance.objectNode();
		ObjectNode jsonPosObject = instance.objectNode();
		jsonPosObject.set("x", instance.numberNode((int) deployedOn.getPos().getX()));
		jsonPosObject.set("y", instance.numberNode((int) deployedOn.getPos().getY()));
		jsonUnitObject.set("pos", jsonPosObject);
		jsonUnitObject.set("unitType", instance.textNode(unitType.toString()));
		jsonUnitObject.set("side", instance.textNode(player.getSide().toString()));
		jsonUnitObject.set("unitId", instance.textNode(getId()));
		return jsonUnitObject;
	}

	public boolean isSelected(Player forPlayer) {
		return forPlayer.getGame().getCurrentPhase().isSelected(this, forPlayer);
	}

	public boolean isSelectable(Player forPlayer) {
		return forPlayer.getGame().getCurrentPhase().isSelectable(this, forPlayer);
	}

	@Override
	public String toString() {
		return "Unit [id=" + id + ", player=" + player.getSide() + ", unitType=" + unitType + ", deployedOn="
				+ (deployedOn != null ? deployedOn.getId() : null) + ", selected=" + isSelected(player)
				+ ", selectable=" + isSelectable(player) + "]";
	}

}
