package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.oglimmer.ggo.logic.phase.BasePhase;
import de.oglimmer.ggo.logic.phase.DraftPhase;
import de.oglimmer.ggo.logic.util.RandomName;
import lombok.Getter;
import lombok.Setter;

public class Game implements de.oglimmer.atmospheremvc.game.Game {

	public static final int TOTAL_TURNS = 5;

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

	@Getter
	@Setter
	private int turn;

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

	@Override
	public Player getOtherPlayer(de.oglimmer.atmospheremvc.game.Player currentPlayer) {
		return (Player) de.oglimmer.atmospheremvc.game.Game.super.getOtherPlayer(currentPlayer);
	}

}
