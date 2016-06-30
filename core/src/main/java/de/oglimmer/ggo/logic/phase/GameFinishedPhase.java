package de.oglimmer.ggo.logic.phase;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.atmospheremvc.com.Constants;
import de.oglimmer.atmospheremvc.com.MessageQueue;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;

public class GameFinishedPhase extends BasePhase {

	public GameFinishedPhase(Game game) {
		super(game);
	}

	@Override
	public void init() {
	}

	@Override
	protected void nextPhase() {
	}

	@Override
	protected void updateMessage(Player player, MessageQueue messages) {
		player.getMessages().setTitle("Game Over.");
	}

	@Override
	protected void updateModalDialg(Player player, MessageQueue messages) {
		ObjectNode root = instance.objectNode();
		String winningInfo;
		if (player.getScore() > getGame().getOtherPlayer(player).getScore()) {
			winningInfo = "You win!";
		} else if (player.getScore() < getGame().getOtherPlayer(player).getScore()) {
			winningInfo = "The opponent wins!";
		} else {
			winningInfo = "It's a tie!";
		}
		root.set("title", instance.textNode("GAME OVER! " + winningInfo));
		root.set("options", instance.arrayNode());
		messages.addMessage(player, Constants.RESP_MODAL_DIALOG_EN, root);
	}

}
