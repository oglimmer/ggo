package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

public class Player {

	@Getter
	@NonNull
	private String id;

	@Getter
	@NonNull
	private Side side;

	@Getter
	@NonNull
	private Game game;

	@Getter
	private int credits;

	@Getter
	private int score;

	@Getter
	private List<Unit> unitInHand = new ArrayList<>();

	@Getter
	private UIStates uiStates = new UIStates(this);

	public Player(String id, Side side, Game game) {
		this.id = id;
		this.side = side;
		this.game = game;
	}

	public void resetUiState() {
		uiStates = new UIStates(this);
	}

	public void spendCredits(int toSpend) {
		credits -= toSpend;
	}

	public void incCredits(int additionalCredits) {
		credits += additionalCredits;
	}

	public void incScore(int addScore) {
		score += addScore;
	}

	public void updateUI() {
		MessageQueue messages = new MessageQueue();
		game.getCurrentPhase().updateMessages(messages);
		game.getCurrentPhase().updateModalDialgs(messages);
		messages.addUpdateUIMessages(game);
		messages.sendMessages();
	}

}
