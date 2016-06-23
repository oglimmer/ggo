package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.MessageQueue;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.UnitType;
import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;

public class DraftPhase extends BasePhase {

	private static final int CREDITS_PER_TURN = 1000;

	private Set<Player> inTurn = new HashSet<>();

	public DraftPhase(Game game) {
		super(game);
	}

	@Override
	public void init(Player firstPlayer) {
		inTurn.addAll(getGame().getPlayers());
		getGame().getPlayers().forEach(p -> p.incCredits(CREDITS_PER_TURN));
		getGame().getPlayers().forEach(p -> p.getUiStates().getClientMessages().clearErrorInfo());
	}

	@Override
	public void execCmd(Player player, String cmd, String param, MessageQueue messages) {
		super.execCmd(player, cmd, param, messages);
		switch (cmd) {
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
					if (player.getCredits() < getCheapestUnit()) {
						playerDone(player);
					}
				}
			}
			break;
		}

	}

	private void playerDone(Player player) {
		inTurn.remove(player);
		if (inTurn.isEmpty()) {
			nextPhase(getGame().getPlayers().get(0));
		}
	}

	private int getCheapestUnit() {
		int minCost = Integer.MAX_VALUE;
		for (UnitType ut : UnitType.values()) {
			if (ut.getCost() < minCost) {
				minCost = ut.getCost();
			}
		}
		return minCost;
	}

	@Override
	protected void nextPhase(Player firstPlayer) {
		getGame().setCurrentPhase(new DeployPhase(firstPlayer));
		getGame().getCurrentPhase().init(firstPlayer);
	}

	@Override
	protected void updateMessage(Player player, MessageQueue messages) {
		if (inTurn.contains(player)) {
			player.getUiStates().getClientMessages().setTitle("Draft your units until all your credits are spent.");
			player.getUiStates().getClientMessages().setInfo("You have " + player.getCredits() + " credits.");
		} else {
			player.getUiStates().getClientMessages().setTitle("Wait for your opponent to finish the draft phase.");
			player.getUiStates().getClientMessages().setInfo("You have " + player.getCredits() + " credits left for next round.");
		}
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		if (inTurn.contains(forPlayer)) {
			buttons.add(new UIButton("doneButton", "Done", null, 30, 20,
					DiffableBoolean.create(!inTurn.contains(forPlayer))));

			for (UnitType type : UnitType.values()) {
				buttons.add(new UIButton("buy" + type.toString(), null, type.toString(), 48, 48,
						DiffableBoolean.create(forPlayer.getCredits() < type.getCost())));
			}
		}
		return buttons;
	}
}
