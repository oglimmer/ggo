package de.oglimmer.ggo.logic;

public enum Side {
	RED("red"), GREEN("green");

	private String name;

	private Side(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
