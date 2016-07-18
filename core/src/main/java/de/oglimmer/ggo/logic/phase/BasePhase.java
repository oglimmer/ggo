package de.oglimmer.ggo.logic.phase;

import java.util.Collection;
import java.util.Collections;

import de.oglimmer.atmospheremvc.com.AtmosphereResourceCache;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.ui.shortlife.UIButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract public class BasePhase implements de.oglimmer.atmospheremvc.game.Phase {

	private static final long serialVersionUID = 1L;

	@Getter
	@NonNull
	private Game game;

	@Override
	public void execCmd(de.oglimmer.atmospheremvc.game.Player player, String cmd, String param) {
		execCmd((Player) player, cmd, param);
	}

	/**
	 * Executes a client command
	 * 
	 * @param player
	 * @param cmd
	 * @param param
	 * @param messages
	 */
	public void execCmd(Player player, String cmd, String param) {
		switch (cmd) {
		case "join":
			player.resetUiState();
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

	protected void notifyPlayers() {
		getGame().getPlayers().forEach(this::notifyPlayer);
	}

	protected void notifyPlayer(Player p) {
		AtmosphereResourceCache.Item item = AtmosphereResourceCache.INSTANCE.getItem(p);
		if (item == null || item.isDisconnected()) {
			EmailService.EMAIL.gameNeedsYourAction(p);
		}
	}

	@Override
	final public void updateMessages() {
		updateScoreMessages();
		updateInfoMessages();
		updateTitleMessages();
	}

	protected void updateTitleMessages() {
		getGame().getPlayers().forEach(p -> updateTitleMessage(p));
	}

	protected void updateInfoMessages() {
		getGame().getPlayers().forEach(p -> updateInfoMessage(p));
	}

	protected void updateScoreMessages() {
		getGame().getPlayers().forEach(player -> {
			player.getMessages()
					.setScore("Your score: " + player.getScore() + ", opponents score: "
							+ getGame().getOtherPlayer(player).getScore() + " | Turn#" + getGame().getTurn() + " of "
							+ Game.TOTAL_TURNS);
		});
	}

	/**
	 * Set on screen title message for a player
	 * 
	 * Must be idempotent
	 */
	abstract protected void updateTitleMessage(Player player);

	/**
	 * Set on screen info message for a player
	 * 
	 * Must be idempotent
	 */
	protected void updateInfoMessage(Player player) {
	}

	@Override
	final public void updateModalDialgs() {
		getGame().getPlayers().forEach(p -> updateModalDialg(p));
	}

	/**
	 * Set modal dialog for a player
	 * 
	 * Must be idempotent
	 * 
	 * @param player
	 * @param messages
	 */
	protected void updateModalDialg(Player player) {
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
