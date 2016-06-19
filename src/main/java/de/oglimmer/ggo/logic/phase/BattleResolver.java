package de.oglimmer.ggo.logic.phase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.phase.CombatPhase.CommandCenter;
import de.oglimmer.ggo.logic.phase.CombatPhase.CommandType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class BattleResolver {

	private CommandCenter cc;

	public void calcBattles() {
		battleCrossingUnits();
		battleBattleGrounds();
		moveUnits();
		cc.clearCommands();
	}

	private void moveUnits() {
		Map<Field, Unit> allMoves = new HashMap<>();
		cc.allCommands(c -> {
			if (c.getCommandType() == CommandType.MOVE) {
				assert !allMoves.containsKey(c.getTargetField());
				allMoves.put(c.getTargetField(), c.getUnit());
			}
		});

		allMoves.entrySet().forEach(en -> {
			Unit unit = en.getValue();
			Field oldField = unit.getDeployedOn();
			oldField.setUnit(null);
		});

		allMoves.entrySet().forEach(en -> {
			Unit unit = en.getValue();
			Field newField = en.getKey();
			unit.setDeployedOn(newField);
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
		u.getPlayer().getClientMessages()
				.appendInfo(u.getUnitType().toString() + " on " + u.getDeployedOn().getId() + " got killed. ");
		u.getDeployedOn().setUnit(null);
		cc.remove(u);
	}
}