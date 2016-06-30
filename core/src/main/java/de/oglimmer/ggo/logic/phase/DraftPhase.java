package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.oglimmer.atmospheremvc.com.MessageQueue;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.UnitType;
import de.oglimmer.ggo.ui.UIButton;

public class DraftPhase extends BasePhase {

	private static final int CREDITS_PER_TURN = 1000;

	private Set<Player> inTurn = new HashSet<>();

	public DraftPhase(Game game) {
		super(game);
		game.setTurn(game.getTurn() + 1);
	}

	@Override
	public void init() {
		inTurn.addAll(getGame().getPlayers());
		getGame().getPlayers().forEach(p -> p.incCredits(CREDITS_PER_TURN));
		getGame().getPlayers().forEach(p -> p.getUiStates().getMessagesState().clearErrorInfo());
	}

	@Override
	public void execCmd(Player player, String cmd, String param, MessageQueue messages) {
		super.execCmd(player, cmd, param, messages);
		switch (cmd) {
		case "selectHandCard":
			execSelectHandCard(player, param);
			break;
		case "button":
			if ("doneButton".equals(param)) {
				playerDone(player);
			}
			for (UnitType type : UnitType.values()) {
				if (("buy" + type.toString()).equals(param)) {
					if (type.getCost() <= player.getCredits()) {
						player.spendCredits(type.getCost());
						player.getUnitInHand().add(new Unit(player, type));
					}
				}
			}
			break;
		}

	}

	private void execSelectHandCard(Player player, String param) {
		if (!inTurn.contains(player)) {
			return;
		}
		Unit paramSelectedUnit = player.getUnitInHand().stream().filter(u -> u.getId().equals(param)).findFirst().get();
		player.incCredits(paramSelectedUnit.getUnitType().getCost());
		player.getUnitInHand().remove(paramSelectedUnit);
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return unit.getPlayer() == forPlayer && forPlayer.getUnitInHand().contains(unit) && inTurn.contains(forPlayer);
	}

	private void playerDone(Player player) {
		inTurn.remove(player);
		if (inTurn.isEmpty()) {
			nextPhase();
		}
	}

	@Override
	protected void nextPhase() {
		getGame().setCurrentPhase(new DeployPhase(getGame()));
		getGame().getCurrentPhase().init();
	}

	@Override
	protected void updateMessage(Player player, MessageQueue messages) {
		if (inTurn.contains(player)) {
			player.getMessages().setTitle(
					"Draft units by clicking one at the bottom. To revert click the unit in your hand. Click 'done' to finish the draft phase. The player with more money left will start the next phase.");
			player.getMessages().setInfo("You have " + player.getCredits() + " credits.");
		} else {
			player.getMessages().setTitle("Wait for your opponent to finish the draft phase.");
			player.getMessages().setInfo("You have " + player.getCredits() + " credits left for next round.");
		}
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		if (inTurn.contains(forPlayer)) {
			buttons.add(new UIButton("doneButton", "Done", null, 30, 20, !inTurn.contains(forPlayer)));

			for (UnitType type : UnitType.values()) {
				buttons.add(new UIButton("buy" + type.toString(), Integer.toString(type.getCost()), type.toString(), 48,
						48, forPlayer.getCredits() < type.getCost()));
			}
		}
		return buttons;
	}
}
