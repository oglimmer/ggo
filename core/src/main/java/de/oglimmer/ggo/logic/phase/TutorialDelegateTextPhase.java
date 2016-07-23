package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.ui.shortlife.UIButton;
import lombok.Setter;

public class TutorialDelegateTextPhase extends TutorialDelegateBasePhase {

	private static final long serialVersionUID = 1L;

	@Setter
	private boolean hideScore;

	@Setter
	private boolean hideInfo;

	public TutorialDelegateTextPhase(Game game) {
		super(game);
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<UIButton>();
		buttons.add(UIButton.builder().id("doneButton").text("Done").width(60).height(20).hidden(false).build());
		return buttons;
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "button":
			if ("doneButton".equals(param)) {
				nextPhase();
			}
			break;
		}

	}

	@Override
	protected void updateScoreMessages() {
		if (!hideScore) {
			super.updateScoreMessages();
		}
	}

	@Override
	protected void updateInfoMessage(Player player) {
		if (!hideInfo) {
			super.updateInfoMessage(player);
		}
	}

	@Override
	public boolean isSelectable(Field field, Player player) {
		return false;
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return false;
	}

	@Override
	public String toString() {
		return "TutorialDelegateTextPhase [] extends " + super.toString();
	}

}
