package de.oglimmer.atmospheremvc.game;

import de.oglimmer.atmospheremvc.com.MessageQueue;

public interface Phase {

	void execCmd(Player player, String cmd, String param, MessageQueue messages);

	void updateMessages();

	void updateModalDialgs();

}
