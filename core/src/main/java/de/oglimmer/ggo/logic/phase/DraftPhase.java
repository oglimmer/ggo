package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.UnitType;
import de.oglimmer.ggo.ui.shortlife.UIButton;
import lombok.AccessLevel;
import lombok.Getter;

public class DraftPhase extends BasePhase {

	private static final long serialVersionUID = 1L;

	public static final int CREDITS_PER_TURN = 1000;

	@Getter(value = AccessLevel.PROTECTED)
	private Set<Player> inTurn = new HashSet<>();

	public DraftPhase(Game game) {
		super(game);
		game.setTurn(game.getTurn() + 1);
	}

	@Override
	public void init() {
		inTurn.addAll(getGame().getPlayers());
		notifyPlayers();
		getGame().getPlayers().forEach(p -> p.incCredits(CREDITS_PER_TURN));
		getGame().getPlayers().forEach(p -> p.getMessages().clearErrorInfo());
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
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
						draftUnit(player, type);
					}
				}
			}
			break;
		}
	}

	public void draftUnit(Player player, UnitType type) {
		assert player.getCredits() >= type.getCost();
		player.spendCredits(type.getCost());
		player.getUnitInHand().add(new Unit(player, type));
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

	public void playerDone(Player player) {
		inTurn.remove(player);
		if (inTurn.isEmpty()) {
			nextPhase();
		}
	}

	@Override
	protected void nextPhase() {
		boolean initShouldBeCalled = getGame().setCurrentPhase(new DeployPhase(getGame()));
		assert initShouldBeCalled;
		getGame().getCurrentPhase().init();
	}

	@Override
	protected void updateTitleMessage(Player player) {
		if (inTurn.contains(player)) {
			player.getMessages()
					.setTitle("Draft units by clicking one at the bottom. To revert click the unit in your hand."
							+ " Click 'done' to finish the draft phase. The player with more money left will start the next phase.");
		} else {
			player.getMessages().setTitle("Wait for your opponent to finish the draft phase.");
		}
	}

	@Override
	protected void updateInfoMessage(Player player) {
		if (inTurn.contains(player)) {
			player.getMessages().setInfo("You have " + player.getCredits() + " credits.");
		} else {
			player.getMessages().setInfo("You have " + player.getCredits() + " credits left for next round.");
		}
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		if (inTurn.contains(forPlayer)) {
			buttons.add(UIButton.builder().id("doneButton").text("Done").width(30).height(20)
					.hidden(!inTurn.contains(forPlayer)).build());
			for (UnitType type : UnitType.values()) {
				buttons.add(UIButton.builder().id("buy" + type.toString()).text(Integer.toString(type.getCost()))
						.graphic(type.toString()).width(48).height(48).hidden(forPlayer.getCredits() < type.getCost())
						.build());
			}
		}
		return buttons;
	}

	@Override
	public String toString() {
		return "DraftPhase [inTurn=" + inTurn.stream().map(p -> p.getSide().toString()).collect(Collectors.joining(","))
				+ "]";
	}
}
