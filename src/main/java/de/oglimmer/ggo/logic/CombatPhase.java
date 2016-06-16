package de.oglimmer.ggo.logic;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatPhase extends BasePhase {

	private int round = 0;

	private Map<Player, Unit> selectedUnits = new HashMap<>();

	public CombatPhase() {
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		return false;
	}

	@Override
	public boolean isSelectable(Field field, Player forPlayer) {
		return false;
	}

	@Override
	public boolean isSelected(Unit unit, Player forPlayer) {
		return selectedUnits.get(forPlayer) == unit && unit.getPlayer() == forPlayer;
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return unit.getPlayer() == forPlayer && unitOnBoard(unit);
	}

	private boolean unitOnBoard(Unit unit) {
		return unit.getPlayer().getGame().getBoard().getFields().stream().anyMatch(f -> f.getUnit() == unit);
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
		super.execCmd(player, cmd, param);
		switch (cmd) {
		case "selectUnit":
			execselectUnit(player, param);
			break;
		}
	}

	private void execselectUnit(Player player, String param) {
		Unit unit = player.getUnitById(param);
		Unit currentlySelected = selectedUnits.get(player);
		if (currentlySelected != null && currentlySelected != unit) {
			log.error("Player {} has unit selected", player.getSide());
			return;
		}
		if (currentlySelected == unit) {
			selectedUnits.remove(player);
		} else {
			selectedUnits.put(player, unit);
		}
	}

	@Override
	public void updateUI(Game game) {
		game.getPlayers().forEach(player -> {
			player.getClientMessages().setTitle("XXXX");
		});
	}

	@Override
	protected void nextPhase(Player firstPlayer) {
		firstPlayer.getGame().setCurrentPhase(new DeployPhase(firstPlayer));
	}

}
