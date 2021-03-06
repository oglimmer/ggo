package de.oglimmer.ggo.logic.phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.CombatPhaseRoundCounter;
import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.logic.battle.CommandCenter;
import de.oglimmer.ggo.ui.shortlife.UIButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatDisplayPhase extends BasePhase {

	private static final long serialVersionUID = 1L;

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
		notifyPlayers();
		getGame().getPlayers().forEach(p -> p.getMessages().clearErrorInfo());
		ccDryRun.calcBattle();
		infoMessages = ccDryRun.getInfoMessages();
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "button":
			if ("doneButton".equals(param)) {
				execDoneButton(player);
			}
			break;
		}
	}

	public void execDoneButton(Player player) {
		inTurn.remove(player);
		if (inTurn.isEmpty()) {
			nextPhase();
		}
	}

	@Override
	protected void updateTitleMessage(Player player) {
		String title;
		if (inTurn.contains(player)) {
			title = "Check your and the opponents commands. Press `done` when finished. Round "
					+ combatPhaseRoundCounter.getCurrentRound() + " of " + combatPhaseRoundCounter.getMaxRounds();
		} else {
			title = "Wait for your opponent to finish the turn. Round " + combatPhaseRoundCounter.getCurrentRound()
					+ " of " + combatPhaseRoundCounter.getMaxRounds();
		}
		player.getMessages().setTitle(title);
	}

	@Override
	protected void updateInfoMessage(Player player) {
		getGame().getPlayers()
				.forEach(p -> p.getMessages().setInfo(infoMessages.getOrDefault(p, new StringBuilder()).toString()));
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
				boolean initShouldBeCalled = getGame().setCurrentPhase(new DraftPhase(getGame()));
				assert initShouldBeCalled;
				getGame().getCurrentPhase().init();
			} else {
				boolean initShouldBeCalled = getGame().setCurrentPhase(new GameFinishedPhase(getGame()));
				assert initShouldBeCalled;
				getGame().getCurrentPhase().init();
			}
		} else {
			boolean initShouldBeCalled = getGame().setCurrentPhase(new CombatCommandPhase(getGame(), combatPhaseRoundCounter));
			assert initShouldBeCalled;
			getGame().getCurrentPhase().init();

		}
	}

	@Override
	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		buttons.add(UIButton.builder().id("doneButton").text("Done").width(30).height(20)
				.hidden(!inTurn.contains(forPlayer)).build());
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

	@Override
	public String toString() {
		return "CombatDisplayPhase [inTurn="
				+ inTurn.stream().map(p -> p.getSide().toString()).collect(Collectors.joining(",")) + "]";
	}
}
