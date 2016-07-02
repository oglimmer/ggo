package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.ui.persistent.Messages;
import de.oglimmer.ggo.ui.persistent.ModalDialog;
import de.oglimmer.ggo.ui.shortlife.UIBoardStateProvider;
import de.oglimmer.ggo.ui.shortlife.UIConnectionStateProvider;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIStates extends de.oglimmer.atmospheremvc.game.UIState {

	private States states;

	class States {

		@Getter
		private ModalDialog modalDialogState;

		@Getter
		private UIBoardStateProvider boardState;

		@Getter
		private Messages messagesState;

		@Getter
		private UIConnectionStateProvider connectionState;

		@Getter
		private String myColor;

		States(Player player) {
			this.modalDialogState = player.getModalDialog();
			this.boardState = new UIBoardStateProvider(player);
			this.connectionState = new UIConnectionStateProvider(player);
			this.messagesState = player.getMessages();
			this.myColor = player.getSide().toString();
		}

	}

	public UIStates(Player player) {
		this.states = new States(player);
	}

	@Override
	protected Object getState() {
		return states;
	}

}
