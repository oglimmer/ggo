package de.oglimmer.ggo.logic.battle;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Unit;
import lombok.NonNull;

public record Command(@NonNull CommandType commandType, @NonNull Unit unit, @NonNull Field targetField) {
}