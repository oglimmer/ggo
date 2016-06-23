package de.oglimmer.ggo.logic;

import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIMessages;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIStates {

	@Getter
	// never accessed to set client ui states. this just hold the last
	// transfered
	// ui states retrieved from game.board
	private UIBoard clientUIState;

	@Getter
	// holds last transfered and next messages. used to set next messages
	private UIMessages clientMessages;

	@Getter
	// holds last transfered and next messages. used to set next messages
	private UIConnectedState connected;

	public UIStates(Player player) {
		this.clientUIState = new UIBoard();
		this.clientMessages = new UIMessages();
		this.connected = new UIConnectedState(player);
	}
}
