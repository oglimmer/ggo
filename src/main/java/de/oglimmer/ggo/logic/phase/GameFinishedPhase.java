package de.oglimmer.ggo.logic.phase;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;

public class GameFinishedPhase extends BasePhase {

	private static final long serialVersionUID = 1L;

	public GameFinishedPhase(Game game) {
		super(game);
	}

	@Override
	public void init() {
		notifyPlayers();
	}

	@Override
	protected void nextPhase() {
	}

	@Override
	protected void updateTitleMessage(Player player) {
		player.getMessages().setTitle("Game Over.");
	}

	@Override
	protected void updateModalDialg(Player player) {
		String winningInfo;
		if (player.getScore() > getGame().getOtherPlayer(player).getScore()) {
			winningInfo = "You win!";
		} else if (player.getScore() < getGame().getOtherPlayer(player).getScore()) {
			winningInfo = "The opponent wins!";
		} else {
			winningInfo = "It's a tie!";
		}
		player.getModalDialog().setTitle("GAME OVER! " + winningInfo);
		player.getModalDialog().setShow(true);
	}

}
