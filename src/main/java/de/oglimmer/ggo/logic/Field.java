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

	private Game game;

	public Field(Game game, int x, int y) {
		this.game = game;
		pos = new Point(x, y);
		id = x + ":" + y;
	}

	public boolean isHighlighted(Player p) {
		return p.getGame().getCurrentPhase().isHighlighted(this, p);
	}

	public boolean isSelectable(Player player) {
		return player.getGame().getCurrentPhase().isSelectable(this, player);
	}

	private boolean adjacent(Field f) {
		return Math.abs(f.getPos().getX() - pos.getX()) < 2 && Math.abs(f.getPos().getY() - pos.getY()) < 2;
	}

	public void calcNeighbors(Set<Field> fields) {
		fields.stream().filter(f -> adjacent(f)).forEach(f -> neighbords.add(f));
	}

	@Override
	public String toString() {
		return "Field [id=" + id + ", pos=" + pos + ", structure=" + structure + ", unit=" + unit + ", highlighted="
				+ ", selectable=" + "]";
	}
}
