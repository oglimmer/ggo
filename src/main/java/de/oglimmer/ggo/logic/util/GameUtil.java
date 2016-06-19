package de.oglimmer.ggo.logic.util;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;

public class GameUtil {

	public static Player getOtherPlayer(Player currentPlayer) {
		Game game = currentPlayer.getGame();
		return game.getPlayers().get(0) == currentPlayer ? game.getPlayers().get(1) : game.getPlayers().get(0);
	}

}
