package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.UnitType;
import de.oglimmer.ggo.ui.shortlife.UIButton;
import lombok.Setter;

public class TutorialDelegateDraftPhase extends TutorialDelegateBasePhase {

	private static final long serialVersionUID = 1L;

	@Setter
	private UnitType unitType;

	public TutorialDelegateDraftPhase(Game game) {
		super(game);
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return false;
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "button":
			if (param.startsWith("buy") && !isAutoEnd()) {
				nextPhase();
			}
			break;
		}

	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		UnitType type = unitType;
		buttons.add(UIButton.builder().id("buy" + type.toString()).text(Integer.toString(type.getCost()))
				.graphic(type.toString()).width(48).height(48).hidden(forPlayer.getCredits() < type.getCost()).build());
		return buttons;
	}

	@Override
	public String toString() {
		return "TutorialDelegateDraftPhase [unitType=" + unitType + "] extends " + super.toString();
	}

}
