package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.com.AtmosphereResourceCache;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.util.GameUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UIConnectedState {

	public UIConnectedState(Player forPlayer) {
		connectionStateOtherPlayer = DiffableBoolean.create(null);
	}

	@Getter
	@Setter
	private DiffableBoolean connectionStateOtherPlayer;

	public boolean hasChange() {
		return connectionStateOtherPlayer != null;
	}

	public UIConnectedState calcStateAndDiff(Player forPlayer) {
		UIConnectedState diff = new UIConnectedState();
		boolean change = false;
		change |= connectionStateOtherPlayer.diffAndUpdate(getConnectionStatus(forPlayer),
				diff::setConnectionStateOtherPlayer);
		return change ? diff : null;
	}

	private boolean getConnectionStatus(Player forPlayer) {
		Player opponent = GameUtil.getOtherPlayer(forPlayer);
		if (opponent == null) {
			return false;
		}
		return AtmosphereResourceCache.INSTANCE.isConnected(opponent);
	}

}
