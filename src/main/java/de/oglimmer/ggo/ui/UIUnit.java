package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Structure;
import de.oglimmer.ggo.logic.Unit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@ToString
public class UIUnit {

	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String color;
	@Getter
	@Setter
	private String type;
	@Getter
	@Setter
	private Integer x;
	@Getter
	@Setter
	private Integer y;

	@Getter
	@Setter
	private Boolean selectable;
	@Getter
	@Setter
	private Boolean selected;

	public void copy(Structure structure, String color, int x, int y) {
		this.id = structure.getId();
		this.color = color;
		this.type = structure.getType();
		this.x = x;
		this.y = y;
		this.selectable = false;
	}

	public void copy(Unit unit, int x, int y, Player forPlayer) {
		this.id = unit.getId();
		this.color = unit.getPlayer().getSide().toString();
		this.type = unit.getType().toString();
		this.x = x;
		this.y = y;
		this.selected = unit.isSelected(forPlayer);
		this.selectable = unit.isSelectable(forPlayer);
	}

	public UIUnit diffAndUpdate(Unit u, int latestX, int latestY, Player forPlayer) {
		UIUnit diff = new UIUnit();
		boolean changed = false;

		// this.id == IMMUTABLE
		// this.color == IMMUTABLE
		// this.type == IMMUTABLE

		if (this.x != latestX) {
			diff.setX(latestX);
			this.x = latestX;
			changed = true;
		}
		if (this.y != latestY) {
			diff.setY(latestY);
			this.y = latestY;
			changed = true;
		}

		boolean latestSelected = u.isSelected(forPlayer);
		if (this.selected != latestSelected) {
			diff.setSelected(latestSelected);
			this.selected = latestSelected;
			changed = true;
		}
		boolean latestSelectable = u.isSelectable(forPlayer);
		if (this.selectable != latestSelectable) {
			diff.setSelectable(latestSelectable);
			this.selectable = latestSelectable;
			changed = true;
		}

		return changed ? diff : null;
	}

}
