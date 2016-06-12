package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	private List<Unit> unitInHand = new ArrayList<>();

	public Player(String id, Side side, Game game) {
		this.id = id;
		this.side = side;
		this.game = game;
		unitInHand.add(new Unit(this, UnitType.INFANTERY));
		unitInHand.add(new Unit(this, UnitType.INFANTERY));
		unitInHand.add(new Unit(this, UnitType.INFANTERY));
		unitInHand.add(new Unit(this, UnitType.TANK));
		unitInHand.add(new Unit(this, UnitType.AIRBORNE));
		unitInHand.add(new Unit(this, UnitType.AIRBORNE));
		unitInHand.add(new Unit(this, UnitType.HELICOPTER));
		unitInHand.add(new Unit(this, UnitType.ARTILLERY));
	}

	public Set<Field> getValidTargetFields() {
		
		return game.getBoard().getFields();
	}

}
