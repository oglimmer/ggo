package de.oglimmer.ggo.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.oglimmer.ggo.logic.util.GameUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeployPhase extends BasePhase {

	private Player activePlayer;

	private Unit selectedUnit;

	private Map<Player, Set<Field>> additionalAirborneTargetFields = new HashMap<>();

	public DeployPhase(Player firstActivePlayer) {
		super(firstActivePlayer.getGame());
		this.activePlayer = firstActivePlayer;
		getGame().getPlayers().forEach(p -> p.getClientMessages().clearErrorInfo());
		getGame().getPlayers()
				.forEach(p -> additionalAirborneTargetFields.put(p, calcAdditionalTargetFieldsAirborne(p)));
	}

	private Set<Field> getDefaultDeployZone(Player player) {
		Predicate<? super Field> filter;
		if (player.getSide() == Side.GREEN) {
			filter = f -> f.getPos().getX() <= 3;
		} else {
			filter = f -> f.getPos().getX() >= 6;
		}
		return getGame().getBoard().getFields().stream().filter(filter).collect(Collectors.toSet());
	}

	private Set<Field> calcAdditionalTargetFieldsAirborne(Player player) {
		return getGame().getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.filter(f -> f.getUnit().getPlayer() == player).flatMap(f -> f.getNeighbords().stream())
				.collect(Collectors.toSet());
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		Set<Field> tmp = new HashSet<>(getDefaultDeployZone(forPlayer));
		if (selectedUnit != null && selectedUnit.getUnitType() == UnitType.AIRBORNE) {
			tmp.addAll(additionalAirborneTargetFields.get(forPlayer));
		}
		// remove fields with unit
		getGame().getBoard().getFields().stream().filter(f -> f.getUnit() != null).forEach(f -> tmp.remove(f));
		return forPlayer == activePlayer && selectedUnit != null && tmp.contains(field);
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
			log.error("execSelectHandCard but selectedUnit was " + selectedUnit.getUnitType());
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
		Field target = getGame().getBoard().getField(param);
		if (target.getUnit() != null) {
			log.error("execSelectTargetField but already occupied field was seleted");
			return;
		}
		target.setUnit(selectedUnit);
		selectedUnit.setDeployedOn(target);
		player.getUnitInHand().remove(selectedUnit);
		selectedUnit = null;
		switchPlayer(player);

	}

	protected void switchPlayer(Player player) {
		boolean nextPhase = false;
		activePlayer.getClientMessages().clearErrorInfo();
		Player nextPlayer = GameUtil.getOtherPlayer(getGame(), activePlayer);
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
			updateUI();
		}
	}

	@Override
	public void updateUI() {
		getGame().getPlayers().forEach(player -> {
			if (player == activePlayer) {
				if (selectedUnit != null) {
					player.getClientMessages().setTitle("Select a highlighted field to deploy "
							+ selectedUnit.getUnitType() + " or click the unit again to de-select it");
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
		getGame().setCurrentPhase(new CombatPhase(getGame()));
	}

	private boolean hasMoreMoves(Player p) {
		return !p.getUnitInHand().isEmpty();
	}

}
