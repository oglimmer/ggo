package de.oglimmer.ggo.logic.phase.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CmdException extends RuntimeException {

    @Getter
    private Type Type;

    public enum Type {
        NO_UNIT_SELECTED, ERROR, NOT_ENOUGH_MONEY, UNKNOWN_UNIT, SELECTED_CARD_NOT_FROM_ACTIVE_PLAYER, WRONG_SELECTED_CARD, ILLEGAL_FIELD_TO_DEPLOY, FIELD_ALREADY_OCCUPIED

    }
}
