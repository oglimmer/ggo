package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.List;

import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIMessages;
import de.oglimmer.ggo.util.RandomString;
import lombok.Getter;
import lombok.NonNull;

public class Player {

	@Getter
	private String id = RandomString.getRandomStringHex(8);

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

	public Player(Side side, Game game) {
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
