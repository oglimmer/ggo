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
	@NonNull
	// never accessed to set client ui states. this just hold the last transfered
	// ui states retrieved from game.board
	private UIBoard clientUIState = new UIBoard();
	@Getter
	@NonNull
	// holds last transfered and next messages. used to set next messages
	private UIMessages clientMessages = new UIMessages();

	@Getter
	private List<Unit> unitInHand = new ArrayList<>();

	public Player(Side side, Game game) {
		this.side = side;
		this.game = game;
	}

	public void resetUiState() {
		clientUIState = new UIBoard();
		clientMessages = new UIMessages();
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

}
