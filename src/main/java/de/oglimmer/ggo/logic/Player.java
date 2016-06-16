package de.oglimmer.ggo.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.oglimmer.ggo.ui.UIBoard;
import de.oglimmer.ggo.ui.UIField;
import de.oglimmer.ggo.ui.UIHandItem;
import de.oglimmer.ggo.ui.UIMessages;
import de.oglimmer.ggo.ui.UIUnit;
import lombok.Getter;
import lombok.NonNull;

public class Player {

	@Getter
	@NonNull
	private String id;

	@Getter
	@NonNull
	private Side side;

	@Getter
	@NonNull
	private Game game;

	@Getter
	@NonNull
	private UIBoard clientUIState = new UIBoard();
	@Getter
	@NonNull
	private UIMessages clientMessages = new UIMessages();
	private UIMessages lastClientStateMessages = new UIMessages();

	@Getter
	private List<Unit> unitInHand = new ArrayList<>();

	public Player(String id, Side side, Game game) {
		this.id = id;
		this.side = side;
		this.game = game;
		unitInHand.add(new Unit(this, UnitType.INFANTERY));
		// unitInHand.add(new Unit(this, UnitType.INFANTERY));
		// unitInHand.add(new Unit(this, UnitType.INFANTERY));
		// unitInHand.add(new Unit(this, UnitType.TANK));
		// unitInHand.add(new Unit(this, UnitType.AIRBORNE));
		// unitInHand.add(new Unit(this, UnitType.AIRBORNE));
		// unitInHand.add(new Unit(this, UnitType.HELICOPTER));
		// unitInHand.add(new Unit(this, UnitType.ARTILLERY));
	}

	public Set<Field> getValidTargetFields() {
		Set<Field> validFields = new HashSet<>();
		if (side == Side.GREEN) {
			validFields.addAll(game.getBoard().getFields().stream().filter(f -> f.getPos().getX() <= 4)
					.collect(Collectors.toSet()));
		} else {
			validFields.addAll(game.getBoard().getFields().stream().filter(f -> f.getPos().getX() >= 5)
					.collect(Collectors.toSet()));
		}
		validFields.addAll(game.getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.filter(f -> f.getUnit().getPlayer() == this).flatMap(f -> f.getNeighbords().stream())
				.collect(Collectors.toSet()));
		return validFields;
	}

	public void resetUiState() {
		clientUIState = new UIBoard();
		clientMessages = new UIMessages();
		lastClientStateMessages = new UIMessages();
	}

	public UIMessages calcDiffMessages() {
		UIMessages transfer = new UIMessages();
		if (lastClientStateMessages.getTitle() != null && clientMessages.getTitle() == null
				|| lastClientStateMessages.getTitle() == null && clientMessages.getTitle() != null
				|| (lastClientStateMessages.getTitle() != null
						&& !lastClientStateMessages.getTitle().equals(clientMessages.getTitle()))) {
			transfer.setTitle(clientMessages.getTitle());
			lastClientStateMessages.setTitle(clientMessages.getTitle());
		}
		if (lastClientStateMessages.getInfo() != null && clientMessages.getInfo() == null
				|| lastClientStateMessages.getInfo() == null && clientMessages.getInfo() != null
				|| (lastClientStateMessages.getInfo() != null
						&& !lastClientStateMessages.getInfo().equals(clientMessages.getInfo()))) {
			transfer.setInfo(clientMessages.getInfo());
			lastClientStateMessages.setInfo(clientMessages.getInfo());
		}
		if (lastClientStateMessages.getError() != null && clientMessages.getError() == null
				|| lastClientStateMessages.getError() == null && clientMessages.getError() != null
				|| (lastClientStateMessages.getError() != null
						&& !lastClientStateMessages.getError().equals(clientMessages.getError()))) {
			transfer.setError(clientMessages.getError());
			lastClientStateMessages.setError(clientMessages.getError());
		}
		return transfer;
	}

	public UIBoard calcDiff() {
		UIBoard transferStates = new UIBoard();
		calcDiffBoard(transferStates);
		calcDiffRemovedUnits(transferStates);
		calcDiffHanditems(transferStates);
		return transferStates;
	}

	private void calcDiffHanditems(UIBoard transferStates) {
		calcDiffAddChangedHanditems(transferStates);
		calcDiffRemovedHanditems(transferStates);
	}

