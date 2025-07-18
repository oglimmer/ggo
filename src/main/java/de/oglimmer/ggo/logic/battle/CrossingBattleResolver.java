package de.oglimmer.ggo.logic.battle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Unit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrossingBattleResolver extends BaseBattleResolver {

	private Set<Set<Unit>> crossingUnits = new HashSet<>();

	public CrossingBattleResolver(CommandCenter cc) {
		super(cc);
	}

	public void battleCrossingUnits() {
		collectCrossingUnits();
		crossingUnits.forEach(this::fight);
	}

	private void fight(Set<Unit> set) {
		assert set.size() == 2;
		Iterator<Unit> it = set.iterator();
		Unit u1 = it.next();
		Unit u2 = it.next();
		resolveBattle(u1, u2, true);
	}

	private void collectCrossingUnits() {
		getCc().stream().filter(c -> c.commandType().isMove()).filter(this::unitAlreadyAdded).forEach(this::check);
		log.debug("Total crossingUnits {}", crossingUnits.size());
	}

	private void check(Command c) {
		Unit unit = c.unit();
		Field oldField = unit.getDeployedOn();
		Field newField = c.targetField();
		Unit crossingUnit = getUnitMovingFromTo(newField, oldField);
		if (crossingUnit != null) {
			Set<Unit> newSet = new HashSet<>();
			newSet.add(unit);
			newSet.add(crossingUnit);
			crossingUnits.add(newSet);
			log.debug("Added crossingUnits {} and {}", unit, crossingUnit);
		}
	}

	private boolean unitAlreadyAdded(Command command) {
		return !crossingUnits.stream().anyMatch(set -> set.contains(command.unit()));
	}

	private Unit getUnitMovingFromTo(Field from, Field to) {
		Optional<Command> findFirst = getCc().stream()
				.filter(c -> c.targetField() == to && c.unit().getDeployedOn() == from).findFirst();
		if (findFirst.isPresent() && findFirst.get().commandType().isMove()) {
			return findFirst.get().unit();
		}
		return null;
	}

}