package de.oglimmer.ggo.logic;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class Field {

	@Getter
	private String id;

	@Getter
	private Point pos;

	@Getter
	private Set<Field> neighbords = new HashSet<>();

	@Getter
	@Setter
	private Structure structure;

	@Getter
	@Setter
	private Unit unit;

	public Field(int x, int y) {
		pos = new Point(x, y);
		id = x + ":" + y;
	}

	private boolean adjacent(Field f) {
		return Math.abs(f.getPos().getX() - pos.getX()) < 2 && Math.abs(f.getPos().getY() - pos.getY()) < 2;
	}

	public void calcNeighbors(Set<Field> fields) {
		fields.stream().filter(f -> adjacent(f)).forEach(f -> neighbords.add(f));
	}

}
