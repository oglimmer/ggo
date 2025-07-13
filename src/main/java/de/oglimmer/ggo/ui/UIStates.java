package de.oglimmer.ggo.ui;

import java.io.Serializable;

import de.oglimmer.ggo.atmospheremvc.game.UIState;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.ui.persistent.Messages;
import de.oglimmer.ggo.ui.persistent.ModalDialog;
import de.oglimmer.ggo.ui.shortlife.UIBoardStateProvider;
import de.oglimmer.ggo.ui.shortlife.UIConnectionStateProvider;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIStates extends UIState {

	private static final long serialVersionUID = 1L;

	private States states;

	@ToString
	class States implements Serializable {

		private static final long serialVersionUID = 1L;

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
