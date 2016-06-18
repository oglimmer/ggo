package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.phase.CombatPhase.Command;

public interface UnitCommandablePhase {

	boolean hasCommandFor(Unit unit, Player forPlayer);

	Command getCommand(Unit unit);

}
