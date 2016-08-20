package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.logic.battle.CommandCenter;
import de.oglimmer.ggo.logic.battle.CommandType;
import de.oglimmer.ggo.logic.util.FieldUtil;
import de.oglimmer.utils.random.RandomString;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Unit implements Serializable {

	private static final long serialVersionUID = 1L;

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

	public Set<CommandType> getPossibleCommandTypes(CommandCenter cc, Field targetField) {
		Set<CommandType> possibleCommands = new HashSet<>();
		if (getMovableFields(cc).contains(targetField)) {
			possibleCommands.add(CommandType.MOVE);
		}
		switch (unitType) {
		case TANK:
		case INFANTERY:
		case AIRBORNE:
			if (getSupportableFields(cc).contains(targetField)) {
				possibleCommands.add(CommandType.SUPPORT);
			}
			break;
		case HELICOPTER:
			if (getSupportableFields(cc).contains(targetField)) {
				possibleCommands.add(CommandType.SUPPORT);
			}
			if (getTargetableFields().contains(targetField)) {
				possibleCommands.add(CommandType.BOMBARD);
			}
			break;
		case ARTILLERY:
			if (getTargetableFields().contains(targetField)) {
				possibleCommands.add(CommandType.BOMBARD);
			}
			break;
		}
		return possibleCommands;
	}

	public Set<Field> getSupportableFields(CommandCenter cc) {
		assert this.unitType != UnitType.ARTILLERY;
		/*
		 * A unit can support a neighbor if an own unit is currently there and
		 * not moving away.
		 */
		Set<Field> mf = new HashSet<>();
		for (Field f : deployedOn.getNeighbors()) {

			if (f.getUnit() != null && f.getUnit().getPlayer() == player) {
				Command cmdForTargetUnit = cc.getByUnit(f.getUnit());
				if (cmdForTargetUnit.getCommandType().isFortify() || cmdForTargetUnit.getCommandType().isBombard()
						|| cmdForTargetUnit.getCommandType().isSupport()) {
					mf.add(f);
				} else if (cmdForTargetUnit.getCommandType().isMove()) {
					Field targetFieldForMovingTargetUnit = cmdForTargetUnit.getTargetField();
					if (FieldUtil.adjacent(targetFieldForMovingTargetUnit, deployedOn)) {
						mf.add(f);
					}
				}
			}
		}
		return mf;
	}

	private Set<Field> getMovableFields(CommandCenter cc) {
		/*
		 * A unit can move to all neighbors if no own unit has a FORTIFY or MOVE
		 * on/to this field
		 */
		Set<Field> mf = new HashSet<>();
		for (Field f : deployedOn.getNeighbors()) {
			Set<Command> myCommandsForThisField = cc.getByTargetField(player, f);
			boolean occupiedByOwnUnit = myCommandsForThisField.stream().filter(c -> c.getUnit() != this)
					.anyMatch(c -> c.getCommandType().isMove() || c.getCommandType().isFortify());
			if (!occupiedByOwnUnit) {

				Set<Command> supportCommands = getSupportingUnits(cc);
				if (supportCommands != null) {
					if (allSupportCommandsAdjacent(supportCommands, f)) {
						mf.add(f);
					}
				} else {
					mf.add(f);
				}

			}
		}
		return mf;
	}

	private boolean allSupportCommandsAdjacent(Set<Command> supportUnits, Field f) {
		return supportUnits.stream().allMatch(c -> FieldUtil.adjacent(f, c.getUnit().getDeployedOn()));
	}

	private Set<Command> getSupportingUnits(CommandCenter cc) {
		return cc.stream().filter(c -> c.getUnit().getPlayer() == player).filter(c -> c.getCommandType().isSupport())
				.filter(c -> c.getTargetField() == deployedOn).collect(Collectors.toSet());
	}

	public Set<Field> getTargetableFields() {
		assert this.unitType == UnitType.ARTILLERY || this.unitType == UnitType.HELICOPTER;
		/*
		 * A unit can target a neighbor if an enemy unit is on that field (don't
		 * care about enemy commands)
		 */
		Set<Field> possibleTargetableFields = new HashSet<>(deployedOn.getNeighbors());
		if (unitType == UnitType.ARTILLERY) {
			deployedOn.getNeighbors().forEach(f -> possibleTargetableFields.addAll(f.getNeighbors()));
		}
		Set<Field> targetableFields = new HashSet<>();
		for (Field f : possibleTargetableFields) {
			if (f.getUnit() != null && f.getUnit().getPlayer() != player) {
				targetableFields.add(f);
			}
		}
		return targetableFields;
	}

	public boolean isOnBoard() {
		return player.getGame().getBoard().getFields().stream().anyMatch(f -> f.getUnit() == this);
	}

	public boolean hasCommandOnField(CommandCenter cc, Field field) {
		return !getPossibleCommandTypes(cc, field).isEmpty();
	}

	@Override
	public String toString() {
		return "Unit [player=" + player.getSide() + ", type=" + unitType + ", on="
				+ (deployedOn != null ? deployedOn.getId() : null) + "]";
	}
}
