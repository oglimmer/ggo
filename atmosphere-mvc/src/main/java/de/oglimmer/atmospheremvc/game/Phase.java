package de.oglimmer.atmospheremvc.game;

import java.io.Serializable;

public interface Phase extends Serializable {

	void execCmd(Player player, String cmd, String param);

	void updateMessages();

	void updateModalDialgs();

}
