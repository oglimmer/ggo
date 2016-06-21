package de.oglimmer.ggo.logic.phase;

import de.oglimmer.ggo.logic.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class BaseBattleResolver {

	@Getter
	private CommandCenter cc;

	protected void resolveBattle(Unit u1, Unit u2) {
		int for1 = isFortified(u1);
		int for2 = isFortified(u2);
		int sup1 = isSupported(u1);
		int sup2 = isSupported(u2);
		int strength1 = u1.getUnitType().getStrength();
		int strength2 = u2.getUnitType().getStrength();
		int total1 = strength1 + for1 + sup1;
		int total2 = strength2 + for2 + sup2;
		log.debug("FIGHT:");
		log.debug("{}:{},{},{}={}", u1.getPlayer().getSide(), strength1, for1, sup1, total1);
		log.debug("{}:{},{},{}={}", u2.getPlayer().getSide(), strength2, for2, sup2, total2);
		if (total1 == total2) {
			score(u1);
			score(u2);
			kill(u1);
			kill(u2);
		} else if (total1 < total2) {
			score(u2);
			kill(u1);
		} else if (total1 > total2) {
			score(u1);
			kill(u2);
		}
	}

	private void score(Unit winningUnit) {
		score(winningUnit, cc.getByUnit(winningUnit).getCommandType());
	}

	static void score(Unit winningUnit, CommandType ct) {
		int score = 0;
		if (ct == CommandType.MOVE) {
			score = 10;
		} else if (ct == CommandType.BOMBARD) {
			score = 5;
		}
		log.debug("Unit {} scores for {} points by {}", winningUnit, score, ct);
		winningUnit.getPlayer().incScore(score);
	}

	protected int isSupported(Unit u) {
		return (int) cc.getByTargetField(u.getPlayer(), u.getDeployedOn()).stream()
				.filter(c -> c.getCommandType().isSupport()).count();
	}

	protected int isFortified(Unit u) {
		return cc.getByUnit(u).getCommandType() == CommandType.FORTIFY ? 1 : 0;
	}

	protected void kill(Unit u) {
		log.debug("Kill unit {}", u);
		u.getPlayer().getClientMessages()
				.appendInfo(u.getUnitType().toString() + " on " + u.getDeployedOn().getId() + " got killed. ");
		u.getDeployedOn().setUnit(null);
		cc.removeCommandForUnit(u);
	}

}
