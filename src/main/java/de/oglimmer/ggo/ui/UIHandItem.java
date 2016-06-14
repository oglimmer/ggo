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

	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String unitType;
	@Getter
	@Setter
	private Boolean selected;
	@Getter
	@Setter
	private Boolean selectable;

	public void copy(Unit u, Player player) {
		this.id = u.getId();
		this.unitType = u.getType().toString();
		this.selected = u.isSelected(player);
		this.selectable = u.isSelectable(player);
	}

	public UIHandItem diffAndUpdate(Unit u, Player player) {
		UIHandItem diff = new UIHandItem();
		boolean changed = false;

		// this.id == IMMUTABLE
		// this.unitType == IMMUTABLE

		boolean latestSelected = u.isSelected(player);
		if (this.selected != latestSelected) {
			diff.setSelected(latestSelected);
			this.selected = latestSelected;
			changed = true;
		}
		boolean latestSelectable = u.isSelectable(player);
		if (this.selectable != latestSelectable) {
			diff.setSelectable(latestSelectable);
			this.selectable = latestSelectable;
			changed = true;
		}

		return changed ? diff : null;
	}
}
