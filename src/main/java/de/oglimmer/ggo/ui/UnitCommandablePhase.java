package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.logic.CombatPhase.Command;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;

public interface UnitCommandablePhase {

	boolean hasCommandFor(Unit unit, Player forPlayer);

	Command getCommand(Unit unit);

}
