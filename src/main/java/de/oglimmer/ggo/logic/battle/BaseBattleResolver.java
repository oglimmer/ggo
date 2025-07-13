package de.oglimmer.ggo.logic.battle;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class BaseBattleResolver {

	@Getter
	private CommandCenter cc;

	protected void resolveBattle(Unit u1, Unit u2, boolean crossing) {
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
			if (crossing) {
				killCrossingBoth(u1, u2);
			} else {
				killBoth(u1, u2);
			}
		} else if (total1 < total2) {
			score(u2);
			if (crossing) {
				killCrossingOne(u1, u2);
			} else {
				killOne(u1, u2);
			}
		} else if (total1 > total2) {
			score(u1);
			if (crossing) {
				killCrossingOne(u2, u1);
			} else {
				killOne(u2, u1);
			}
		}
	}

	private void score(Unit winningUnit) {
		score(winningUnit, cc);
	}

	protected static void score(Unit winningUnit, CommandCenter cc) {
		CommandType ct = cc.getByUnit(winningUnit).getCommandType();
		if (!cc.isDry()) {
			int score = 0;
			if (ct == CommandType.MOVE) {
				score = 10;
			} else if (ct == CommandType.BOMBARD) {
				score = 5;
			}
			if (score > 0) {
				log.debug("Unit {} scores for {} points by {}", winningUnit, score, ct);
				winningUnit.getPlayer().incScore(score);
			}
		}
	}

	protected int isSupported(Unit u) {
		return (int) cc.getByTargetField(u.getPlayer(), u.getDeployedOn()).stream()
				.filter(c -> c.getCommandType().isSupport()).count();
	}

	protected int isFortified(Unit u) {
		return cc.getByUnit(u).getCommandType() == CommandType.FORTIFY ? 1 : 0;
	}

	protected void killCrossingOne(Unit killed, Unit killer) {
		addInfo(killed.getPlayer(),
				"DEFEAT: Your " + killed.getUnitType() + " on " + killed.getDeployedOn().getId()
						+ " got destroyed while crossing " + killer.getUnitType() + " from "
						+ killer.getDeployedOn().getId() + ".");
		addInfo(killer.getPlayer(), "VICTORY: Your " + killer.getUnitType() + " on " + killer.getDeployedOn().getId()
				+ " destroyed " + killed.getUnitType() + " on " + killed.getDeployedOn().getId() + " while crossing.");
		kill(killed, killer);
	}

	protected void killCrossingBoth(Unit u1, Unit u2) {
		addInfo(u1.getPlayer(), "DEFEAT: Your " + u1.getUnitType() + " on " + u1.getDeployedOn().getId()
				+ " got destroyed while crossing by " + u2.getUnitType() + " from " + u2.getDeployedOn().getId() + ".");
		addInfo(u2.getPlayer(), "DEFEAT: Your " + u2.getUnitType() + " on " + u2.getDeployedOn().getId()
				+ " got destroyed while crossing by " + u1.getUnitType() + " from " + u1.getDeployedOn().getId() + ".");
		kill(u1, u2);
		kill(u2, u1);
	}

	protected void killOne(Unit killed, Unit killer) {
		addInfo(killed.getPlayer(), "DEFEAT: Your " + killed.getUnitType() + " on " + killed.getDeployedOn().getId()
				+ " got destroyed by " + killer.getUnitType() + " from " + killer.getDeployedOn().getId() + ".");
		addInfo(killer.getPlayer(), "VICTORY: Your " + killer.getUnitType() + " on " + killer.getDeployedOn().getId()
				+ " destroyed " + killed.getUnitType() + " on " + killed.getDeployedOn().getId() + ".");
		kill(killed, killer);
	}

	protected void killBoth(Unit u1, Unit u2) {
		addInfo(u1.getPlayer(), "DEFEAT: Your " + u1.getUnitType() + " on " + u1.getDeployedOn().getId()
				+ " got destroyed by " + u2.getUnitType() + " from " + u2.getDeployedOn().getId() + ".");
		addInfo(u2.getPlayer(), "DEFEAT: Your " + u2.getUnitType() + " on " + u2.getDeployedOn().getId()
				+ " got destroyed by " + u1.getUnitType() + " from " + u1.getDeployedOn().getId() + ".");
		kill(u1, u2);
		kill(u2, u1);
	}

	protected void killBombarb(Unit unitKilled, Unit killer) {
		addInfo(unitKilled.getPlayer(),
				"DEFEAT: Your " + unitKilled.getUnitType() + " on " + unitKilled.getDeployedOn().getId()
						+ " got bombarded by " + killer.getUnitType() + " from " + killer.getDeployedOn().getId()
						+ ".");
		addInfo(killer.getPlayer(), "VICTORY: Your " + killer.getUnitType() + " on " + killer.getDeployedOn().getId()
				+ " bombarded " + unitKilled.getUnitType() + " on " + unitKilled.getDeployedOn().getId() + ".");
		kill(unitKilled, killer);
	}

	private void kill(Unit unitKilled, Unit killer) {
		if (!cc.isDry()) {
			log.debug("Kill unit {} by {}", unitKilled, killer);
			unitKilled.getDeployedOn().setUnit(null);
		}
		cc.removeCommandForUnit(unitKilled);
	}

	private void addInfo(Player p, String text) {
		cc.getInfoMessages().computeIfAbsent(p, t -> new StringBuilder()).append(text);
	}

}
