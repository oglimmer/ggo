package de.oglimmer.ggo.logic.battle;

public enum CommandType {
	MOVE("M"), FORTIFY("F"), BOMBARD("B"), SUPPORT("S");

	private String description;

	CommandType(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public boolean isMove() {
		return this == MOVE;
	}

	public boolean isFortify() {
		return this == FORTIFY;
	}

	public boolean isBombard() {
		return this == BOMBARD;
	}

	public boolean isSupport() {
		return this == SUPPORT;
	}

}
