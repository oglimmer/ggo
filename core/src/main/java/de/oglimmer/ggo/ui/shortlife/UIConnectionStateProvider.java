package de.oglimmer.ggo.ui.shortlife;

import java.io.Serializable;

import de.oglimmer.atmospheremvc.com.AtmosphereResourceCache;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.PlayerAi;
import de.oglimmer.ggo.logic.phase.TutorialDelegateBasePhase;
import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Transforms the server-side model into client-side model
 */
@ToString
@AllArgsConstructor
public class UIConnectionStateProvider implements Serializable {

	private static final long serialVersionUID = 1L;

	private Player forPlayer;

	public String getJsClass() {
		return "OpponentConnectionState";
	};

	public boolean getOpponentConnectionStatus() {
		Player opponent = forPlayer.getGame().getOtherPlayer(forPlayer);
		if (opponent == null) {
			return false;
		}
		return AtmosphereResourceCache.INSTANCE.isConnected(opponent);
	}

	public boolean getOpponentConnectionStatusIgnore() {
		return forPlayer.getGame().getCurrentPhase() instanceof TutorialDelegateBasePhase
				|| forPlayer.getGame().getOtherPlayer(forPlayer) instanceof PlayerAi;
	}

}
