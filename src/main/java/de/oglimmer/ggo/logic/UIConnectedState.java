package de.oglimmer.ggo.logic;

import de.oglimmer.ggo.com.AtmosphereResourceCache;
import de.oglimmer.ggo.logic.util.GameUtil;
import de.oglimmer.ggo.ui.DiffableBoolean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UIConnectedState {

	public UIConnectedState(Player player) {
		connectionStateOtherPlayer = DiffableBoolean.create(getConnectionStatus(player));
	}

	@Getter
	@Setter
	private DiffableBoolean connectionStateOtherPlayer;

	public boolean hasChange() {
		return connectionStateOtherPlayer != null;
	}

	public UIConnectedState calcStateAndDiff(Player player) {
		UIConnectedState diff = new UIConnectedState();
		boolean change = false;
		change |= connectionStateOtherPlayer.diffAndUpdate(getConnectionStatus(player),
				diff::setConnectionStateOtherPlayer);
		return change ? diff : null;
	}

	private boolean getConnectionStatus(Player otherPlayer) {
		Player player = GameUtil.getOtherPlayer(otherPlayer);
		if (player == null) {
			return false;
		}
		return AtmosphereResourceCache.INSTANCE.isConnected(player);
	}

}
