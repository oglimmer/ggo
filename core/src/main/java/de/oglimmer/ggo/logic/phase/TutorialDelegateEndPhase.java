package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.ui.shortlife.UIButton;

public class TutorialDelegateEndPhase extends TutorialDelegateBasePhase {

	private static final long serialVersionUID = 1L;

	public TutorialDelegateEndPhase(Game game) {
		super(game);
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<UIButton>();
		return buttons;
	}

	@Override
	protected void updateScoreMessages() {
	}

	@Override
	protected void updateInfoMessage(Player player) {
	}

	@Override
	public String toString() {
		return "TutorialDelegateEndPhase [] extends " + super.toString();
	}

}
