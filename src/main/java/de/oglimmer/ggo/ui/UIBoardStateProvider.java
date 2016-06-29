package de.oglimmer.ggo.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.oglimmer.ggo.logic.Player;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class UIBoardStateProvider {

	@NonNull
	private Player forPlayer;

	public Map<String, UIField> getCorToFields() {
		return forPlayer.getGame().getBoard().getFields().stream().map(f -> new UIField(f, forPlayer))
				.collect(Collectors.toMap(UIField::getId, hi -> hi));
	}

	public Map<String, UIUnit> getIdToUnits() {
		Map<String, UIUnit> result = new HashMap<>();
		result.putAll(forPlayer.getGame().getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.map(f -> new UIUnit(f.getUnit(), f.getPos().x, f.getPos().y, forPlayer))
				.collect(Collectors.toMap(UIUnit::getId, hi -> hi)));
		result.putAll(forPlayer.getGame().getBoard().getFields().stream()
				.filter(f -> f.getStructure() != null).map(f -> new UIUnit(f.getStructure(),
						f.getStructure().getPlayer().getSide().toString(), f.getPos().x, f.getPos().y))
				.collect(Collectors.toMap(UIUnit::getId, hi -> hi)));
		return result;
	}

	public Map<String, UIHandItem> getIdToHanditems() {
		return forPlayer.getUnitInHand().stream().map(u -> new UIHandItem(u, forPlayer))
				.collect(Collectors.toMap(UIHandItem::getId, hi -> hi));
	}

	public Map<String, UIButton> getIdToButtons() {
		return forPlayer.getGame().getCurrentPhase().getButtons(forPlayer).stream()
				.collect(Collectors.toMap(UIButton::getId, b -> b));
	}

	public Boolean isShowCoordinates() {
		return forPlayer.getGame().getCurrentPhase().isShowCoordinates();
	}
}
