package de.oglimmer.ggo.ui;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Player;
import lombok.Getter;
import lombok.ToString;

@ToString
public class UIField {

	// IMMUTABLE

	@Getter
	private String id;
	@Getter
	private Integer x;
	@Getter
	private Integer y;

	// CHANGABLE

	@Getter
	private Boolean highlight;
	@Getter
	private Boolean selectable;

	public UIField(Field f, Player player) {
		this.id = f.getId();
		this.x = f.getPos().x;
		this.y = f.getPos().y;
		this.highlight = f.isHighlighted(player);
		this.selectable = f.isSelectable(player);
	}
}
