package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.logic.util.GameUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeployPhase extends BasePhase {

	private Player activePlayer;

	private Unit selectedUnit;

	private Map<Player, Set<Field>> validTargetFields = new HashMap<>();

	public DeployPhase(Player firstActivePlayer) {
		this.activePlayer = firstActivePlayer;
		firstActivePlayer.getGame().getPlayers().forEach(p -> validTargetFields.put(p, p.getValidTargetFields()));
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "selectHandCard":
			execSelectHandCard(player, param);
			break;
		case "selectTargetField":
			execSelectTargetField(player, param);
			break;
		}
	}

	private void execSelectHandCard(Player player, String param) {
		if (player != activePlayer) {
			log.error("got cmd selectHandCard from not active player");
			return;
		}
		if (selectedUnit != null) {
			log.error("execSelectHandCard but selectedUnit was " + selectedUnit.getType());
			return;
		}
		selectedUnit = player.getUnitInHand().stream().filter(u -> u.getId().equals(param)).findFirst().get();
	}

	private void execSelectTargetField(Player player, String param) {
		if (player != activePlayer) {
			log.error("got cmd selectHandCard from not active player");
			return;
		}
		if (selectedUnit == null) {
			log.error("execSelectTargetField but selectedUnit was null");
			return;
		}
		Field target = player.getGame().getBoard().getField(param);
		if (target.getUnit() != null) {
			getMessages().addMessage(player, Constants.RESP_ADD_MESSAGE,
					instance.textNode("You cannot put a unit where another unit is already there."));
		} else {
			target.setUnit(selectedUnit);
			selectedUnit.setDeployedOn(target);
			player.getUnitInHand().remove(selectedUnit);
			getMessages().addMessage(player, Constants.RESP_REMOVE_HANDITEM, instance.textNode(selectedUnit.getId()));
			getMessages().addMessage(player.getGame(), Constants.RESP_ADD_UNIT, selectedUnit.getJson());
			selectedUnit = null;
			switchPlayer(player);
		}
	}

	private void switchPlayer(Player player) {
		activePlayer = GameUtil.getOtherPlayer(player.getGame(), activePlayer);
		updateUI(activePlayer);
	}

	@Override
	public void updateUI(Player player) {
		if (player == activePlayer) {
			if (selectedUnit != null) {
				ObjectNode messageObj = instance.objectNode();
				messageObj.set("message",
						instance.textNode("Select an empty field in your controlled area to deploy unit"));
				messageObj.set("selectedHandItemId", instance.textNode(selectedUnit.getId()));
				ArrayNode validTargetFieldIds = instance.arrayNode();
				validTargetFields.get(player).forEach(f -> {
					ObjectNode jsonPosObject = instance.objectNode();
					jsonPosObject.set("x", instance.numberNode((int) f.getPos().getX()));
					jsonPosObject.set("y", instance.numberNode((int) f.getPos().getY()));
					validTargetFieldIds.add(jsonPosObject);
				});
				messageObj.set("validTargetFieldIds", validTargetFieldIds);
				getMessages().addMessage(player, Constants.RESP_SELECT_DEPLOY_TARGET, messageObj);
			} else {
				ObjectNode messageObj = instance.objectNode();
				messageObj.set("message", instance.textNode("Select a unit from your hand to deploy it"));
				getMessages().addMessage(player, Constants.RESP_DO_DEPLOY, messageObj);
			}
		} else {
			ObjectNode messageObj = instance.objectNode();
			messageObj.set("message", instance.textNode("waiting for other player's action"));
			getMessages().addMessage(player, Constants.RESP_WAIT, messageObj);
		}
	}

}
