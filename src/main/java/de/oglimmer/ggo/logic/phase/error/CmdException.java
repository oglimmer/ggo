package de.oglimmer.ggo.logic.phase.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CmdException extends RuntimeException {

    @Getter
    private Type Type;

    public enum Type {
        NO_UNIT_SELECTED, ERROR, FIELD_ALREADY_OCCUPIED

    }
}
