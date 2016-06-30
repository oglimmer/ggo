package de.oglimmer.ggo.logic.phase;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.Collection;
import java.util.Collections;

import de.oglimmer.ggo.com.Constants;
import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.MessageQueue;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.logic.util.GameUtil;
import de.oglimmer.ggo.ui.UIButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract public class BasePhase {

	@Getter
	@NonNull
	private Game game;

	/**
	 * Executes a client command
	 * 
	 * @param player
	 * @param cmd
	 * @param param
	 * @param messages
	 */
	public void execCmd(Player player, String cmd, String param, MessageQueue messages) {
		switch (cmd) {
		case "join":
			player.resetUiState();
			messages.addMessage(player, Constants.RESP_MYCOLOR, instance.textNode(player.getSide().toString()));
			break;
		}
	}

	/**
	 * Initializes a phase. May call nextPhase.
	 */
	abstract public void init();

	/**
	 * Moves the game to the next phase
	 */
	abstract protected void nextPhase();

	final public void updateMessages(MessageQueue messages) {
		getGame().getPlayers().forEach(p -> updateMessage(p, messages));
	}

	/**
	 * Set on screen text message for a player
	 * 
	 * Must be idempotent
	 */
	abstract protected void updateMessage(Player player, MessageQueue messages);

	final public void updateModalDialgs(MessageQueue messages) {
		getGame().getPlayers().forEach(p -> updateModalDialg(p, messages));
		getGame().getPlayers().forEach(player -> {
			player.getUiStates().getMessagesState()
					.setScore("Your score: " + player.getScore() + ", opponents score: "
							+ GameUtil.getOtherPlayer(player).getScore() + " | Turn#" + getGame().getTurn() + " of "
							+ Game.TOTAL_TURNS);
		});
	}

	/**
	 * Set modal dialog for a player
	 * 
	 * Must be idempotent
	 * 
	 * @param player
	 * @param messages
	 */
	protected void updateModalDialg(Player player, MessageQueue messages) {
	}

	/**
	 * @param field
	 * @param player
	 * @return
	 */
	public boolean isHighlighted(Field field, Player player) {
		return false;
	}

	/**
	 * @param unit
	 * @param forPlayer
	 * @return
	 */
	public boolean isSelected(Unit unit, Player forPlayer) {
		return false;
	}

	/**
	 * @param field
	 * @param player
	 * @return
	 */
	public boolean isSelectable(Field field, Player player) {
		return false;
	}

	/**
	 * @param unit
	 * @param forPlayer
	 * @return
	 */
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return false;
	}

	/**
	 * @param forPlayer
	 * @return
	 */
	public Collection<UIButton> getButtons(Player forPlayer) {
		return Collections.emptyList();
	}

	/**
	 * Returns the command currently set on a unit seen by a player
	 * 
	 * @param unit
	 * @param forPlayer
	 * @return
	 */
	public Command getCommand(Unit unit, Player forPlayer) {
		return null;
	}

	/**
	 * @return
	 */
	public Boolean isShowCoordinates() {
		return false;
	}
}
