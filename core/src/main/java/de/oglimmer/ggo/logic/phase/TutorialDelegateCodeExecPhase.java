package de.oglimmer.ggo.logic.phase;

import de.oglimmer.ggo.logic.Game;
import lombok.Setter;

public class TutorialDelegateCodeExecPhase extends TutorialDelegateBasePhase {

	private static final long serialVersionUID = 1L;

	@Setter
	private Runnable execCode;

	public TutorialDelegateCodeExecPhase(Game game) {
		super(game);
	}

	@Override
	public void initTutorialStep() {
		execCode.run();
		super.initTutorialStep();
	}

	@Override
	public String toString() {
		return "TutorialDelegateCodeExecPhase [] extends " + super.toString();
	}

}
