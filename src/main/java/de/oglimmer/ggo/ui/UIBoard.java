package de.oglimmer.ggo.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Structure;
import de.oglimmer.ggo.logic.Unit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
public class UIBoard {

	@Getter
	private Map<String, UIField> corToFields = new HashMap<>();

	@Getter
	private Map<String, UIUnit> idToUnits = new HashMap<>();
	@Getter
	private Set<String> unitsToRemove = new HashSet<>();

	@Getter
	private Map<String, UIHandItem> idToHanditems = new HashMap<>();
	@Getter
	private Set<String> handitemsToRemove = new HashSet<>();

	@Getter
	private Map<String, UIButton> idToButtons = new HashMap<>();
	@Getter
	private Set<String> buttonsToRemove = new HashSet<>();

	@Getter
	@Setter
	private DiffableBoolean showCoordinates = DiffableBoolean.create(null);

	public boolean hasChange() {
		return !corToFields.isEmpty() || !idToUnits.isEmpty() || !unitsToRemove.isEmpty() || !idToHanditems.isEmpty()
				|| !handitemsToRemove.isEmpty() || !idToButtons.isEmpty() || !buttonsToRemove.isEmpty()
				|| showCoordinates.getVal() != null;
	}

	public UIBoard calcStateAndDiff(Player player) {
		return new TransferBuilder(player).calcStateAndDiff();
	}

	@RequiredArgsConstructor
	class TransferBuilder {

		@NonNull
		private Player uiUpdateForPlayer;

		private UIBoard transferStates = new UIBoard();

		public UIBoard calcStateAndDiff() {
			calcDiffBoard();
			calcDiffRemovedUnits();
			calcDiffHanditems();
			calcDiffButtons();
			calcShowCoordinates();
			return transferStates;
		}

		private void calcShowCoordinates() {
			boolean bb = uiUpdateForPlayer.getGame().getCurrentPhase().isShowCoordinates();
			System.out.println(bb);
			showCoordinates.diffAndUpdate(bb,
					transferStates::setShowCoordinates);
		}

		private void calcDiffButtons() {
			Collection<UIButton> currentButtonsToShwo = uiUpdateForPlayer.getGame().getCurrentPhase()
					.getButtons(uiUpdateForPlayer);

			calcDiffAddChangedButtons(currentButtonsToShwo);
			calcDiffRemoveButtons(currentButtonsToShwo);

		}

		private void calcDiffRemoveButtons(Collection<UIButton> currentButtonsToShwo) {
			for (Iterator<Map.Entry<String, UIButton>> it = uiUpdateForPlayer.getUiStates().getClientUIState()
					.getIdToButtons().entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, UIButton> en = it.next();
				String buttonId = en.getKey();

				if (!currentButtonsToShwo.stream().anyMatch(b -> b.getId().equals(buttonId))) {
					it.remove();
					transferStates.getButtonsToRemove().add(buttonId);
				}
			}
		}

		private void calcDiffAddChangedButtons(Collection<UIButton> buttons) {
			buttons.forEach(b -> {
				UIButton state = uiUpdateForPlayer.getUiStates().getClientUIState().getIdToButtons().get(b.getId());
				if (state == null) {
					state = new UIButton(b.getId(), b.getText(), null, 30, 20, b.getHidden());
					state.copy(b);
					uiUpdateForPlayer.getUiStates().getClientUIState().getIdToButtons().put(b.getId(), state);
					transferStates.getIdToButtons().put(b.getId(), state);
				} else {
					UIButton diff = state.diffAndUpdate(b);
					if (diff != null) {
						transferStates.getIdToButtons().put(b.getId(), diff);
					}
				}
			});
		}

		private void calcDiffHanditems() {
			calcDiffAddChangedHanditems();
			calcDiffRemovedHanditems();
		}

		private void calcDiffRemovedHanditems() {
			for (Iterator<Map.Entry<String, UIHandItem>> it = uiUpdateForPlayer.getUiStates().getClientUIState()
					.getIdToHanditems().entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, UIHandItem> en = it.next();
				String handitemId = en.getKey();
				if (!uiUpdateForPlayer.getUnitInHand().stream().anyMatch(h -> h.getId().equals(handitemId))) {
					it.remove();
					transferStates.getHanditemsToRemove().add(handitemId);
				}
			}
		}

