package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;
import de.oglimmer.ggo.ui.UnitCommandablePhase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatPhase extends BasePhase implements UnitCommandablePhase {

	private int round = 0;

	private Map<Player, Unit> selectedUnits = new HashMap<>();
	private Set<Player> inTurn = new HashSet<>();
	private CommandCenter cc = new CommandCenter();

	public CombatPhase(Game game) {
		super(game);
		inTurn.addAll(game.getPlayers());
		cc.setAllToFortify();
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		Unit unit = selectedUnits.get(forPlayer);
		// FIELD SELECTEDABLE: unit is selected AND a field around that unit
		return unit != null && unit.getDeployedOn().getNeighbords().contains(field);
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
		case "button":
			if ("doneButton".equals(param)) {
				inTurn.remove(player);
				if (inTurn.isEmpty()) {
					BattleResolver br = new BattleResolver(cc);
					br.calcBattles();
					round++;
					if (round == 2 || getGame().getBoard().getTotalUnits() == 0) {
						nextPhase(getGame().getPlayers().get(0));
					} else {
						inTurn.addAll(getGame().getPlayers());
					}
				}
			}
			break;
		}
	}

	private void execTargetField(Player player, String param) {
		Field targetField = getGame().getBoard().getField(param);
		Unit unit = selectedUnits.get(player);
		if (cc.validCommand(unit, targetField)) {
			cc.addCommand(unit, targetField, CommandType.MOVE);
			selectedUnits.remove(player);
		} else {
			player.getClientMessages().setError(
					"One of your own units is/will be alreay there. De-select your unit or chose another target field.");
		}
	}

	private void execSelectUnit(Player player, String param) {
		Unit unit = getGame().getUnitById(param);
		Unit currentlySelected = selectedUnits.get(player);
		if (currentlySelected != null && currentlySelected != unit) {
			log.error("Player {} has unit selected", player.getSide());
			return;
		}
		if (currentlySelected == unit) {
			Command command = cc.get(unit);
			command.setCommandType(CommandType.FORTIFY);
			command.setTargetField(unit.getDeployedOn());
			selectedUnits.remove(player);
		} else {
			selectedUnits.put(player, unit);
		}
	}

	@Override
	public void updateUI() {
		getGame().getPlayers().forEach(player -> {
			player.getClientMessages().setTitle("Command your units.");
		});
	}

	@Override
	protected void nextPhase(Player firstPlayer) {
		getGame().getPlayers().forEach(p -> p.addUnits());
		getGame().setCurrentPhase(new DeployPhase(firstPlayer));
	}

	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		buttons.add(new UIButton("doneButton", "Done", DiffableBoolean.create(!inTurn.contains(forPlayer))));
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

	class CommandCenter {
		@Getter
		private Map<Unit, Command> commands = new HashMap<>();

		public boolean validCommand(Unit unit, Field targetField) {
			return !commands.values().stream().filter(c -> c.getUnit().getPlayer() == unit.getPlayer())
					.anyMatch(c -> c.getTargetField() == targetField);
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

		public void setAllToFortify() {
			getGame().getBoard().getFields().stream().filter(f -> f.getUnit() != null).forEach(f -> {
				addCommand(f.getUnit(), f, CommandType.FORTIFY);
			});
		}

		public void addCommand(Unit unit, Field targetField, CommandType command) {
			Command newCommand = new Command(command, unit, targetField);
			commands.put(unit, newCommand);
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
	}

	@AllArgsConstructor
	@Data
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
