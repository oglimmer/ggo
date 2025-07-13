package de.oglimmer.ggo.logic.battle;

import java.io.Serializable;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Unit;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class Command implements Serializable {
	private static final long serialVersionUID = 1L;
	private CommandType commandType;
	private Unit unit;
	private Field targetField;
}