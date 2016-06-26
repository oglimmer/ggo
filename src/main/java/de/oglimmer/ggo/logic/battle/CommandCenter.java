package de.oglimmer.ggo.logic.battle;

import java.util.HashMap;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CommandCenter {

	@NonNull
	private Game game;

	private Map<Unit, Command> commands = new HashMap<>();

	public void addCommand(Unit unit, Field targetField, CommandType command) {
		Command newCommand = new Command(command, unit, targetField);
		commands.put(unit, newCommand);
		log.trace("Added command {} for {} at {} ", command, unit.getUnitType(), targetField.getId());
	}

	public void removeCommandForUnit(Unit u) {
		for (Iterator<Map.Entry<Unit, Command>> it = commands.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Unit, Command> en = it.next();
			if (en.getValue().getUnit() == u) {
				it.remove();
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
		return commands.values().stream().filter(c -> c.getUnit().getPlayer() == forPlayer)
				.filter(c -> c.getTargetField() == f).collect(Collectors.toSet());
	}

	public void allCommands(Consumer<Command> consumer) {
		commands.values().forEach(consumer);
	}

	public Stream<Command> stream() {
		return commands.values().stream();
	}

}