package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.oglimmer.ggo.logic.phase.BasePhase;
import de.oglimmer.ggo.logic.phase.DraftPhase;
import de.oglimmer.ggo.util.RandomName;
import lombok.Getter;

public class Game {

	@Getter
	private String id = RandomName.getName(4);

	@Getter
	private Date createdOn = new Date();

	@Getter
	private List<Player> players = new ArrayList<>();

	@Getter
	private BasePhase currentPhase;

	@Getter
	private Board board;

	public Game() {
		board = new Board();
		currentPhase = new DraftPhase(this);
	}

	public void startGame() {
		currentPhase.init();
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

	public Player createPlayer() {
		Side side;
		if (players.size() == 0) {
			side = Side.GREEN;
		} else if (players.size() == 1) {
			side = Side.RED;
		} else {
			throw new RuntimeException();
		}
		Player newPlayer = new Player(side, this);
		players.add(newPlayer);
		return newPlayer;
	}

}
