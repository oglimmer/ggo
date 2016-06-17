package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatPhase extends BasePhase {

	private int round = 0;

	private Map<Player, Unit> selectedUnits = new HashMap<>();
	private Set<Player> inTurn = new HashSet<>();

	public CombatPhase(Game game) {
		inTurn.addAll(game.getPlayers());
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		Unit unit = selectedUnits.get(forPlayer);
		return unit != null && unit.getDeployedOn().getNeighbords().contains(field);
	}

	@Override
	public boolean isSelectable(Field field, Player forPlayer) {
		return isHighlighted(field, forPlayer);
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
		case "selectTargetField":
			break;
		}
	}

	private void execselectUnit(Player player, String param) {
		Unit unit = player.getGame().getUnitById(param);
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

	public Collection<UIButton> getButtons(Player forPlayer) {
		Collection<UIButton> buttons = new ArrayList<>();
		buttons.add(new UIButton("doneButton", "Done", DiffableBoolean.create(false)));
		return buttons;
	}
}
