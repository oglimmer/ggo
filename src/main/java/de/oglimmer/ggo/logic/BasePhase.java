package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;

abstract public class BasePhase {

	@Getter
	private MessageQueue messages = new MessageQueue();

	public void execCmd(Player player, String cmd, String param) {
		switch (cmd) {
		case "join":
			createAllStaticFields(player);
			createAllHand(player);
			createAllBoardUnit(player, player.getGame());
			messages.addMessage(player, Constants.RESP_MYCOLOR, instance.textNode(player.getSide().toString()));
			break;
		}
	}

	private void createAllBoardUnit(Player player, Game game) {
		ArrayNode unitsArray = instance.arrayNode();
		game.getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.forEach(f -> unitsArray.add(f.getUnit().getJson()));
		messages.addMessage(player, Constants.RESP_ALL_BOARD_UNITS, unitsArray);
	}

	private void createAllStaticFields(Player player) {
		ArrayNode fieldsArray = instance.arrayNode();
		player.getGame().getBoard().getFields().forEach(f -> {
			ObjectNode jsonFieldObject = instance.objectNode();
			ObjectNode jsonPosObject = instance.objectNode();
			jsonPosObject.set("x", instance.numberNode((int) f.getPos().getX()));
			jsonPosObject.set("y", instance.numberNode((int) f.getPos().getY()));
			jsonFieldObject.set("pos", jsonPosObject);
			if (f.getStructure() != null) {
				ObjectNode jsonStructureObject = instance.objectNode();
				jsonStructureObject.set("type", instance.textNode(f.getStructure().getType()));
				jsonStructureObject.set("side", instance.textNode(f.getStructure().getPlayer().getSide().toString()));
				jsonFieldObject.set("structure", jsonStructureObject);
			}
			fieldsArray.add(jsonFieldObject);
		});
		messages.addMessage(player, Constants.RESP_ALL_STATIC_FIELDS, fieldsArray);
	}

	private void createAllHand(Player player) {
		ArrayNode unitsArray = instance.arrayNode();
		player.getUnitInHand().forEach(u -> {
			ObjectNode jsonUnitObject = instance.objectNode();
			jsonUnitObject.set("unitId", instance.textNode(u.getId()));
			jsonUnitObject.set("unitType", instance.textNode(u.getType().toString()));
			unitsArray.add(jsonUnitObject);
		});
		messages.addMessage(player, Constants.RESP_ALL_HAND, unitsArray);
	}

	/**
	 * Must be idempotent
	 * 
	 * @param player
	 *            for whom the UI should be updated
	 */
	public void updateUI(Player player) {
	}
}
