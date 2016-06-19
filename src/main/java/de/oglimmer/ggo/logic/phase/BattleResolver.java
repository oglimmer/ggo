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
import de.oglimmer.ggo.logic.util.GameUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BattleResolver {

	@NonNull
	private CommandCenter cc;

	public void calcBattles() {
		battleBombard();
		battleCrossingUnits();
		battleBattleGrounds();
		moveUnits();
		cc.clearCommands();
	}

	private void battleBombard() {
		Set<Unit> killed = new HashSet<>();
		cc.getCommands().values().stream().filter(c -> c.getCommandType() == CommandType.BOMBARD)
				.forEach(c -> killed.add(c.getTargetField().getUnit()));
		log.debug("Total units killed during bombard {}", killed.size());

		killed.forEach(u -> {
			kill(u, cc);
			GameUtil.getOtherPlayer(u.getPlayer()).incScore(10);
		});
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
			resolveBattle(u1, u2);
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
			resolveBattle(u1, u2);
		});
	}

	private void resolveBattle(Unit u1, Unit u2) {
		int strength1 = u1.getUnitType().getStrength() + isFortified(u1, cc) + isSupported(u1, cc);
		int strength2 = u2.getUnitType().getStrength() + isFortified(u2, cc) + isSupported(u1, cc);
		if (strength1 == strength2) {
			score(u1);
			score(u2);
			kill(u1, cc);
			kill(u2, cc);
		} else if (strength1 < strength2) {
			score(u2);
			kill(u1, cc);
		} else if (strength1 > strength2) {
			score(u1);
			kill(u2, cc);
		}
	}

	private void score(Unit unit) {
		System.out.println("score for unit "+unit+"?"+cc.get(unit).getCommandType());
		if (cc.get(unit).getCommandType() == CommandType.MOVE) {
			unit.getPlayer().incCredits(10);
		}
	}

	private int isSupported(Unit u, CommandCenter cc2) {
		return 0;
	}

	private int isFortified(Unit u, CommandCenter cc) {
		return cc.getCommands().get(u).getCommandType() == CommandType.FORTIFY ? 1 : 0;
	}

	private void kill(Unit u, CommandCenter cc) {
		log.debug("Kill unit {}", u);
		u.getPlayer().getClientMessages()
				.appendInfo(u.getUnitType().toString() + " on " + u.getDeployedOn().getId() + " got killed. ");
		u.getDeployedOn().setUnit(null);
		cc.remove(u);
	}
}