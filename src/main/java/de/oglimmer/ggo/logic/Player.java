package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIButton;
import de.oglimmer.ggo.ui.UIMessages;
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
	@NonNull
	// never accessed to set client ui states. this just hold the last transfered
	// ui states retrieved from game.board
	private UIBoard clientUIState = new UIBoard();
	@Getter
	@NonNull
	// holds last transfered and next messages. used to set next messages
	private UIMessages clientMessages = new UIMessages();
	@Getter
	@NonNull
	// never accessed to set client ui states. this just hold the last transfered
	// ui states retrieved from phase.buttons
	private Map<String, UIButton> buttons = new HashMap<>();

	@Getter
	private List<Unit> unitInHand = new ArrayList<>();

	public Player(String id, Side side, Game game) {
		this.id = id;
		this.side = side;
		this.game = game;
		addUnits();
	}

	public void addUnits() {
		unitInHand.add(new Unit(this, UnitType.INFANTERY));
//		unitInHand.add(new Unit(this, UnitType.TANK));
//		unitInHand.add(new Unit(this, UnitType.AIRBORNE));
		// unitInHand.add(new Unit(this, UnitType.INFANTERY));
		// unitInHand.add(new Unit(this, UnitType.INFANTERY));
		// unitInHand.add(new Unit(this, UnitType.AIRBORNE));
		// unitInHand.add(new Unit(this, UnitType.HELICOPTER));
		// unitInHand.add(new Unit(this, UnitType.ARTILLERY));
	}

	public void resetUiState() {
		clientUIState = new UIBoard();
		clientMessages = new UIMessages();
		buttons = new HashMap<>();
	}

}
