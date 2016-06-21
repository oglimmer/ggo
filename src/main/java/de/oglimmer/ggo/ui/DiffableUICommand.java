package de.oglimmer.ggo.ui;

import java.util.function.Consumer;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.phase.Command;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class DiffableUICommand {

	private static final DiffableUICommand REMOVE_ITEM = new DiffableUICommand();

	@Getter
	@Setter
	private String commandType;
	@Getter
	@Setter
	private DiffableInteger x;
	@Getter
	@Setter
	private DiffableInteger y;

	private DiffableUICommand() {
		// REMOVE_ITEM
	}

	public DiffableUICommand(String commandType, int x, int y) {
		this.commandType = commandType;
		this.x = DiffableInteger.create(x);
		this.y = DiffableInteger.create(y);
	}

	public static DiffableUICommand create(Player forPlayer, Unit unit) {
		Command cPCommand = forPlayer.getGame().getCurrentPhase().getCommand(unit, forPlayer);
		if (cPCommand != null) {
			return new DiffableUICommand(cPCommand.getCommandType().toString(),
					(int) cPCommand.getTargetField().getPos().getX(), (int) cPCommand.getTargetField().getPos().getY());
		}
		return null;
	}

	public static boolean diffAndUpdate(DiffableUICommand thiz, Player forPlayer, Unit unit,
			Consumer<DiffableUICommand> diffTarget, Consumer<DiffableUICommand> thizHolder) {

		DiffableUICommand latest = create(forPlayer, unit);
		if ((thiz == null && latest == null) || (thiz != null && thiz.equals(latest))) {
			return false;
		}
		if (latest == null) {
			thizHolder.accept(latest);
			diffTarget.accept(REMOVE_ITEM);
		} else {
			thizHolder.accept(latest);
			diffTarget.accept(latest);
		}
		return true;
	}

}
