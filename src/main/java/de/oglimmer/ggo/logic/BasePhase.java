package de.oglimmer.ggo.logic;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.ArrayList;
import java.util.Collection;

import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIButton;
import de.oglimmer.ggo.ui.UIMessages;
import lombok.Getter;

abstract public class BasePhase {

	@Getter
	private MessageQueue messages = new MessageQueue();

	public void execCmd(Player player, String cmd, String param) {
		switch (cmd) {
		case "join":
			player.resetUiState();
			messages.addMessage(player, Constants.RESP_MYCOLOR, instance.textNode(player.getSide().toString()));
			break;
		}
	}

	abstract protected void nextPhase(Player firstPlayer);

	/**
	 * Must be idempotent
	 */
	abstract public void updateUI(Game game);

	final public void diffUIState(Game game) {
		game.getPlayers().forEach(p -> {
			UIBoard uiUpdate = p.getClientUIState().calcDiff(p);
			UIMessages uiMessages = p.getClientMessages().calcDiffMessages();
			messages.addMessage(p, uiUpdate, uiMessages);
		});
	}

	public boolean isHighlighted(Field field, Player player) {
		return false;
	}

	public boolean isSelected(Unit unit, Player forPlayer) {
		return false;
	}

	public boolean isSelectable(Field field, Player player) {
		return false;
	}

	public boolean isSelectable(Unit unit, Player forPlayer) {
		return false;
	}

	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		buttons.add(new UIButton("doneButton", "Done", DiffableBoolean.create(true)));
		return buttons;
	}
}
