package de.oglimmer.ggo.logic.phase;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.UnitType;
import lombok.Setter;

public class TutorialDelegateDeployPhase extends TutorialDelegateBasePhase {

	private static final long serialVersionUID = 1L;

	@Setter
	private UnitType unitType;
	@Setter
	private Field field;

	public TutorialDelegateDeployPhase(Game game) {
		super(game);
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return unit.getUnitType() == unitType && super.isSelectable(unit, forPlayer);
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		return this.field == field && super.isHighlighted(field, forPlayer);
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "selectHandCard":
			nextPhase();
			break;
		case "selectTargetField":
			nextPhase();
			break;
		}
	}

	@Override
	public String toString() {
		return "TutorialDelegateDeployPhase [unitType=" + unitType + ", field="
				+ (field != null ? field.getId() : "null") + "] extends " + super.toString();
	}

}
