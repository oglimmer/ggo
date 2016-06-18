package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Game {

	@Getter
	private String id = "0";

	@Getter
	private List<Player> players = new ArrayList<>();

	@Getter
	private BasePhase currentPhase;

	@Getter
	private Board board;

	public Game() {
		Player player1 = new Player("p1", Side.GREEN, this);
		players.add(player1);
		Player player2 = new Player("p2", Side.RED, this);
		players.add(player2);
		board = new Board();
		board.addCities(players);
		currentPhase = new DraftPhase(this);
	}

	public Player getPlayerById(String pid) {
		return players.get(0).getId().equals(pid) ? players.get(0) : players.get(1);
	}

	public Unit getUnitById(String unitId) {
		return board.getFields().stream().filter(f -> f.getUnit() != null)
				.filter(f -> f.getUnit().getId().equals(unitId)).map(f -> f.getUnit()).findFirst().get();
	}

	public void setCurrentPhase(BasePhase nextPhase) {
		currentPhase = nextPhase;
	}

}
