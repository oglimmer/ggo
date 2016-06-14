package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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

	public void copy(Structure structure, String color, int x, int y) {
		this.id = structure.getId();
		this.color = color;
		this.type = structure.getType();
		this.x = x;
		this.y = y;
		this.selectable = false;
	}

	public void copy(Unit unit, String color, int x, int y) {
		this.id = unit.getId();
		this.color = color;
		this.type = unit.getType().toString();
		this.x = x;
		this.y = y;
		this.selectable = false;
	}

	public UIUnit diffAndUpdate(int latestX, int latestY) {
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

		this.selectable = false;

		return changed ? diff : null;
	}

}
