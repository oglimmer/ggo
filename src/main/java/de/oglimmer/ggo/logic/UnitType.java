package de.oglimmer.ggo.logic;

public enum UnitType {

	INFANTERY("infantry"), TANK("tank"), AIRBORNE("airborne"), HELICOPTER("helicopter"), ARTILLERY("artillery");

	private String name;

	private UnitType(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
