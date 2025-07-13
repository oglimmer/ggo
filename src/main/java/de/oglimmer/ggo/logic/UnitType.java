package de.oglimmer.ggo.logic;

import lombok.Getter;

public enum UnitType {

    INFANTERY("infantry", 1, true, 100), TANK("tank", 2, true, 250), AIRBORNE("airborne", 1, true,
            200), HELICOPTER("helicopter", 1, true, 300), ARTILLERY("artillery", 0, false, 300);

    private String name;

    @Getter
    private int strength;

    @Getter
    private boolean support;

    @Getter
    private int cost;

    private UnitType(String name, int strength, boolean support, int cost) {
        this.name = name;
        this.strength = strength;
        this.support = support;
        this.cost = cost;
    }

    public String toString() {
        return name;
    }

    public static UnitType getUnitType(String name) {
        for (UnitType ut : UnitType.values()) {
            if (ut.name.equalsIgnoreCase(name)) {
                return ut;
            }
        }
        return null;
    }

}
