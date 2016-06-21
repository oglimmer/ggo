package de.oglimmer.ggo.logic.phase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Unit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BattleGroundResolver extends BaseBattleResolver {

	private Map<Field, Set<Unit>> possibleBattleGrounds = new HashMap<>();

	public BattleGroundResolver(CommandCenter cc) {
		super(cc);
	}

	public void battleBattleGrounds() {
		collectFieldsWithUnits();
		removeFieldWithJustOneUnit();
		log.debug("Total battleGrounds {}", possibleBattleGrounds.size());
		fightAllBattleGrounds();
	}

	private void fightAllBattleGrounds() {
		possibleBattleGrounds.values().forEach(this::fightBattleGround);
	}

	private void fightBattleGround(Set<Unit> units) {
		Iterator<Unit> it = units.iterator();
		Unit u1 = it.next();
		Unit u2 = it.next();
		resolveBattle(u1, u2);
	}

	private void removeFieldWithJustOneUnit() {
		for (Iterator<Map.Entry<Field, Set<Unit>>> it = possibleBattleGrounds.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Field, Set<Unit>> en = it.next();
			Set<Unit> battlingUnits = en.getValue();
			assert battlingUnits.size() > 0 && battlingUnits.size() < 3;
			if (battlingUnits.size() == 1) {
				log.debug("Remove battleGrounds {}", en.getKey().getId());
				it.remove();
			}
		}
	}

	private void collectFieldsWithUnits() {
		getCc().allCommands(this::collectFieldsWithUnits);
	}

	private void collectFieldsWithUnits(Command c) {
		if (c.getCommandType().isFortify() || c.getCommandType().isMove()) {
			possibleBattleGrounds.computeIfAbsent(c.getTargetField(), t -> new HashSet<>()).add(c.getUnit());
		}
	}

}