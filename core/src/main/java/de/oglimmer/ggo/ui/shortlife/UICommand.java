package de.oglimmer.ggo.ui.shortlife;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.Command;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UICommand {

	@Getter
	private String commandType;
	@Getter
	private Integer x;
	@Getter
	private Integer y;

	public static UICommand create(Player forPlayer, Unit unit) {
		Command cPCommand = forPlayer.getGame().getCurrentPhase().getCommand(unit, forPlayer);
		if (cPCommand != null) {
			UICommand returnObj = new UICommand();
			returnObj.commandType = cPCommand.getCommandType().toString();
			returnObj.x = (int) cPCommand.getTargetField().getPos().getX();
			returnObj.y = (int) cPCommand.getTargetField().getPos().getY();
			return returnObj;
		}
		return null;
	}

}
