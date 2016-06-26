package de.oglimmer.ggo.logic.battle;

import java.util.HashSet;
import java.util.Set;

import de.oglimmer.ggo.logic.Unit;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BombarbResolver extends BaseBattleResolver {

	private Set<Units> targetByBombard = new HashSet<>();

	public BombarbResolver(CommandCenter cc) {
		super(cc);
	}

	public void killTargets() {
		targetByBombard.forEach(this::kilTarget);
	}

	private void kilTarget(Units u) {
		kill(u.getTarget(), u.getKiller());
	}

	public void collectTargets() {
		getCc().stream().filter(c -> c.getCommandType().isBombard()).forEach(c -> collectTarget(c));
	}

	private void collectTarget(Command c) {
		targetByBombard.add(new Units(c.getTargetField().getUnit(), c.getUnit()));
		score(c.getUnit(), CommandType.BOMBARD);
		log.debug("Unit {} marked to be killed due to bombard by {}", c.getTargetField().getUnit(), c.getUnit());
	}

	@Value
	class Units {
		private Unit target;
		private Unit killer;
	}

}