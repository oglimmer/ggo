package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.ui.shortlife.UIButton;
import lombok.Setter;

public class TutorialDelegateCombatPhase extends TutorialDelegateBasePhase {

	private static final long serialVersionUID = 1L;

	@Setter
	private Field unit;
	@Setter
	private Field field;

	public TutorialDelegateCombatPhase(Game game) {
		super(game);
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return this.unit != null && this.unit.getUnit() == unit && super.isSelectable(unit, forPlayer);
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		return this.field == field && super.isHighlighted(field, forPlayer);
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "selectUnit":
			nextPhase();
			break;
		case "selectTargetField":
			nextPhase();
			break;
		case "selectModalDialog":
			nextPhase();
			break;
		}
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		return buttons;
	}

	@Override
	public String toString() {
		return "TutorialDelegateCombatPhase [unit=" + unit + ", field=" + (field != null ? field.getId() : "null")
				+ "] extends " + super.toString();
	}

}
