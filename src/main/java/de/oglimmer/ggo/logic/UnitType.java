package de.oglimmer.ggo.logic;

import lombok.Getter;

public enum UnitType {

	INFANTERY("infantry", 1, true), TANK("tank", 2, true), AIRBORNE("airborne", 1, true), HELICOPTER("helicopter", 1,
			true), ARTILLERY("artillery", 0, false);

	private String name;

	@Getter
	private int strength;

	@Getter
	private boolean support;

	private UnitType(String name, int strength, boolean support) {
		this.name = name;
		this.strength = strength;
		this.support = support;
	}

	public String toString() {
		return name;
	}

}