		private void calcDiffAddChangedHanditems() {
			uiUpdateForPlayer.getUnitInHand().forEach(u -> {
				UIHandItem uiHandItem = uiUpdateForPlayer.getUiStates().getClientUIState().getIdToHanditems()
						.get(u.getId());
				if (uiHandItem == null) {
					uiHandItem = new UIHandItem();
					uiHandItem.copy(u, uiUpdateForPlayer);
					uiUpdateForPlayer.getUiStates().getClientUIState().getIdToHanditems().put(u.getId(), uiHandItem);
					transferStates.getIdToHanditems().put(u.getId(), uiHandItem);
				} else {
					UIHandItem diff = uiHandItem.diffAndUpdate(u, uiUpdateForPlayer);
					if (diff != null) {
						transferStates.getIdToHanditems().put(u.getId(), diff);
					}
				}
			});
		}

		private void calcDiffRemovedUnits() {
			for (Iterator<Map.Entry<String, UIUnit>> it = uiUpdateForPlayer.getUiStates().getClientUIState()
					.getIdToUnits().entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, UIUnit> en = it.next();
				String unitId = en.getKey();
				UIUnit unit = en.getValue();
				if (!unit.getUnitType().equals("city")) {// HACK: need to find a
															// better
															// way to detect a
															// structure
					if (!uiUpdateForPlayer.getGame().getBoard().getFields().stream().filter(f -> f.getUnit() != null)
							.anyMatch(f -> f.getUnit().getId().equals(unitId))) {
						it.remove();
						transferStates.getUnitsToRemove().add(unitId);
					}
				}
			}

		}

		private void calcDiffBoard() {
			uiUpdateForPlayer.getGame().getBoard().getFields().forEach(f -> {
				calcDiffAddChangedFields(f);
				calcDiffAddStructure(f);
				calcDiffAddChangedUnits(f);
			});
		}

		private void calcDiffAddChangedUnits(Field f) {
			if (f.getUnit() != null) {
				Unit unit = f.getUnit();
				String unitId = unit.getId();
				UIUnit uiUnit = uiUpdateForPlayer.getUiStates().getClientUIState().getIdToUnits().get(unitId);
				if (uiUnit == null) {
					uiUnit = new UIUnit();
					uiUnit.copy(unit, (int) f.getPos().getX(), (int) f.getPos().getY(), uiUpdateForPlayer);
					uiUpdateForPlayer.getUiStates().getClientUIState().getIdToUnits().put(unitId, uiUnit);
					transferStates.getIdToUnits().put(unitId, uiUnit);
				} else {
					UIUnit diff = uiUnit.diffAndUpdate(unit, (int) f.getPos().getX(), (int) f.getPos().getY(),
							uiUpdateForPlayer);
					if (diff != null) {
						transferStates.getIdToUnits().put(unitId, diff);
					}
				}
			}
		}

		private void calcDiffAddStructure(Field f) {
			if (f.getStructure() != null) {
				Structure structure = f.getStructure();
				String structureId = structure.getId();
				UIUnit uiUnit = uiUpdateForPlayer.getUiStates().getClientUIState().getIdToUnits().get(structureId);
				if (uiUnit == null) {
					uiUnit = new UIUnit();
					uiUnit.copy(structure, structure.getPlayer().getSide().toString(), (int) f.getPos().getX(),
							(int) f.getPos().getY());
					uiUpdateForPlayer.getUiStates().getClientUIState().getIdToUnits().put(structureId, uiUnit);
					transferStates.getIdToUnits().put(structureId, uiUnit);
				}
			}
		}

		private void calcDiffAddChangedFields(Field f) {
			String id = (int) f.getPos().getX() + ":" + (int) f.getPos().getY();
			UIField uiField = uiUpdateForPlayer.getUiStates().getClientUIState().getCorToFields().get(id);
			if (uiField == null) {
				// field from logic not in UI
				uiField = new UIField(); // created
				uiField.copy(f, uiUpdateForPlayer); // fill it
				// add it to uistate
				uiUpdateForPlayer.getUiStates().getClientUIState().getCorToFields().put(id, uiField);
				// add it to transfer
				transferStates.getCorToFields().put(id, uiField);
			} else {
				UIField diff = uiField.diffAndUpdate(f, uiUpdateForPlayer);
				if (diff != null) {
					transferStates.getCorToFields().put(id, diff);
				}
			}
		}
	}
}
