package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.oglimmer.ggo.logic.ai.AiStrategy;
import de.oglimmer.ggo.logic.phase.BasePhase;
import de.oglimmer.ggo.logic.phase.DraftPhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateBasePhase;
import de.oglimmer.utils.random.RandomName;
import lombok.Getter;
import lombok.Setter;

public class Game implements de.oglimmer.atmospheremvc.game.Game {

	private static final long serialVersionUID = 1L;

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

	/**
	 * @param nextPhase
	 * @return true if a new game logic phase was enabled (false if it was just
	 *         a Delegatable)
	 */
	public boolean setCurrentPhase(BasePhase nextPhase) {
		if (currentPhase instanceof TutorialDelegateBasePhase) {
			// currentPhase is a delegable
			TutorialDelegateBasePhase currentPhaseTutorial = (TutorialDelegateBasePhase) currentPhase;
			if (nextPhase instanceof TutorialDelegateBasePhase) {
				// nextPhase is a delegable as well => change the top-level
				// class and just safe the underlying delegate
				TutorialDelegateBasePhase nextPhaseTutorial = (TutorialDelegateBasePhase) nextPhase;
				nextPhaseTutorial.setDelegate(currentPhaseTutorial.getDelegate());
				currentPhase = nextPhase;
				return false;
			} else {
				// nextPhase isn't delegable => keep the top-level class and
				// replace the underlying delegate
				currentPhaseTutorial.setDelegate(nextPhase);
				return true;
			}

		} else {
			// currentPhase isn't a delegable
			if (nextPhase instanceof TutorialDelegateBasePhase) {
				// we switch to a top-level delegable, but we must keep the
				// underlying delegate
				TutorialDelegateBasePhase nextPhaseTutorial = (TutorialDelegateBasePhase) nextPhase;
				nextPhaseTutorial.setDelegate(currentPhase);
				currentPhase = nextPhase;
				return false;
			} else {
				currentPhase = nextPhase;
				return true;
			}
		}
	}

	public Player createPlayer() {
		return createPlayer(null);
	}

	public Player createPlayer(String email) {
		Side side = getUnusedSide();
		Player newPlayer = new Player(side, email, this);
		players.add(newPlayer);
		return newPlayer;
	}

	public void createAiPlayer(Class<? extends AiStrategy> clazz) {
		players.add(new PlayerAi(getUnusedSide(), this, clazz));
	}

	private Side getUnusedSide() {
		if (players.size() == 0) {
			return Side.GREEN;
		} else if (players.size() == 1) {
			return Side.RED;
		} else {
			throw new RuntimeException();
		}
	}

	@Override
	public Player getOtherPlayer(de.oglimmer.atmospheremvc.game.Player currentPlayer) {
		return (Player) de.oglimmer.atmospheremvc.game.Game.super.getOtherPlayer(currentPlayer);
	}

}
