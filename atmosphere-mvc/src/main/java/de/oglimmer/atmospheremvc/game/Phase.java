package de.oglimmer.atmospheremvc.game;

public interface Phase {

	void execCmd(Player player, String cmd, String param);

	void updateMessages();

	void updateModalDialgs();

}
