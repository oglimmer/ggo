package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Structure;
import de.oglimmer.ggo.logic.Unit;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIUnit {

	// IMMUTABLE
	
	@Getter
	private String jsClass = "Unit";

	@Getter
	private String id;
	@Getter
	private String color;
	@Getter
	private String unitType;

	// CHANGABLE

	@Getter
	private Integer x;
	@Getter
	private Integer y;
	@Getter
	private Boolean selectable;
	@Getter
	private Boolean selected;
	@Getter
	private UICommand command;

	public UIUnit(Structure structure, String color, int x, int y) {
		this.id = structure.getId();
		this.color = color;
		this.unitType = structure.getType().toString();
		this.x = x;
		this.y = y;
		this.selectable = false;
		this.selected = false;
	}

	public UIUnit(Unit unit, int x, int y, Player forPlayer) {
		this.id = unit.getId();
		this.color = unit.getPlayer().getSide().toString();
		this.unitType = unit.getUnitType().toString();
		this.x = x;
		this.y = y;
		this.selectable = unit.isSelectable(forPlayer);
		this.selected = unit.isSelected(forPlayer);
		this.command = UICommand.create(forPlayer, unit);
	}

}
