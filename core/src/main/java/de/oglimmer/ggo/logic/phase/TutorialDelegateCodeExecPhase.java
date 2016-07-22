package de.oglimmer.ggo.logic.phase;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.util.Strings;
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
		nextPhase();
	}

	@Override
	public String toString(int lvl) {
		return "<br/>" + Strings.repeat("&nbsp;", lvl * 2) + "TutorialDelegateCodeExecPhase [] extends " + super.toString(lvl);
	}

}
