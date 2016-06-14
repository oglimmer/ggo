package de.oglimmer.ggo.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;

public class Board {

	@Getter
	private Set<Field> fields = new HashSet<>();

	public Board(List<Player> players) {
		init(players);
	}

	private void init(List<Player> players) {
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				Field newField = new Field(players.get(0).getGame(), x, y);
				fields.add(newField);
			}
		}
		fields.stream().forEach(f -> f.calcNeighbors(fields));
		getField(0, 0).setStructure(new Structure(Structure.CITY, players.get(0)));
		getField(0, 4).setStructure(new Structure(Structure.CITY, players.get(0)));
		getField(0, 8).setStructure(new Structure(Structure.CITY, players.get(0)));
		getField(9, 1).setStructure(new Structure(Structure.CITY, players.get(1)));
		getField(9, 5).setStructure(new Structure(Structure.CITY, players.get(1)));
		getField(9, 9).setStructure(new Structure(Structure.CITY, players.get(1)));
	}

	private Field getField(int x, int y) {
		Optional<Field> opt = fields.stream().filter(f -> f.getPos().getX() == x && f.getPos().getY() == y).findFirst();
		return opt.isPresent() ? opt.get() : null;
	}

	public Field getField(String id) {
		Optional<Field> opt = fields.stream().filter(f -> f.getId().equals(id)).findFirst();
		return opt.isPresent() ? opt.get() : null;
	}

}
