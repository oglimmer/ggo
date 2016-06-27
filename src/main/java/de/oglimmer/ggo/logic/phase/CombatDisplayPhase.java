package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.MessageQueue;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.CombatPhaseRoundCounter;
import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.logic.battle.CommandCenter;
import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatDisplayPhase extends BasePhase {

	private CombatPhaseRoundCounter combatPhaseRoundCounter;
	private CommandCenter ccDryRun;
	private CommandCenter cc;
	private Set<Player> inTurn = new HashSet<>();
	private Map<Player, StringBuilder> infoMessages;

	public CombatDisplayPhase(Game game, CombatPhaseRoundCounter combatPhaseRoundCounter, CommandCenter cc) {
		super(game);
		this.combatPhaseRoundCounter = combatPhaseRoundCounter;
		this.cc = cc;
		this.ccDryRun = new CommandCenter(cc, true);
	}

	@Override
	public void init() {
		inTurn.addAll(getGame().getPlayers());
		getGame().getPlayers().forEach(p -> p.getUiStates().getClientMessages().clearErrorInfo());
		ccDryRun.calcBattle();
		infoMessages = ccDryRun.getInfoMessages();
	}

	@Override
	public void execCmd(Player player, String cmd, String param, MessageQueue messages) {
		super.execCmd(player, cmd, param, messages);
		switch (cmd) {
		case "button":
			if ("doneButton".equals(param)) {
				execDoneButton(player);
			}
			break;
		}
	}

	private void execDoneButton(Player player) {
		inTurn.remove(player);
		if (inTurn.isEmpty()) {
			nextPhase();
		}
	}

	@Override
	protected void updateMessage(Player player, MessageQueue messages) {
		String title;
		if (inTurn.contains(player)) {
			title = "Check your and the opponents commands. Press `done` when finished. Round "
					+ combatPhaseRoundCounter.getCurrentRound() + " of " + combatPhaseRoundCounter.getMaxRounds();
		} else {
			title = "Wait for your opponent to finish the turn. Round " + combatPhaseRoundCounter.getCurrentRound()
					+ " of " + combatPhaseRoundCounter.getMaxRounds();
		}
		player.getUiStates().getClientMessages().setTitle(title);

		getGame().getPlayers().forEach(p -> p.getUiStates().getClientMessages()
				.setInfo(infoMessages.getOrDefault(p, new StringBuilder()).toString()));

	}

	@Override
	protected void nextPhase() {
		cc.calcBattle();
		combatPhaseRoundCounter.incRound();
		if (combatPhaseRoundCounter.lastRoundReached() || getGame().getBoard().getTotalUnits() == 0) {
			getGame().getBoard().getFields().stream().filter(f -> f.getStructure() != null)
					.filter(f -> f.getUnit() != null)
					.filter(f -> f.getUnit().getPlayer() != f.getStructure().getPlayer()).forEach(f -> {
						f.getUnit().getPlayer().incScore(25);
						log.debug("Player {} scores 25 points for owning a city.", f.getUnit().getPlayer().getSide());
					});

			if (getGame().getTurn() < Game.TOTAL_TURNS) {
				getGame().setCurrentPhase(new DraftPhase(getGame()));
				getGame().getCurrentPhase().init();
			} else {
				getGame().setCurrentPhase(new GameFinishedPhase(getGame()));
				getGame().getCurrentPhase().init();
			}
		} else {
			getGame().setCurrentPhase(new CombatCommandPhase(getGame(), combatPhaseRoundCounter));
			getGame().getCurrentPhase().init();

		}
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		buttons.add(
				new UIButton("doneButton", "Done", null, 30, 20, DiffableBoolean.create(!inTurn.contains(forPlayer))));
		return buttons;
	}

	@Override
	public Command getCommand(Unit unit, Player forPlayer) {
		return cc.getByUnit(unit);
	}

	@Override
	public Boolean isShowCoordinates() {
		return true;
	}
}
