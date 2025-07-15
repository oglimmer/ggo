package de.oglimmer.ggo.logic.battle;

import java.util.HashSet;
import java.util.Set;

import de.oglimmer.ggo.logic.Unit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BombarbResolver extends BaseBattleResolver {

    private final Set<Units> targetByBombard = new HashSet<>();

    public BombarbResolver(CommandCenter cc) {
        super(cc);
    }

    public void collectTargets() {
        getCc().stream().filter(c -> c.commandType().isBombard()).forEach(this::collectTarget);
    }

    public void killTargets() {
        targetByBombard.forEach(this::kilTarget);
    }

    private void collectTarget(Command c) {
        if (c.targetField().getUnit() == null) {
            log.error("Target field {} of command {} is empty", c.targetField().getId(), c.toString());
            return;
        }
        if (c.unit() == null) {
            log.error("Unit is null of command {}", c.toString());
            return;
        }
        targetByBombard.add(new Units(c.targetField().getUnit(), c.unit()));
        score(c.unit(), getCc());
        log.debug("Unit {} marked to be killed due to bombard by {}", c.targetField().getUnit(), c.unit());
    }

    private void kilTarget(Units u) {
        killBombarb(u.target(), u.killer());
    }

    record Units(Unit target, Unit killer) {
    }

}