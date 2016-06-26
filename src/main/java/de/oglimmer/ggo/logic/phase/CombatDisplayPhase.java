package de.oglimmer.ggo.logic.phase;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.com.Constants;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.MessageQueue;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.BattleGroundResolver;
import de.oglimmer.ggo.logic.battle.BombarbResolver;
import de.oglimmer.ggo.logic.battle.CombatPhaseRoundCounter;
import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.logic.battle.CommandCenter;
import de.oglimmer.ggo.logic.battle.CrossingBattleResolver;
import de.oglimmer.ggo.logic.battle.MoveResolver;
import de.oglimmer.ggo.logic.util.GameUtil;
import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatDisplayPhase extends BasePhase {

	private CombatPhaseRoundCounter combatPhaseRoundCounter;
	private CommandCenter cc;
	private Set<Player> inTurn = new HashSet<>();

	public CombatDisplayPhase(Game game, CombatPhaseRoundCounter combatPhaseRoundCounter, CommandCenter cc) {
		super(game);
		this.combatPhaseRoundCounter = combatPhaseRoundCounter;
		this.cc = cc;
	}

	@Override
	public void init() {
		if (getGame().getBoard().getTotalUnits() == 0) {
			nextPhase();
		} else {
			inTurn.addAll(getGame().getPlayers());
			getGame().getPlayers().forEach(p -> p.getUiStates().getClientMessages().clearErrorInfo());
		}
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
	}

	@Override
	protected void updateModalDialg(Player player, MessageQueue messages) {
		if (getGame().getTurn() >= Game.TOTAL_TURNS) {
			ObjectNode root = instance.objectNode();
			String winningInfo;
			if (player.getScore() > GameUtil.getOtherPlayer(player).getScore()) {
				winningInfo = "You win!";
			} else if (player.getScore() < GameUtil.getOtherPlayer(player).getScore()) {
				winningInfo = "The opponent wins!";
			} else {
				winningInfo = "It's a tie!";
			}
			root.set("title", instance.textNode("GAME OVER! " + winningInfo));
			root.set("options", instance.arrayNode());
			messages.addMessage(player, Constants.RESP_MODAL_DIALOG_EN, root);
		}
	}

	@Override
	protected void nextPhase() {
		calcBattle();
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

	private void calcBattle() {
		BombarbResolver bomb = new BombarbResolver(cc);
		bomb.collectTargets();

		CrossingBattleResolver br = new CrossingBattleResolver(cc);
		br.battleCrossingUnits();

		BattleGroundResolver bgr = new BattleGroundResolver(cc);
		bgr.battleBattleGrounds();

		bomb.killTargets();

		MoveResolver mr = new MoveResolver(cc);
		mr.moveUnits();
	}
}
