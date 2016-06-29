package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.com.AtmosphereResourceCache;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.util.GameUtil;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class UIConnectionStateProvider {

	private Player forPlayer;

	public boolean getOpponentConnectionStatus() {
		Player opponent = GameUtil.getOtherPlayer(forPlayer);
		if (opponent == null) {
			return false;
		}
		return AtmosphereResourceCache.INSTANCE.isConnected(opponent);
	}

}
