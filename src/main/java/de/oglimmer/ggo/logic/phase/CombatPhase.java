package de.oglimmer.ggo.logic.phase;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.oglimmer.ggo.logic.Constants;
import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.MessageQueue;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatPhase extends BasePhase {

	private static final int MAX_ROUNDS = 3;

	private int round = 0;

	private Map<Player, State> states = new HashMap<>();

	private Set<Player> inTurn = new HashSet<>();
	private CommandCenter cc;

	public CombatPhase(Game game) {
		super(game);
		cc = new CommandCenter(game);
	}

	private State get(Player player) {
		return states.computeIfAbsent(player, t -> new State(player));
	}

	@Override
	public void init() {
		if (getGame().getBoard().getTotalUnits() == 0) {
			nextPhase();
		} else {
			inTurn.addAll(getGame().getPlayers());
			cc.clearCommands();
			getGame().getPlayers().forEach(p -> p.getUiStates().getClientMessages().clearErrorInfo());
		}
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		return states.get(forPlayer).isHighlighted(field);
	}

	@Override
	public boolean isSelectable(Field field, Player forPlayer) {
		return isHighlighted(field, forPlayer);
	}

	@Override
	public boolean isSelected(Unit unit, Player forPlayer) {
		return states.get(forPlayer).isSelected(unit);
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return states.get(forPlayer).isSelectable(unit);
	}

	@Override
	public void execCmd(Player player, String cmd, String param, MessageQueue messages) {
		super.execCmd(player, cmd, param, messages);
		switch (cmd) {
		case "selectUnit":
			execSelectUnit(player, param);
			break;
		case "selectTargetField":
			execTargetField(player, param);
			break;
		case "selectModalDialog":
			execModalDialog(player, param, messages);
			break;
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
			roundEnded();
		}
	}

	private void roundEnded() {
		calcBattle();
		round++;
		if (round == MAX_ROUNDS || getGame().getBoard().getTotalUnits() == 0) {
			nextPhase();
		} else {
			inTurn.addAll(getGame().getPlayers());
		}
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

		cc.clearCommands();
	}

	private void execModalDialog(Player player, String param, MessageQueue messages) {
		Unit unit = get(player).getSelectedUnits();
		if (unit == null) {
			log.error("execTargetField but no unit was selected");
			return;
		}
		Field targetField = get(player).getSelectedFields();
		if (targetField == null) {
			log.error("execTargetField but no target field was selected");
			return;
		}
		if (!"Cancel".equalsIgnoreCase(param)) {
			CommandType commandType = CommandType.valueOf(param);
			cc.addCommand(unit, targetField, commandType);
		}
		get(player).clear();
		ObjectNode root = instance.objectNode();
		messages.addMessage(player, Constants.RESP_MODAL_DIALOG_DIS, root);
	}

	private void execTargetField(Player player, String param) {
		Unit unit = get(player).getSelectedUnits();
		if (unit == null) {
			log.error("execTargetField but no unit was selected");
			return;
		}
		Field targetField = getGame().getBoard().getField(param);
		Set<CommandType> possibleCommandTypes = unit.getPossibleCommandTypes(cc, targetField);
		if (possibleCommandTypes.size() == 0) {
			player.getUiStates().getClientMessages().setError(
					"One of your own units is/will be alreay there. De-select your unit or chose another target field.");
		} else if (possibleCommandTypes.size() == 1) {
			cc.addCommand(unit, targetField, possibleCommandTypes.iterator().next());
			get(player).clear();
		} else {
			get(player).setPossibleCommandTypesOptions(possibleCommandTypes);
			get(player).setSelectedFields(targetField);
		}
	}

	private void execSelectUnit(Player player, String param) {
		Unit unit = getGame().getUnitById(param);
		Unit currentlySelected = get(player).getSelectedUnits();
		if (currentlySelected != null && currentlySelected != unit) {
			log.error("Player {} has unit selected", player.getSide());
			return;
		}
		if (currentlySelected == unit) {
			cc.removeCommandForUnit(unit);
			cc.addCommand(unit, unit.getDeployedOn(), CommandType.FORTIFY);
			get(player).clear();
		} else {
			get(player).setSelectedUnits(unit);
		}
	}

	@Override
	protected void updateMessage(Player player, MessageQueue messages) {
		String title;
		if (inTurn.contains(player)) {
			Unit unit = get(player).getSelectedUnits();
			if (unit != null) {
				title = "Choose a destination field for " + unit.getUnitType().toString();
			} else {
				title = "Command your units. Press `done` when finished. Round " + (round + 1) + " of " + MAX_ROUNDS;
			}
		} else {
			title = "Wait for your opponent to finish the turn. Round " + (round + 1) + " of " + MAX_ROUNDS;
		}
		player.getUiStates().getClientMessages().setTitle(title);
	}

	@Override
	protected void updateModalDialg(Player player, MessageQueue messages) {
		if (get(player).getPossibleCommandTypesOptions() != null) {
			ObjectNode root = instance.objectNode();
			root.set("title", instance.textNode("Choose a command"));
			ArrayNode options = instance.arrayNode();
			for (CommandType ct : get(player).getPossibleCommandTypesOptions()) {
				ObjectNode option = instance.objectNode();
				option.set("id", instance.textNode(ct.name()));
				option.set("description", instance.textNode(ct.name()));
				options.add(option);
			}
			root.set("options", options);
			messages.addMessage(player, Constants.RESP_MODAL_DIALOG_EN, root);
		}
	}

	@Override
	protected void nextPhase() {

		getGame().getBoard().getFields().stream().filter(f -> f.getStructure() != null).filter(f -> f.getUnit() != null)
				.filter(f -> f.getUnit().getPlayer() != f.getStructure().getPlayer()).forEach(f -> {
					f.getUnit().getPlayer().incScore(25);
					log.debug("Player {} scores 25 points for owning a city.", f.getUnit().getPlayer().getSide());
				});

		getGame().setCurrentPhase(new DraftPhase(getGame()));
		getGame().getCurrentPhase().init();
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
		Command command = cc.getByUnit(unit);
		if (command != null && command.getUnit().getPlayer() == forPlayer) {
			return command;
		}
		return null;
	}

	@RequiredArgsConstructor
	@Data
	class State {
		@NonNull
		private Player player;
		private Unit selectedUnits;
		private Field selectedFields;
		private Set<CommandType> possibleCommandTypesOptions;

		public void clear() {
			selectedUnits = null;
			selectedFields = null;
			possibleCommandTypesOptions = null;
		}

		public boolean isHighlighted(Field field) {
			if (isPickUnit()) {
				return false;
			} else if (isPickTargetField()) {
				return selectedUnits.hasCommandOnField(cc, field);
			} else if (isPickCommand()) {
				return selectedUnits.hasCommandOnField(cc, field);
			}
			return false;
		}

		public boolean isSelected(Unit unit) {
			return selectedUnits == unit && unit.getPlayer() == player;
		}

		public boolean isSelectable(Unit unit) {
			if (isPickUnit()) {
				return unit.getPlayer() == player && unit.isOnBoard();
			} else if (isPickTargetField()) {
				return unit == selectedUnits;
			} else if (isPickCommand()) {
				return unit == selectedUnits;
			}
			return false;
		}

		private boolean isPickUnit() {
			return selectedUnits == null && selectedFields == null && possibleCommandTypesOptions == null;
		}

		private boolean isPickTargetField() {
			return selectedUnits != null && selectedFields == null && possibleCommandTypesOptions == null;
		}

		private boolean isPickCommand() {
			return selectedUnits != null && selectedFields != null && possibleCommandTypesOptions != null;
		}
	}
}
