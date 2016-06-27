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

import de.oglimmer.ggo.com.Constants;
import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.MessageQueue;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.battle.CombatPhaseRoundCounter;
import de.oglimmer.ggo.logic.battle.Command;
import de.oglimmer.ggo.logic.battle.CommandCenter;
import de.oglimmer.ggo.logic.battle.CommandType;
import de.oglimmer.ggo.ui.DiffableBoolean;
import de.oglimmer.ggo.ui.UIButton;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CombatCommandPhase extends BasePhase {

	private CombatPhaseRoundCounter combatPhaseRoundCounter;

	private Map<Player, State> states = new HashMap<>();

	private Set<Player> inTurn = new HashSet<>();
	private CommandCenter cc;

	public CombatCommandPhase(Game game, CombatPhaseRoundCounter combatPhaseRoundCounter) {
		super(game);
		this.combatPhaseRoundCounter = combatPhaseRoundCounter;
		if (this.combatPhaseRoundCounter == null) {
			this.combatPhaseRoundCounter = new CombatPhaseRoundCounter();
			getGame().getPlayers().forEach(p -> p.getUiStates().getClientMessages().clearErrorInfo());
		}
		cc = new CommandCenter(game);
	}

	private State getState(Player player) {
		return states.computeIfAbsent(player, t -> new State(player));
	}

	@Override
	public void init() {
		getGame().getPlayers().forEach(p -> p.getUiStates().getClientMessages().clearErrorInfo());
		if (getGame().getBoard().getTotalUnits() == 0) {
			nextPhase();
		} else {
			getGame().getPlayers().forEach(p -> {
				if (getGame().getBoard().getTotalUnits(p) > 0) {
					inTurn.add(p);
				}
			});
			if (inTurn.isEmpty()) {
				nextPhase();
			}

			cc.setAllToFortify();			
		}
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		return getState(forPlayer).isHighlighted(field);
	}

	@Override
	public boolean isSelectable(Field field, Player forPlayer) {
		return isHighlighted(field, forPlayer);
	}

	@Override
	public boolean isSelected(Unit unit, Player forPlayer) {
		return getState(forPlayer).isSelected(unit);
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return getState(forPlayer).isSelectable(unit);
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
			nextPhase();
		}
	}

	private void execModalDialog(Player player, String param, MessageQueue messages) {
		Unit unit = getState(player).getSelectedUnits();
		if (unit == null) {
			log.error("execTargetField but no unit was selected");
			return;
		}
		Field targetField = getState(player).getSelectedFields();
		if (targetField == null) {
			log.error("execTargetField but no target field was selected");
			return;
		}
		if (!"Cancel".equalsIgnoreCase(param)) {
			CommandType commandType = CommandType.valueOf(param);
			cc.addCommand(unit, targetField, commandType);
		}
		getState(player).clear();
		ObjectNode root = instance.objectNode();
		messages.addMessage(player, Constants.RESP_MODAL_DIALOG_DIS, root);
	}

	private void execTargetField(Player player, String param) {
		Unit unit = getState(player).getSelectedUnits();
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
			getState(player).clear();
		} else {
			getState(player).setPossibleCommandTypesOptions(possibleCommandTypes);
			getState(player).setSelectedFields(targetField);
		}
	}

	private void execSelectUnit(Player player, String param) {
		Unit unit = getGame().getUnitById(param);
		Unit currentlySelected = getState(player).getSelectedUnits();
		if (currentlySelected != null && currentlySelected != unit) {
			log.error("Player {} has unit selected", player.getSide());
			return;
		}
		if (currentlySelected == unit) {
			cc.removeCommandForUnit(unit);
			cc.addCommand(unit, unit.getDeployedOn(), CommandType.FORTIFY);
			getState(player).clear();
		} else {
			getState(player).setSelectedUnits(unit);
		}
	}

	@Override
	protected void updateMessage(Player player, MessageQueue messages) {
		String title;
		if (inTurn.contains(player)) {
			Unit unit = getState(player).getSelectedUnits();
			if (unit != null) {
				title = "Choose a destination field for " + unit.getUnitType().toString();
			} else {
				title = "Command your units. Press `done` when finished. Round "
						+ combatPhaseRoundCounter.getCurrentRound() + " of " + combatPhaseRoundCounter.getMaxRounds();
			}
		} else {
			title = "Wait for your opponent to finish the turn. Round " + combatPhaseRoundCounter.getCurrentRound()
					+ " of " + combatPhaseRoundCounter.getMaxRounds();
		}
		player.getUiStates().getClientMessages().setTitle(title);
	}

	@Override
	protected void updateModalDialg(Player player, MessageQueue messages) {
		if (getState(player).getPossibleCommandTypesOptions() != null) {
			ObjectNode root = instance.objectNode();
			root.set("title", instance.textNode("Choose a command"));
			ArrayNode options = instance.arrayNode();
			for (CommandType ct : getState(player).getPossibleCommandTypesOptions()) {
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
		getGame().setCurrentPhase(new CombatDisplayPhase(getGame(), combatPhaseRoundCounter, cc));
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
