package de.oglimmer.atmospheremvc.game;

public interface Player {

	String getId();

	Object getSide();

	UIState getUiStates();

	void updateUI();

	Game getGame();

}
