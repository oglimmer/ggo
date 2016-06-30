package de.oglimmer.ggo.logic;

public enum StructureType {
	CITY("city");

	private String name;

	private StructureType(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
