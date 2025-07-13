package de.oglimmer.ggo.logic.battle;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommandType {
    MOVE("M", "move"), FORTIFY("F", "fortify"), BOMBARD("B", "bombard"), SUPPORT("S", "support");

    private final String description;
    private final String longName;

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

    public static CommandType fromString(String text) {
        for (CommandType b : CommandType.values()) {
            if (b.description.equalsIgnoreCase(text) || b.longName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
