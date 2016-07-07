package de.oglimmer.atmospheremvc.game;

import java.io.Serializable;

public interface Player extends Serializable {

	String getId();

	Object getSide();

	UIState getUiStates();

	void updateUI();

	Game getGame();

}
