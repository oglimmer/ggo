package de.oglimmer.ggo.logic;

import java.awt.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.oglimmer.ggo.logic.util.FieldUtil;
import lombok.Getter;
import lombok.Setter;

public class Field {

	@Getter
	private String id;

	private Point pos;

	private Set<Field> neighbors = new HashSet<>();

	@Getter
	@Setter
	private Structure structure;

	@Getter
	@Setter
	private Unit unit;

	public Field(int x, int y) {
		this.pos = new Point(x, y);
		this.id = x + ":" + y;
	}

	public boolean isHighlighted(Player p) {
		return p.getGame().getCurrentPhase().isHighlighted(this, p);
	}

	public boolean isSelectable(Player player) {
		return player.getGame().getCurrentPhase().isSelectable(this, player);
	}

	public void calcNeighbors(Set<Field> fields) {
		fields.stream().filter(f -> FieldUtil.adjacent(this, f)).forEach(f -> neighbors.add(f));
	}

	public Point getPos() {
		return new Point(pos);
	}

	public Set<Field> getNeighbors() {
		return Collections.unmodifiableSet(this.neighbors);
	}

	@Override
	public String toString() {
		return "Field [id=" + id + ", structure=" + structure + ", unit=" + unit + "]";
	}
}
