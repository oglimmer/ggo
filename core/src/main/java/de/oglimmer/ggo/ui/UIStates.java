package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.logic.Messages;
import de.oglimmer.ggo.logic.Player;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIStates extends de.oglimmer.atmospheremvc.game.UIState {

	private States states;

	class States {

		@Getter
		private UIBoardStateProvider boardState;

		@Getter
		private Messages messagesState;

		@Getter
		private UIConnectionStateProvider connectionState;

		@Getter
		private String myColor;

		States(Player player) {
			this.boardState = new UIBoardStateProvider(player);
			this.connectionState = new UIConnectionStateProvider(player);
			this.messagesState = player.getMessages();
			this.myColor = player.getSide().toString();
		}

	}

	public UIStates(Player player) {
		this.states = new States(player);
	}

	public UIBoardStateProvider getBoardState() {
		return states.getBoardState();
	}

	public Messages getMessagesState() {
		return states.getMessagesState();
	}

	public UIConnectionStateProvider getConnectionState() {
		return states.getConnectionState();
	}

	@Override
	protected Object getState() {
		return states;
	}

}
