package de.oglimmer.ggo.logic;

import java.lang.reflect.InvocationTargetException;

import de.oglimmer.ggo.logic.ai.AiStrategy;
import de.oglimmer.ggo.logic.phase.CombatCommandPhase;
import de.oglimmer.ggo.logic.phase.CombatDisplayPhase;
import de.oglimmer.ggo.logic.phase.DeployPhase;
import de.oglimmer.ggo.logic.phase.DraftPhase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayerAi extends Player {

	private static final long serialVersionUID = 1L;

	private AiStrategy strategy;

	public PlayerAi(Side side, Game game, Class<? extends AiStrategy> clazz) {
		super(side, null, game);
		try {
			strategy = clazz.getConstructor(Player.class, Game.class).newInstance(this, game);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error("Failed to create AiStrategy strategy", e);
		}
	}

	@Override
	public void notifyForAction() {
		if (getGame().getCurrentPhase() instanceof DraftPhase) {
			strategy.draft();
		} else if (getGame().getCurrentPhase() instanceof DeployPhase) {
			strategy.deploy();
		} else if (getGame().getCurrentPhase() instanceof CombatCommandPhase) {
			strategy.command();
		} else if (getGame().getCurrentPhase() instanceof CombatDisplayPhase) {
			((CombatDisplayPhase) getGame().getCurrentPhase()).execDoneButton(this);
		}
	}
}
