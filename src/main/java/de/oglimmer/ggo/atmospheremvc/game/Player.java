package de.oglimmer.ggo.atmospheremvc.game;

import java.io.Serializable;
import java.util.Date;

public interface Player extends Serializable {

	String getId();

	Object getSide();

	UIState getUiStates();

	void updateUI();

	Game getGame();

	void setLastAction(Date now);

	void setLastConnection(Date now);

	Date getLastConnection();
}
