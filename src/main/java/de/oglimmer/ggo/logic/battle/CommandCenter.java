package de.oglimmer.ggo.logic.battle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CommandCenter implements Serializable {

	private static final long serialVersionUID = 1L;

	@NonNull
	private Game game;

	@Setter
	@Getter
	private boolean dry;

	private Map<Unit, Command> commands = new HashMap<>();

	@Getter
	private Map<Player, StringBuilder> infoMessages = new HashMap<>();

	public CommandCenter(CommandCenter toCopy, boolean dryRun) {
		this.game = toCopy.game;
		this.commands = new HashMap<>(toCopy.commands);
		this.dry = dryRun;
	}

	public void addCommand(Unit unit, Field targetField, CommandType command) {
		Command newCommand = new Command(command, unit, targetField);
		commands.put(unit, newCommand);
		log.trace("Added command {} for {} at {} ", command.toString(), unit.getUnitType(), targetField.getId());
	}

	public void removeCommandForUnit(Unit u) {
		removeCommandForUnitRecursive(u, new HashSet<>());
	}
	
	private void removeCommandForUnitRecursive(Unit u, Set<Unit> processedUnits) {
		// Prevent infinite recursion
		if (processedUnits.contains(u)) {
			return;
		}
		processedUnits.add(u);
		
		Command commandToRemove = commands.get(u);
		
		// If this was a MOVE command, we need to check for cascading cancellations
		if (commandToRemove != null && commandToRemove.commandType().isMove()) {
			Field fieldBeingVacated = commandToRemove.unit().getDeployedOn();
			
			// Remove the original command first
			commands.remove(u);
			
			// Find other units that were planning to move into the field being vacated
			Set<Unit> unitsToCancel = commands.entrySet().stream()
					.filter(entry -> entry.getValue().commandType().isMove())
					.filter(entry -> entry.getValue().targetField() == fieldBeingVacated)
					.filter(entry -> entry.getValue().unit().getPlayer() == u.getPlayer()) // Only same player
					.map(Map.Entry::getKey)
					.collect(Collectors.toSet());
			
			// Recursively cancel those move commands
			for (Unit unitToCancel : unitsToCancel) {
				log.trace("Cascaded cancellation: Unit {} command canceled due to {} move cancellation", 
						unitToCancel.getUnitType(), u.getUnitType());
				removeCommandForUnitRecursive(unitToCancel, processedUnits);
				// Set to FORTIFY after recursive cancellation
				addCommand(unitToCancel, unitToCancel.getDeployedOn(), CommandType.FORTIFY);
			}
		} else {
			// For non-MOVE commands, just remove the command normally
			for (Iterator<Map.Entry<Unit, Command>> it = commands.entrySet().iterator(); it.hasNext();) {
				Map.Entry<Unit, Command> en = it.next();
				if (en.getValue().unit() == u) {
					it.remove();
				}
			}
		}
	}

	public void setAllToFortify() {
		game.getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.forEach(f -> addCommand(f.getUnit(), f, CommandType.FORTIFY));
	}

	// ACCESS

	public Command getByUnit(Unit unit) {
		return commands.get(unit);
	}

	public Set<Command> getByTargetField(Player forPlayer, Field f) {
		return commands.values().stream().filter(c -> c.unit().getPlayer() == forPlayer)
				.filter(c -> c.targetField() == f).collect(Collectors.toSet());
	}

	public void allCommands(Consumer<Command> consumer) {
		commands.values().forEach(consumer);
	}

	public Stream<Command> stream() {
		return commands.values().stream();
	}

	// BATTLE

	public void calcBattle() {
		BombarbResolver bomb = new BombarbResolver(this);
		bomb.collectTargets();

		CrossingBattleResolver br = new CrossingBattleResolver(this);
		br.battleCrossingUnits();

		BattleGroundResolver bgr = new BattleGroundResolver(this);
		bgr.battleBattleGrounds();

		bomb.killTargets();

		if (!dry) {
			MoveResolver mr = new MoveResolver(this);
			mr.moveUnits();
		}
	}

}