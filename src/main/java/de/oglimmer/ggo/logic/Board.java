package de.oglimmer.ggo.logic;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;

public class Board {

	@Getter
	private Set<Field> fields = new HashSet<>();

	public Board() {
		init();
	}

	private void init() {
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				Field newField = new Field(x, y);
				fields.add(newField);
			}
		}
		fields.stream().forEach(f -> f.calcNeighbors(fields));
	}

	public void addCities(List<Player> players) {
		if (players.size() > 0) {
			getField(0, 0).setStructure(new Structure(StructureType.CITY, players.get(0)));
			getField(0, 4).setStructure(new Structure(StructureType.CITY, players.get(0)));
			getField(0, 8).setStructure(new Structure(StructureType.CITY, players.get(0)));
			getField(9, 1).setStructure(new Structure(StructureType.CITY, players.get(1)));
			getField(9, 5).setStructure(new Structure(StructureType.CITY, players.get(1)));
			getField(9, 9).setStructure(new Structure(StructureType.CITY, players.get(1)));
		}
	}

	private Field getField(int x, int y) {
		Optional<Field> opt = fields.stream().filter(f -> f.getPos().getX() == x && f.getPos().getY() == y).findFirst();
		return opt.isPresent() ? opt.get() : null;
	}

	public Field getField(String id) {
		Optional<Field> opt = fields.stream().filter(f -> f.getId().equals(id)).findFirst();
		return opt.isPresent() ? opt.get() : null;
	}

	public Field getField(Point pos) {
		Optional<Field> opt = fields.stream().filter(f -> f.getPos().equals(pos)).findFirst();
		return opt.isPresent() ? opt.get() : null;
	}

	public Unit getUnitById(String id) {
		return fields.stream().filter(f -> f.getUnit() != null).filter(f -> f.getUnit().getId().equals(id))
				.map(f -> f.getUnit()).findFirst().get();
	}

	public long getTotalUnits() {
		return fields.stream().filter(f -> f.getUnit() != null).count();
	}

}