	private void calcDiffRemovedHanditems(UIBoard transferStates) {
		for (Iterator<Map.Entry<String, UIHandItem>> it = clientUIState.getIdToHanditems().entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<String, UIHandItem> en = it.next();
			String handitemId = en.getKey();
			if (!unitInHand.stream().anyMatch(h -> h.getId().equals(handitemId))) {
				it.remove();
				transferStates.getHanditemsToRemove().add(handitemId);
			}
		}
	}

	private void calcDiffAddChangedHanditems(UIBoard transferStates) {
		unitInHand.forEach(u -> {
			UIHandItem uiHandItem = clientUIState.getIdToHanditems().get(u.getId());
			if (uiHandItem == null) {
				uiHandItem = new UIHandItem();
				uiHandItem.copy(u, this);
				clientUIState.getIdToHanditems().put(u.getId(), uiHandItem);
				transferStates.getIdToHanditems().put(u.getId(), uiHandItem);
			} else {
				UIHandItem diff = uiHandItem.diffAndUpdate(u, this);
				if (diff != null) {
					transferStates.getIdToHanditems().put(u.getId(), diff);
				}
			}
		});
	}

	private void calcDiffRemovedUnits(UIBoard transferStates) {
		for (Iterator<Map.Entry<String, UIUnit>> it = clientUIState.getIdToUnits().entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<String, UIUnit> en = it.next();
			String unitId = en.getKey();
			UIUnit unit = en.getValue();
			if (!unit.getType().equals("city")) {// HACK: need to find a better
													// way to detect a structure
				if (!game.getBoard().getFields().stream().filter(f -> f.getUnit() != null)
						.anyMatch(f -> f.getUnit().getId().equals(unitId))) {
					it.remove();
					transferStates.getUnitsToRemove().add(unitId);
				}
			}
		}

	}

	private void calcDiffBoard(UIBoard transferStates) {
		game.getBoard().getFields().forEach(f -> {
			calcDiffAddChangedFields(transferStates, f);
			calcDiffAddStructure(transferStates, f);
			calcDiffAddChangedUnits(transferStates, f);
		});
	}

	private void calcDiffAddChangedUnits(UIBoard transferStates, Field f) {
		if (f.getUnit() != null) {
			Unit unit = f.getUnit();
			String unitId = unit.getId();
			UIUnit uiUnit = clientUIState.getIdToUnits().get(unitId);
			if (uiUnit == null) {
				uiUnit = new UIUnit();
				uiUnit.copy(unit, (int) f.getPos().getX(), (int) f.getPos().getY(), this);
				clientUIState.getIdToUnits().put(unitId, uiUnit);
				transferStates.getIdToUnits().put(unitId, uiUnit);
			} else {
				UIUnit diff = uiUnit.diffAndUpdate(unit, (int) f.getPos().getX(), (int) f.getPos().getY(), this);
				if (diff != null) {
					transferStates.getIdToUnits().put(unitId, diff);
				}
			}
		}
	}

	private void calcDiffAddStructure(UIBoard transferStates, Field f) {
		if (f.getStructure() != null) {
			Structure structure = f.getStructure();
			String structureId = structure.getId();
			UIUnit uiUnit = clientUIState.getIdToUnits().get(structureId);
			if (uiUnit == null) {
				uiUnit = new UIUnit();
				uiUnit.copy(structure, structure.getPlayer().getSide().toString(), (int) f.getPos().getX(),
						(int) f.getPos().getY());
				clientUIState.getIdToUnits().put(structureId, uiUnit);
				transferStates.getIdToUnits().put(structureId, uiUnit);
			}
		}
	}

	private void calcDiffAddChangedFields(UIBoard transferStates, Field f) {
		String id = (int) f.getPos().getX() + ":" + (int) f.getPos().getY();
		UIField uiField = clientUIState.getCorToFields().get(id);
		if (uiField == null) {
			// field from logic not in UI
			uiField = new UIField(); // created
			uiField.copy(f, this); // fill it
			// add it to uistate
			clientUIState.getCorToFields().put(id, uiField);
			// add it to transfer
			transferStates.getCorToFields().put(id, uiField);
		} else {
			UIField diff = uiField.diffAndUpdate(f, this);
			if (diff != null) {
				transferStates.getCorToFields().put(id, diff);
			}
		}
	}

	public Unit getUnitById(String unitId) {
		return game.getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.filter(f -> f.getUnit().getId().equals(unitId)).map(f -> f.getUnit()).findFirst().get();
	}

}
