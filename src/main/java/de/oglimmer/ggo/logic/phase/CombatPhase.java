package de.oglimmer.ggo.logic.phase;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.logic.Constants;
import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.util.GameUtil;
import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;
import de.oglimmer.ggo.ui.UnitCommandablePhase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatPhase extends BasePhase implements UnitCommandablePhase {

	private static final int MAX_ROUNDS = 3;

	private int round = 0;

	private Map<Player, Unit> selectedUnits = new HashMap<>();
	private Map<Player, Field> selectedFields = new HashMap<>();
	@Getter
	private Map<Player, Set<CommandType>> possibleCommandTypesOptions = new HashMap<>();
	private Set<Player> inTurn = new HashSet<>();
	private CommandCenter cc = new CommandCenter();

	public CombatPhase(Game game) {
		super(game);
	}

	@Override
	public void init(Player firstActivePlayer) {
		inTurn.addAll(getGame().getPlayers());
		cc.setAllToFortify();
		getGame().getPlayers().forEach(p -> p.getClientMessages().clearErrorInfo());
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		Unit unit = selectedUnits.get(forPlayer);
		// FIELD SELECTEDABLE: unit is selected AND unit can go to or target the field
		return unit != null && (unit.getMovableFields().contains(field) || unit.getTargetableFields().contains(field));
	}

	@Override
	public boolean isSelectable(Field field, Player forPlayer) {
		return isHighlighted(field, forPlayer);
	}

	@Override
	public boolean isSelected(Unit unit, Player forPlayer) {
		return selectedUnits.get(forPlayer) == unit && unit.getPlayer() == forPlayer;
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		// UNIT SELECTABLE: owned unit && unit is onboard && no unit selected yet || unit is currently selected
		return unit.getPlayer() == forPlayer && unitOnBoard(unit)
				&& (selectedUnits.get(forPlayer) == null || selectedUnits.get(forPlayer) == unit);
	}

	private boolean unitOnBoard(Unit unit) {
		return getGame().getBoard().getFields().stream().anyMatch(f -> f.getUnit() == unit);
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "selectUnit":
			execSelectUnit(player, param);
			break;
		case "selectTargetField":
			execTargetField(player, param);
			break;
		case "selectModalDialog":
			execModalDialog(player, param);
			break;
		case "button":
			if ("doneButton".equals(param)) {
				inTurn.remove(player);
				if (inTurn.isEmpty()) {
					BattleResolver br = new BattleResolver(cc);
					br.calcBattles();
					round++;
					if (round == MAX_ROUNDS || getGame().getBoard().getTotalUnits() == 0) {

						getGame().getBoard().getFields().stream().filter(f -> f.getStructure() != null)
								.filter(f -> f.getUnit() != null)
								.filter(f -> f.getUnit().getPlayer() != f.getStructure().getPlayer())
								.forEach(f -> f.getUnit().getPlayer().incScore(100));

						nextPhase(getGame().getPlayers().get(0));
					} else {
						inTurn.addAll(getGame().getPlayers());
					}
				}
			}
			break;
		}
	}

	private void execModalDialog(Player player, String param) {
		Unit unit = selectedUnits.get(player);
		if (unit == null) {
			log.error("execTargetField but no unit was selected");
			return;
		}
		Field targetField = selectedFields.get(player);
		if (targetField == null) {
			log.error("execTargetField but no target field was selected");
			return;
		}
		if (!"Cancel".equalsIgnoreCase(param)) {
			CommandType commandType = CommandType.valueOf(param);
			cc.addCommand(unit, targetField, commandType);
		}
		selectedUnits.remove(player);
		selectedFields.remove(player);
		possibleCommandTypesOptions.remove(player);
		ObjectNode root = instance.objectNode();
		getMessages().addMessage(player, Constants.RESP_MODAL_DIALOG_DIS, root);
	}

	private void execTargetField(Player player, String param) {
		Unit unit = selectedUnits.get(player);
		if (unit == null) {
			log.error("execTargetField but no unit was selected");
			return;
		}
		Field targetField = getGame().getBoard().getField(param);
		Set<CommandType> possibleCommandTypes = getPossibleCommandTypes(unit, targetField);
		if (possibleCommandTypes.size() == 0) {
			player.getClientMessages().setError(
					"One of your own units is/will be alreay there. De-select your unit or chose another target field.");
		} else if (possibleCommandTypes.size() == 1) {
			cc.addCommand(unit, targetField, possibleCommandTypes.iterator().next());
			selectedUnits.remove(player);
		} else {
			selectedFields.put(player, targetField);
			possibleCommandTypesOptions.put(player, possibleCommandTypes);
		}
	}

	private Set<CommandType> getPossibleCommandTypes(Unit unit, Field targetField) {
		return unit.getPossibleCommandTypes(cc, targetField);
	}

	private void execSelectUnit(Player player, String param) {
		Unit unit = getGame().getUnitById(param);
		Unit currentlySelected = selectedUnits.get(player);
		if (currentlySelected != null && currentlySelected != unit) {
			log.error("Player {} has unit selected", player.getSide());
			return;
		}
		if (currentlySelected == unit) {
			cc.remove(unit);
			cc.addCommand(unit, unit.getDeployedOn(), CommandType.FORTIFY);
			selectedUnits.remove(player);
		} else {
			selectedUnits.put(player, unit);
		}
	}

	@Override
	public void updateUI() {
		getGame().getPlayers().forEach(player -> {
			String title;
			Unit unit = selectedUnits.get(player);
			if (unit != null) {
				title = "Choose a destination field for " + unit.getUnitType().toString();
			} else {
				title = "Command your units. Press `done` when finished. Round " + (round + 1) + " of " + MAX_ROUNDS;
			}
			player.getClientMessages().setTitle(title);
			player.getClientMessages().setScore("Your score: " + player.getScore() + ", opponents score: "
					+ GameUtil.getOtherPlayer(player).getScore());

			if (possibleCommandTypesOptions.get(player) != null) {
				ObjectNode root = instance.objectNode();
				root.set("title", instance.textNode("Choose a command"));
				ArrayNode options = instance.arrayNode();
				for (CommandType ct : possibleCommandTypesOptions.get(player)) {
					ObjectNode option = instance.objectNode();
					option.set("id", instance.textNode(ct.name()));
					option.set("description", instance.textNode(ct.name()));
					options.add(option);
				}
				root.set("options", options);
				getMessages().addMessage(player, Constants.RESP_MODAL_DIALOG_EN, root);
			}

		});
	}

	@Override
	protected void nextPhase(Player firstPlayer) {
		getGame().setCurrentPhase(new DraftPhase(getGame()));
		getGame().getCurrentPhase().init(firstPlayer);
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		buttons.add(
				new UIButton("doneButton", "Done", null, 30, 20, DiffableBoolean.create(!inTurn.contains(forPlayer))));
		return buttons;
	}

	@Override
	public boolean hasCommandFor(Unit unit, Player forPlayer) {
		return cc.hasCommandFor(unit, forPlayer);
	}

	@Override
	public Command getCommand(Unit unit) {
		return cc.get(unit);
	}

	public class CommandCenter {

		@Getter
		private Map<Unit, Command> commands = new HashMap<>();

		public CommandCenter() {
			setAllToFortify();
		}

		public void remove(Unit u) {
			for (Iterator<Map.Entry<Unit, Command>> it = commands.entrySet().iterator(); it.hasNext();) {
				Map.Entry<Unit, Command> en = it.next();
				if (en.getValue().getUnit() == u) {
					it.remove();
				}
			}
		}

		public Unit getUnitMovingFromTo(Field from, Field to) {
			Optional<Command> findFirst = commands.values().stream()
					.filter(f -> f.getTargetField() == to && f.getUnit().getDeployedOn() == from).findFirst();
			if (findFirst.isPresent()) {
				log.debug("Found unit moving from {} to {}. Unit: {}", from.getId(), to.getId(),
						findFirst.get().getUnit());
				return findFirst.get().getUnit();
			}
			return null;
		}

		public void clearCommands() {
			commands.clear();
			setAllToFortify();
		}

		private void setAllToFortify() {
			getGame().getBoard().getFields().stream().filter(f -> f.getUnit() != null).forEach(f -> {
				addCommand(f.getUnit(), f, CommandType.FORTIFY);
			});
		}

		public void addCommand(Unit unit, Field targetField, CommandType command) {
			Command newCommand = new Command(command, unit, targetField);
			commands.put(unit, newCommand);
			log.debug("Added command {} for {} at {} ", command, unit.getUnitType(), targetField.getId());
		}

		public boolean hasCommandFor(Unit unit, Player forPlayer) {
			return commands.values().stream().anyMatch(c -> c.getUnit() == unit && unit.getPlayer() == forPlayer);
		}

		public Command get(Unit unit) {
			return commands.values().stream().filter(c -> c.getUnit() == unit).findFirst().get();
		}

		public void allCommands(Consumer<Command> consumer) {
			commands.values().forEach(consumer);
		}

		public boolean isNotTargetedByOtherOwnUnit(Player player, Field targetField, Unit unit) {
			return !isOccupiedByOwnUnit(player, targetField);
		}

		public boolean isOccupiedByOwnUnit(Player player, Field targetField) {
			return commands.values().stream().filter(c -> c.getUnit().getPlayer() == player)
					.anyMatch(c -> c.getTargetField() == targetField);
		}

		public boolean isOccupiedByEnemyUnit(Player player, Field targetField) {
			return commands.values().stream().filter(c -> c.getUnit().getPlayer() != player)
					.anyMatch(c -> c.getTargetField() == targetField);
		}

	}

	@AllArgsConstructor
	@Value
	public class Command {
		private CommandType commandType;
		private Unit unit;
		private Field targetField;
	}

	public enum CommandType {
		MOVE("M"), FORTIFY("F"), BOMBARD("B"), SUPPORT("S");

		private String description;

		CommandType(String description) {
			this.description = description;
		}

		public String toString() {
			return description;
		}

	}

}
