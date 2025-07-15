package de.oglimmer.ggo.logic.battle;

import java.util.HashMap;
import java.util.Map;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Unit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MoveResolver {

	@NonNull
	private CommandCenter cc;

	private Map<Field, Unit> targetFieldToUnit = new HashMap<>();

	public void moveUnits() {
		collect();
		setOldFieldNull();
		setNewField();
	}

	private void collect() {
		cc.stream().filter(c -> c.commandType() == CommandType.MOVE)
				.forEach(c -> targetFieldToUnit.put(c.targetField(), c.unit()));
	}

	private void setNewField() {
		targetFieldToUnit.entrySet().forEach(en -> {
			Unit unit = en.getValue();
			Field newField = en.getKey();
			unit.setDeployedOn(newField);
			newField.setUnit(unit);
		});
	}

	private void setOldFieldNull() {
		targetFieldToUnit.entrySet().forEach(en -> {
			Unit unit = en.getValue();
			Field oldField = unit.getDeployedOn();
			oldField.setUnit(null);
		});
	}

}