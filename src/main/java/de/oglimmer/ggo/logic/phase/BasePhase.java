package de.oglimmer.ggo.logic.phase;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.ArrayList;
import java.util.Collection;

import de.oglimmer.ggo.logic.Constants;
import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.MessageQueue;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIButton;
import de.oglimmer.ggo.ui.UIMessages;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract public class BasePhase {

	@Getter
	@NonNull
	private Game game;

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
	abstract public void updateUI();

	final public void diffUIState() {
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
		buttons.add(new UIButton("doneButton", "Done", null, 30, 20, DiffableBoolean.create(true)));
		return buttons;
	}
}
