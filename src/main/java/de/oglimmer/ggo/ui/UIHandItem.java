package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@ToString
public class UIHandItem {

	// IMMUTABLE

	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String unitType;

	// CHANGABLE

	@Getter
	@Setter
	private DiffableBoolean selectable;
	@Getter
	@Setter
	private DiffableBoolean selected;

	public void copy(Unit u, Player player) {
		this.id = u.getId();
		this.unitType = u.getUnitType().toString();
		this.selected = DiffableBoolean.create(u.isSelected(player));
		this.selectable = DiffableBoolean.create(u.isSelectable(player));
	}

	public UIHandItem diffAndUpdate(Unit u, Player player) {
		UIHandItem diff = new UIHandItem();
		boolean changed = false;
		changed |= selected.diffAndUpdate(u.isSelected(player), diff::setSelected);
		changed |= selectable.diffAndUpdate(u.isSelectable(player), diff::setSelectable);
		return changed ? diff : null;
	}
}
