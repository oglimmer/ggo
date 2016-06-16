package de.oglimmer.ggo.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.oglimmer.ggo.logic.util.GameUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeployPhase extends BasePhase {

	private Player activePlayer;

	private Unit selectedUnit;

	private Map<Player, Set<Field>> validTargetFields = new HashMap<>();

	public DeployPhase(Player firstActivePlayer) {
		this.activePlayer = firstActivePlayer;
		firstActivePlayer.getGame().getPlayers().forEach(p -> validTargetFields.put(p, p.getValidTargetFields()));
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		return forPlayer == activePlayer && selectedUnit != null && validTargetFields.get(forPlayer).contains(field);
	}

	@Override
	public boolean isSelectable(Field field, Player forPlayer) {
		return isHighlighted(field, forPlayer);
	}

	@Override
	public boolean isSelected(Unit unit, Player forPlayer) {
		return forPlayer == activePlayer && selectedUnit == unit;
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return forPlayer == activePlayer && forPlayer.getUnitInHand().contains(unit)
				&& (selectedUnit == null || selectedUnit == unit);
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "selectHandCard":
			execSelectHandCard(player, param);
			break;
		case "selectTargetField":
			execSelectTargetField(player, param);
			break;
		}
	}

	private void execSelectHandCard(Player player, String param) {
		if (player != activePlayer) {
			log.error("got cmd selectHandCard from not active player");
			return;
		}
		Unit paramSelectedUnit = player.getUnitInHand().stream().filter(u -> u.getId().equals(param)).findFirst().get();
		if (selectedUnit != null && selectedUnit != paramSelectedUnit) {
			log.error("execSelectHandCard but selectedUnit was " + selectedUnit.getType());
			return;
		}
		if (selectedUnit == paramSelectedUnit) {
			selectedUnit = null;
		} else {
			selectedUnit = paramSelectedUnit;
		}
	}

	private void execSelectTargetField(Player player, String param) {
		if (player != activePlayer) {
			log.error("got cmd selectHandCard from not active player");
			return;
		}
		if (selectedUnit == null) {
			log.error("execSelectTargetField but selectedUnit was null");
			return;
		}
		Field target = player.getGame().getBoard().getField(param);
		if (target.getUnit() != null) {
			log.error("execSelectTargetField but already occupied field was seleted");
			return;
		}
		target.setUnit(selectedUnit);
		selectedUnit.setDeployedOn(target);
		player.getUnitInHand().remove(selectedUnit);
		this.validTargetFields.values().forEach(s -> s.remove(target));
		selectedUnit = null;
		switchPlayer(player);

	}

	protected void switchPlayer(Player player) {
		boolean nextPhase = false;
		activePlayer.getClientMessages().clearError();
		Player nextPlayer = GameUtil.getOtherPlayer(player.getGame(), activePlayer);
		if (!hasMoreMoves(nextPlayer)) {
			if (hasMoreMoves(activePlayer)) {
				nextPlayer = activePlayer;
			} else {
				nextPhase = true;
			}
		}
		if (nextPhase) {
			nextPhase(nextPlayer);
		} else {
			activePlayer = nextPlayer;
			updateUI(activePlayer.getGame());
		}
	}

	@Override
	public void updateUI(Game game) {
		game.getPlayers().forEach(player -> {
			if (player == activePlayer) {
				if (selectedUnit != null) {
					player.getClientMessages().setTitle("Select a highlighted field to deploy " + selectedUnit.getType()
							+ " or click the unit again to de-select it");
				} else {
					player.getClientMessages().setTitle("Select a unit from your hand to deploy it");
				}
			} else {
				player.getClientMessages().setTitle("waiting for other player's deployment");
			}
		});
	}

	@Override
	protected void nextPhase(Player firstPlayer) {
		firstPlayer.getGame().setCurrentPhase(new CombatPhase());
	}

	private boolean hasMoreMoves(Player p) {
		return !p.getUnitInHand().isEmpty();
	}

}
