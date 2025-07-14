package de.oglimmer.ggo.websocket.game;

import java.io.Serializable;
import java.util.List;

public interface Game extends Serializable {

	String getId();

	List<? extends Player> getPlayers();

	Player getPlayerById(String pid);

	Phase getCurrentPhase();

	default Player getOtherPlayer(Player currentPlayer) {
		if (getPlayers().size() != 2) {
			return null;
		}
		return getPlayers().get(0) == currentPlayer ? getPlayers().get(1) : getPlayers().get(0);
	}
}
