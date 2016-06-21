package de.oglimmer.ggo.logic.phase;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Unit;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class Command {
	private CommandType commandType;
	private Unit unit;
	private Field targetField;
}