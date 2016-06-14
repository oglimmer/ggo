package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Player;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@ToString
public class UIField {

	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private Integer x;
	@Getter
	@Setter
	private Integer y;

	@Getter
	@Setter
	private Boolean highlight;
	@Getter
	@Setter
	private Boolean selectable;

	public void copy(Field f, Player player) {
		this.id = f.getId();
		this.x = (int) f.getPos().getX();
		this.y = (int) f.getPos().getY();
		this.highlight = f.isHighlighted(player);
		this.selectable = f.isSelectable(player);
	}

	public UIField diffAndUpdate(Field f, Player player) {
		UIField diff = new UIField();
		boolean changed = false;

		// this.id == IMMUTABLE
		// this.x == IMMUTABLE
		// this.y == IMMUTABLE

		boolean highlighted = f.isHighlighted(player);
		if (this.highlight != highlighted) {
			diff.setHighlight(highlighted);
			this.highlight = highlighted;
			changed = true;
		}
		boolean selected = f.isSelectable(player);
		if (this.selectable != selected) {
			diff.setSelectable(selected);
			this.selectable = selected;
			changed = true;
		}
		return changed ? diff : null;
	}
}
