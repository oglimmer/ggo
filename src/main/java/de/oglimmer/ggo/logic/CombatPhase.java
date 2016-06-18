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
		// UNIT SELECTABLE: owned unit && unit is onboard && no unit selected yet
		return unit.getPlayer() == forPlayer && unitOnBoard(unit) && selectedUnits.get(forPlayer) == null;
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
					calcBattles();
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
		}
	}

	private void calcBattles() {
		battleCrossingUnits();
		battleBattleGrounds();
		moveUnits();
		cc.clearCommands();
	}

	private void moveUnits() {
		Set<Field> movedTo = new HashSet<>();
		cc.getCommands().values().forEach(c -> {
			Field oldField = c.getUnit().getDeployedOn();
			Unit unit = c.getUnit();
			Field newField = c.getTargetField();

			log.debug("Move {} from {} to {}", unit, oldField, newField);

			assert !movedTo.contains(newField);
			movedTo.add(newField);

			unit.setDeployedOn(newField);
			oldField.setUnit(null);
			newField.setUnit(unit);
		});
	}

	private void battleBattleGrounds() {
		Map<Field, Set<Unit>> possibleBattleGrounds = new HashMap<>();
		cc.allCommands(c -> {
			Set<Unit> battleParticipants = possibleBattleGrounds.get(c.getTargetField());
			if (battleParticipants == null) {
				battleParticipants = new HashSet<>();
				possibleBattleGrounds.put(c.getTargetField(), battleParticipants);
			}
			battleParticipants.add(c.getUnit());
			log.debug("possibleBattleGrounds at {} added {}", c.getTargetField().getPos(), battleParticipants);
		});
		for (Iterator<Map.Entry<Field, Set<Unit>>> it = possibleBattleGrounds.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Field, Set<Unit>> en = it.next();
			assert en.getValue().size() > 0 && en.getValue().size() < 3;
			if (en.getValue().size() == 1) {
				log.debug("Remove battleGrounds {}", en.getKey().getId());
				it.remove();
			}
		}
		log.debug("Total battleGrounds {}", possibleBattleGrounds.size());

		possibleBattleGrounds.entrySet().forEach(entry -> {
			// Field f = entry.getKey();
			Set<Unit> units = entry.getValue();
			Iterator<Unit> it = units.iterator();
			Unit u1 = it.next();
			Unit u2 = it.next();
			battle(u1, u2, cc);
		});
	}

	private void battleCrossingUnits() {
		Set<Set<Unit>> crossingUnits = new HashSet<>();
		cc.getCommands().values().forEach(c -> {
			if (c.getCommandType() == CommandType.MOVE) {
				Unit unit = c.getUnit();
				log.debug("Processing unit (battleCrossingUnits): {}", unit);
				if (!crossingUnits.stream().anyMatch(set -> set.contains(unit))) {
					Field oldField = unit.getDeployedOn();
					Field newField = c.getTargetField();

					Unit crossingUnit = cc.getUnitMovingFromTo(newField, oldField);
					if (crossingUnit != null) {
						Set<Unit> s = new HashSet<>();
						s.add(unit);
						s.add(crossingUnit);
						crossingUnits.add(s);
						log.debug("Added crossingUnits {}", s);
					}
				}
			}
		});
		log.debug("Total crossingUnits {}", crossingUnits.size());

		crossingUnits.forEach(set -> {
			assert set.size() == 2;
			Iterator<Unit> it = set.iterator();
			Unit u1 = it.next();
			Unit u2 = it.next();
			battle(u1, u2, cc);
		});
	}

	private void battle(Unit u1, Unit u2, CommandCenter cc) {
		int strength1 = u1.getUnitType().getStrength() + isFortified(u1, cc);
		int strength2 = u2.getUnitType().getStrength() + isFortified(u2, cc);
		if (strength1 == strength2) {
			kill(u1);
			kill(u2);
		} else if (strength1 < strength2) {
			kill(u1);
		} else if (strength1 > strength2) {
			kill(u2);
		}
	}

	private int isFortified(Unit u, CommandCenter cc) {
		return cc.getCommands().get(u).getCommandType() == CommandType.FORTIFY ? 1 : 0;
	}

	private void kill(Unit u) {
		log.debug("Kill unit {}", u);
		u.getPlayer().getClientMessages().setInfo(u.getPlayer().getClientMessages().getInfo()
				+ u.getUnitType().toString() + " on " + u.getDeployedOn().getId() + " got killed. ");
		u.getDeployedOn().setUnit(null);
		cc.remove(u);
	}

	private void execSelectUnit(Player player, String param) {
		Unit unit = getGame().getUnitById(param);
		Unit currentlySelected = selectedUnits.get(player);
		if (currentlySelected != null && currentlySelected != unit) {
			log.error("Player {} has unit selected", player.getSide());
			return;
		}
		if (currentlySelected == unit) {
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
