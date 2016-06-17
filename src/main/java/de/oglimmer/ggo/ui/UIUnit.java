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

	// IMMUTABLE

	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String color;
	@Getter
	@Setter
	private String unitType;

	// CHANGABLE

	@Getter
	@Setter
	private DiffableInteger x;
	@Getter
	@Setter
	private DiffableInteger y;
	@Getter
	@Setter
	private DiffableBoolean selectable;
	@Getter
	@Setter
	private DiffableBoolean selected;

	public void copy(Structure structure, String color, int x, int y) {
		this.id = structure.getId();
		this.color = color;
		this.unitType = structure.getType().toString();
		this.x = DiffableInteger.create(x);
		this.y = DiffableInteger.create(y);
		this.selected = DiffableBoolean.create(false);
		this.selectable = DiffableBoolean.create(false);
	}

	public void copy(Unit unit, int x, int y, Player forPlayer) {
		this.id = unit.getId();
		this.color = unit.getPlayer().getSide().toString();
		this.unitType = unit.getUnitType().toString();
		this.x = DiffableInteger.create(x);
		this.y = DiffableInteger.create(y);
		this.selected = DiffableBoolean.create(unit.isSelected(forPlayer));
		this.selectable = DiffableBoolean.create(unit.isSelectable(forPlayer));
	}

	public UIUnit diffAndUpdate(Unit u, int latestX, int latestY, Player forPlayer) {
		UIUnit diff = new UIUnit();
		boolean changed = false;
		changed |= x.diffAndUpdate(latestX, diff::setX);
		changed |= y.diffAndUpdate(latestY, diff::setY);
		changed |= selected.diffAndUpdate(u.isSelected(forPlayer), diff::setSelected);
		changed |= selectable.diffAndUpdate(u.isSelectable(forPlayer), diff::setSelectable);
		return changed ? diff : null;
	}

}
