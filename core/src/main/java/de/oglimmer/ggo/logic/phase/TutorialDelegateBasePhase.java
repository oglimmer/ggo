package de.oglimmer.ggo.logic.phase;

import java.util.Collection;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.ui.shortlife.UIButton;
import lombok.Getter;
import lombok.Setter;

abstract public class TutorialDelegateBasePhase extends BasePhase {

	private static final long serialVersionUID = 1L;

	@Getter
	protected BasePhase delegate;

	public void setDelegate(BasePhase delegate) {
		assert delegate != null;
		assert !(delegate instanceof TutorialDelegateBasePhase) : "Illegal class " + delegate.getClass().getName();
		this.delegate = delegate;
	}

	@Setter
	private String title;

	@Setter
	private TutorialDelegateBasePhase nextPhase;

	public TutorialDelegateBasePhase(Game game) {
		super(game);
	}

	@Override
	public void init() {
		delegate.init();
	}

	public void initTutorialStep() {
	}

	@Override
	protected void nextPhase() {
		if (getGame().setCurrentPhase(nextPhase)) {
			getGame().getCurrentPhase().init();
		}
		((TutorialDelegateBasePhase) getGame().getCurrentPhase()).initTutorialStep();
	}

	@Override
	protected void updateTitleMessage(Player player) {
		player.getMessages().setTitle(title);
	}

	@Override
	protected void updateInfoMessage(Player player) {
		delegate.updateInfoMessage(player);
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		delegate.execCmd(player, cmd, param);
	}

	@Override
	public boolean isHighlighted(Field field, Player player) {
		return delegate.isHighlighted(field, player);
	}

	@Override
	public boolean isSelected(Unit unit, Player forPlayer) {
		return delegate.isSelected(unit, forPlayer);
	}

	@Override
	public boolean isSelectable(Field field, Player player) {
		return delegate.isSelectable(field, player);
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return delegate.isSelectable(unit, forPlayer);
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		return delegate.getButtons(forPlayer);
	}

	@Override
	public Command getCommand(Unit unit, Player forPlayer) {
		return delegate.getCommand(unit, forPlayer);
	}

	@Override
	public Boolean isShowCoordinates() {
		return delegate.isShowCoordinates();
	}

	@Override
	protected void notifyPlayers() {
		delegate.notifyPlayers();
	}

	@Override
	protected void notifyPlayer(Player p) {
		delegate.notifyPlayer(p);
	}

	@Override
	protected void updateScoreMessages() {
		delegate.updateScoreMessages();
	}

	@Override
	protected void updateModalDialg(Player player) {
		delegate.updateModalDialg(player);
	}

	public String toString(int lvl) {
		return "TutorialDelegateBasePhase [delegate=" + delegate + ", title=\""
				+ (title != null ? (title.length() > 20 ? title.substring(0, 20) + "…" : title) : "null")
				+ "\", nextPhase=" + (nextPhase != null ? nextPhase.toString(lvl + 1) : "null") + "]";
	}

}
