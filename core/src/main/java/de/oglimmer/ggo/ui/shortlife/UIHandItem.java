package de.oglimmer.ggo.ui.shortlife;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIHandItem {

	// IMMUTABLE
	
	@Getter
	private String jsClass = "HandItem";

	@Getter
	private String id;
	@Getter
	private String unitType;

	// CHANGABLE

	@Getter
	private Boolean selectable;
	@Getter
	private Boolean selected;

	public UIHandItem(Unit u, Player player) {
		this.id = u.getId();
		this.unitType = u.getUnitType().toString();
		this.selectable = u.isSelectable(player);
		this.selected = u.isSelected(player);
	}

}
