package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.List;

import de.oglimmer.ggo.logic.phase.BasePhase;
import de.oglimmer.ggo.ui.UIStates;
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
	private Messages messages;

	@Getter
	private UIStates uiStates;

	public Player(Side side, Game game) {
		this.side = side;
		this.game = game;
		uiStates = new UIStates(this);
		game.getBoard().addCities(this);
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
		assert addScore > 0;
		score += addScore;
	}

	public void updateUI() {
		MessageQueue messages = new MessageQueue();
		BasePhase currentPhase = game.getCurrentPhase();
		if (currentPhase != null) {
			currentPhase.updateMessages(messages);
			currentPhase.updateModalDialgs(messages);
		}
		messages.addUpdateUIMessages(game);
		messages.sendMessages();
	}

	public String toString() {
		return "Player [id=" + id + ", side=" + side + "]";
	}
}
