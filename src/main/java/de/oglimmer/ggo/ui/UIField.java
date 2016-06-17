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

	// IMMUTABLE

	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private Integer x;
	@Getter
	@Setter
	private Integer y;

	// CHANGABLE

	@Getter
	@Setter
	private DiffableBoolean highlight;
	@Getter
	@Setter
	private DiffableBoolean selectable;

	public void copy(Field f, Player player) {
		this.id = f.getId();
		this.x = (int) f.getPos().getX();
		this.y = (int) f.getPos().getY();
		this.highlight = DiffableBoolean.create(f.isHighlighted(player));
		this.selectable = DiffableBoolean.create(f.isSelectable(player));
	}

	public UIField diffAndUpdate(Field f, Player player) {
		UIField diff = new UIField();
		boolean changed = false;
		changed |= highlight.diffAndUpdate(f.isHighlighted(player), diff::setHighlight);
		changed |= selectable.diffAndUpdate(f.isSelectable(player), diff::setSelectable);
		return changed ? diff : null;
	}
}
