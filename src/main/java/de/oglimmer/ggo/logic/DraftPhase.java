package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;

public class DraftPhase extends BasePhase {

	private Set<Player> inTurn = new HashSet<>();

	public DraftPhase(Game game) {
		super(game);
		inTurn.addAll(game.getPlayers());
		game.getPlayers().forEach(p -> p.incCredits(1000));
		game.getPlayers().forEach(p -> p.getClientMessages().clearErrorInfo());
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
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

	@Override
	protected void nextPhase(Player firstPlayer) {
		getGame().setCurrentPhase(new DeployPhase(firstPlayer));
	}

	@Override
	public void updateUI() {
		getGame().getPlayers().forEach(player -> {
			player.getClientMessages().setTitle("Draft your units until all your credits are spent.");
			player.getClientMessages().setInfo("You have " + player.getCredits() + " credits.");
		});
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		buttons.add(
				new UIButton("doneButton", "Done", null, 30, 20, DiffableBoolean.create(!inTurn.contains(forPlayer))));

		for (UnitType type : UnitType.values()) {
			buttons.add(new UIButton("buy" + type.toString(), null, type.toString(), 48, 48,
					DiffableBoolean.create(forPlayer.getCredits() < type.getCost())));
		}

		return buttons;
	}
}
