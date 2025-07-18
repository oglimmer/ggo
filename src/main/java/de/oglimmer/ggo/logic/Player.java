package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.oglimmer.ggo.websocket.WebSocketSessionCache;
import de.oglimmer.ggo.websocket.WebSocketSessionCacheItem;
import de.oglimmer.ggo.websocket.com.MessageQueue;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.phase.BasePhase;
import de.oglimmer.ggo.ui.UIStates;
import de.oglimmer.ggo.ui.persistent.Messages;
import de.oglimmer.ggo.ui.persistent.ModalDialog;
import de.oglimmer.ggo.random.RandomString;
import lombok.Getter;
import lombok.Setter;

public class Player {

	private static final long serialVersionUID = 1L;

	@Getter
	private String id = RandomString.getRandomStringHex(8);

	@Getter
	@Setter
	private Date lastAction;

	@Getter
	@Setter
	private Date lastConnection;

	@Getter
	private Side side;

	@Getter
	private String email;
	@Getter
	@Setter
	private boolean firstEmail;

	@Getter
	private Game game;

	@Getter
	private int credits;

	@Getter
	private int score;

	@Getter
	private List<Unit> unitInHand = new ArrayList<>();

	@Getter
	private Messages messages = new Messages();

	@Getter
	private ModalDialog modalDialog = new ModalDialog();

	@Getter
	private UIStates uiStates;

	public Player(Side side, String email, Game game) {
		this.side = side;
		this.game = game;
		this.email = email;
		uiStates = new UIStates(this);
		game.getBoard().addCities(this);
		firstEmail = true;
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
		BasePhase currentPhase = game.getCurrentPhase();
		if (currentPhase != null) {
			currentPhase.updateMessages();
			currentPhase.updateModalDialgs();
		}
		MessageQueue messages = new MessageQueue(game);
		messages.process();
	}

	public void notifyForAction() {
		WebSocketSessionCacheItem item = WebSocketSessionCache.INSTANCE.getItem(this);
		if (item == null || item.isDisconnected()) {
			EmailService.INSTANCE.gameNeedsYourAction(this);
		}
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", side=" + side + "]";
	}

}
