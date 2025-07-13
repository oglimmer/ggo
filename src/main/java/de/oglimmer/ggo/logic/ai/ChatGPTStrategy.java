package de.oglimmer.ggo.logic.ai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonSchemaLocalValidation;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.UnitType;
import de.oglimmer.ggo.logic.battle.CommandType;
import de.oglimmer.ggo.logic.phase.CombatCommandPhase;
import de.oglimmer.ggo.logic.phase.DeployPhase;
import de.oglimmer.ggo.logic.phase.DraftPhase;
import de.oglimmer.ggo.logic.phase.error.CmdException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ChatGPTStrategy implements AiStrategy, Serializable {

    private static final String INSTRUCTIONS = """
            Instructions of the game
            Goal of the game: The player who earns more points within 5 turns (with each 3 phases) wins the game.
            The game is played on a 10 by 10 hex field map. The green player has to conquer the right side of the field. The red player has to conquer the left side of the field.
            Phases: Each turn has 3 phases: draft, deploy and combat/move/support/bombard.
            Draft phase: Each player drafts units for credits. Each round a player gets 1000 additional credits. Credits you don't spend this turn, will be kept for next turn. A unit is drafted by clicking on its icon at the bottom. If you want to undo the draft click on the unit in your hand. Drafting is done for both players in parallel. The deploy phase is started by the player having more credits left, or at a tie the player with less units, or at a tie by random.
            Deploy phase: Each player deploys one unit at a time. After a player has deployed a unit, the other player will do so. Each player must deploy all units in his hand. A unit can only be deployed on the player's side of the board, unless a player deploys an Airborne unit. Airborne units can be deployed anywhere on the player's side or next to an existing unit which was on the board at the turn's start.
            combat/move/support/bombard phase: Each combat/move/support/bombard phase has 3 rounds. For each round a player can give each unit one command. When all units have the desired command press the 'done' button to see all commands issues by your and your opponent. To let the computer carry out the move/attacks press done again. All movements and attacks are then executed in parallel.
            Command: Fortify: This is the default command. A "F" on a unit shows that the unit's command is fortify. The unit will not move, support another or bombard, but get +1 (defense-)strength.
            Command: Move / attack: A unit can only move to a field where no other friendly unit is located or commanded. If a unit finds an enemy unit on this field, both units will fight and only one unit will be left. Units will also fight if they move across each other. Units will not fight if a unit moves to a field where a unit has been, but this unit moved into another field. A red arrow indicates a move command.
            Command: Support: A unit can support another friendly and adjacent unit. The other unit gets +1 strength. A yellow arrow indicates a support command. Note: A unit getting support from another unit cannot move into a field which is not adjacent to the supporting unit.
            Command: Bombard: If a unit bombards a unit, this unit will be removed. A green arrow indicates a move command.
            Scoring: For each enemy city you occupy at the of a turn, you get 25 points. For each kill you do while a moving attack you get 10 points. For each bombardment you get 5 points.
            Infantry: Strength 1 Has support ability, Cost: 100
            Tank: Strength 2 Has support ability, Cost: 250
            Airborne forces: Strength 1 Has support ability and can be deployed into the enemy side of the board, Cost: 200
            Helicopter: Strength 1 Has support ability and can bombard fields within 1 field range, Cost: 300
            Artillery: Strength 0 Doesn't have support ability, but can bombard fields within 2 fields range, Cost: 300
            """;

    private static final String STRATEGY = """
            FOLLOW THE STRATEGY: DRAFT
            Spend ≥ 80 % credits; carry ≤ 20 %.
            Per 1000 credits: 2 Tanks, 3 Infantry, 1 Airborne (odd turns), 1 Artillery or Helicopter (even turns).
            
            FOLLOW THE STRATEGY: DEPLOY
            First Airborne adjacent enemy cities on left edge.
            Tanks rows 4-7 behind Infantry.
            Infantry front line adjacent Tanks.
            Artillery two hexes behind front.
            Helicopters one hex behind Tanks.
            
            FOLLOW THE STRATEGY: COMBAT (three rounds)
            Round 1 – Advance Airborne/Infantry; bombard heavy units; move Tanks up.
            Round 2 – Tanks attack with adjacent support; Airborne capture cities; Artillery bombard next targets.
            Round 3 – Fortify Infantry in cities; Tanks support or flank; Artillery/Helicopters bombard remaining threats.
            
            FOLLOW THE STRATEGY: COMMAND RULES
            Each attacking Tank has one supporter.
            Bombard before moving when possible.
            Fortify city-holding Infantry.
            Avoid contested hexes.
            
            SCORING PRIORITY
            1 Hold enemy cities at turn end.
            2 Tank attack kills.
            3 Bombard kills.
            """;

    private final Player player;

    private final Game game;

    private String createMessageInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are the red player, the current game state is: \n");
        game.getBoard().getFields().iterator().forEachRemaining(f -> {
            sb.append("Field ").append(f.getPos().x).append(":").append(f.getPos().y);
            if (f.getUnit() != null && f.getUnit().getPlayer() == player) {
                sb.append(" occupied by you with ").append(f.getUnit().getUnitType());
            } else if (f.getUnit() != null) {
                sb.append(" occupied by the enemy with ").append(f.getUnit().getUnitType());
            } else {
                sb.append(" is unoccupied ");
            }
            sb.append(" and neighbors:[");
            f.getNeighbors().forEach(n -> {
                sb.append(n.getPos().x).append(":").append(n.getPos().y).append(",");
            });
            sb.append("]").append("\n");
        });
        sb.append("\nYour decision must be confirm to the game rules and the current game state at all cost.\n");
        return sb.toString();
    }

    private ChatCompletionCreateParams.Builder builder(StringBuilder sb) {
        return ChatCompletionCreateParams.builder()
                .addSystemMessage(INSTRUCTIONS)
                .addAssistantMessage(createMessageInformation() + "\n" + STRATEGY)
                .addUserMessage(sb.toString())
                .model(ChatModel.GPT_4_1_MINI);
    }

    @Override
    public void draft() {
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();
        DraftPhase draftPhase = (DraftPhase) game.getCurrentPhase();

        StringBuilder sb = new StringBuilder();

        int credits = player.getCredits();
        sb.append("You have ").append(credits).append(" credits.").append("\n\n");

        sb.append("Tell me which units you want to draft! Spend all the credits. Options are: infantry,tank,airborne,helicopter,artillery. Answer with a JSON string array and nothing else.");

        log.debug("ChatGPT input: {}", sb.toString());

        StructuredChatCompletionCreateParams<DraftList> params = builder(sb)
                .responseFormat(DraftList.class, JsonSchemaLocalValidation.NO)
                .build();

        client.chat().completions().create(params).choices().stream()
                .flatMap(choice -> choice.message().content().stream())
                .flatMap(e -> e.unitsToDraft.stream())
                .forEach(unit -> {
                    UnitType ut = UnitType.getUnitType(unit);
                    if (ut != null) {
                        log.debug("Drafting unit: {}", unit);
                        draftPhase.draftUnit(player, ut);
                    } else {
                        log.warn("Unknown unit type: {}", unit);
                    }
                });

        draftPhase.playerDone(player);
    }

    @Override
    public void deploy() {
        DeployPhase deployPhase = (DeployPhase) game.getCurrentPhase();
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        boolean success = false;
        while (!success) {
            try {
                StringBuilder sb = new StringBuilder();

                sb.append("Your hand units are:");
                player.getUnitInHand().forEach(u -> {
                    sb.append(u.getUnitType()).append(",");
                });
                sb.append("\n\n");

                sb.append("Tell me which one unit you want to deploy where! Your side of the field is the right side, columns 5 to 9. Pick one of your hand units and select an unoccupied but strategically important field. Answer with a JSON and nothing else.");

                log.debug("ChatGPT input: {}", sb.toString());

                StructuredChatCompletionCreateParams<DeployDecision> params = builder(sb)
                        .responseFormat(DeployDecision.class, JsonSchemaLocalValidation.NO)
                        .build();

                client.chat().completions().create(params).choices().stream()
                        .flatMap(choice -> choice.message().content().stream())
                        .forEach(deployDecision -> {
                            UnitType ut = UnitType.getUnitType(deployDecision.unitType);
                            if (ut != null) {
                                player.getUnitInHand().stream().filter(u -> u.getUnitType() == ut).findFirst().ifPresent(u -> {
                                    log.debug("Deploying unit: {}", u.getUnitType());
                                    deployPhase.execCmd(player, "selectHandCard", u.getId());
                                    int x = deployDecision.targetFieldX;
                                    int y = deployDecision.targetFieldY;
                                    Field toDeployField = game.getBoard().getField(new Point(x, y));
                                    log.debug("Deploying unit to field: {}", toDeployField.getId());
                                    deployPhase.execCmd(player, "selectTargetField", toDeployField.getId());
                                });
                            } else {
                                log.warn("Unknown unit type: {}", deployDecision.unitType);
                            }
                        });
                success = true;
            } catch (CmdException e) {
                log.debug("Command failed: {}", e.getMessage());
            }
        }
    }

    private static boolean hasConflictingMoves(List<CombatPhaseDecisionDecoded> decisions) {
        return decisions.stream()
                .filter(d -> d.getCommand() == CommandType.MOVE)
                .map(CombatPhaseDecisionDecoded::getTargetField)
                .collect(Collectors.groupingBy(f -> f, Collectors.counting()))
                .values().stream()
                .anyMatch(count -> count > 1);
    }

    private static boolean areSupportsValid(List<CombatPhaseDecisionDecoded> decisions) {
        Map<Field, Field> finalPositions = decisions.stream()
                .filter(d -> d.getCommand() == CommandType.MOVE)
                .collect(Collectors.toMap(
                        CombatPhaseDecisionDecoded::getSourceField,
                        CombatPhaseDecisionDecoded::getTargetField
                ));

        return decisions.stream()
                .filter(d -> d.getCommand() == CommandType.SUPPORT)
                .allMatch(support -> {
                    Field supporterFinalPos = finalPositions.getOrDefault(support.getSourceField(), support.getSourceField());
                    Field targetFinalPos = finalPositions.getOrDefault(support.getTargetField(), support.getTargetField());
                    return supporterFinalPos.getNeighbors().contains(targetFinalPos);
                });
    }

    @Override
    public void command() {
        CombatCommandPhase combatCommandPhase = (CombatCommandPhase) game.getCurrentPhase();
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        boolean success = false;
        while (!success) {
            try {
                StringBuilder sb = new StringBuilder();

                sb.append("Make the decisions for the combat, move, support, fortify phase! For each of your units on the battle field, you can either do nothing (fortify) or move to on unoccupied field or move to a field occupied by an enemy to combat this unit or support one of your own units. Valid commands are move,bombard,support. Answer with a JSON and nothing else.");

                log.debug("ChatGPT input: {}", sb.toString());

                StructuredChatCompletionCreateParams<CombatPhaseDecisionsList> params = builder(sb)
                        .responseFormat(CombatPhaseDecisionsList.class, JsonSchemaLocalValidation.NO)
                        .build();

                List<CombatPhaseDecisionDecoded> list = client.chat().completions().create(params).choices().stream()
                        .flatMap(choice -> choice.message().content().stream())
                        .flatMap(e -> e.decisions.stream())
                        .map(combatDecision -> {
                            int targetFieldX = combatDecision.targetFieldX;
                            int targetFieldY = combatDecision.targetFieldY;
                            int sourceFieldX = combatDecision.sourceFieldX;
                            int sourceFieldY = combatDecision.sourceFieldY;
                            log.debug("Executing command: {} ", combatDecision);
                            CommandType commandType = CommandType.fromString(combatDecision.command);
                            Field sourceField = game.getBoard().getField(new Point(sourceFieldX, sourceFieldY));
                            Field targetField = game.getBoard().getField(new Point(targetFieldX, targetFieldY));
                            log.debug("Executing command: {} from field {} to field {}", commandType, sourceField, targetField);
                            CombatPhaseDecisionDecoded decoded = new CombatPhaseDecisionDecoded();
                            decoded.setCommand(commandType);
                            decoded.setSourceField(sourceField);
                            decoded.setTargetField(targetField);
                            return decoded;
                        }).toList();

                // Check if multiple units are trying to move to the same field
                if (hasConflictingMoves(list)) {
                    throw new CmdException(CmdException.Type.ERROR);
                }

                // Validate move ranges
                list.stream()
                        .filter(d -> d.getCommand() == CommandType.MOVE)
                        .forEach(move -> {
                            if (move.getSourceField().getUnit().getUnitType() == UnitType.HELICOPTER) {
                                if (move.getSourceField().getNeighbors().stream()
                                        .flatMap(f -> f.getNeighbors().stream())
                                        .noneMatch(f -> f == move.getTargetField())) {
                                    throw new CmdException(CmdException.Type.ERROR);
                                }
                            } else if (!move.getSourceField().getNeighbors().contains(move.getTargetField())) {
                                throw new CmdException(CmdException.Type.ERROR);
                            }
                        });

                // Check if supporting units will be neighbors after movement
                if (!areSupportsValid(list)) {
                    throw new CmdException(CmdException.Type.ERROR);
                }

                list.forEach(u -> {

                    if (u.getSourceField().getUnit() == null || u.getSourceField().getUnit().getPlayer() != player) {
                        throw new CmdException(CmdException.Type.ERROR);
                    }
                    if (u.getCommand() == CommandType.SUPPORT && (u.getTargetField().getUnit() == null || u.getTargetField().getUnit().getPlayer() != player)) {
                        throw new CmdException(CmdException.Type.ERROR);
                    }
                    if (u.getCommand() == CommandType.BOMBARD && u.getTargetField().getUnit() != null && u.getTargetField().getUnit().getPlayer() == player) {
                        throw new CmdException(CmdException.Type.ERROR);
                    }

                    combatCommandPhase.getCc().addCommand(u.getSourceField().getUnit(), u.getTargetField(), u.getCommand());
                });
                success = true;
            } catch (CmdException e) {
                log.debug("Command failed: {}", e.getMessage());
            }
        }
        combatCommandPhase.execDoneButton(player);
    }

    @ToString
    @Getter
    @Setter
    public static class DeployDecision {
        private String unitType;
        private Integer targetFieldX;
        private Integer targetFieldY;
    }

    @ToString
    @Getter
    @Setter
    public static class CombatPhaseDecision {
        private String command;
        private Integer sourceFieldX;
        private Integer sourceFieldY;
        private Integer targetFieldX;
        private Integer targetFieldY;
    }

    @ToString
    @Getter
    @Setter
    public static class CombatPhaseDecisionDecoded {
        private CommandType command;
        private Field sourceField;
        private Field targetField;
    }

    @ToString
    @Getter
    @Setter
    public static class CombatPhaseDecisionsList {
        @ArraySchema(maxItems = 100)
        private List<CombatPhaseDecision> decisions;
    }


    @ToString
    @Getter
    @Setter
    public static class DraftList {
        @ArraySchema(maxItems = 100)
        private List<String> unitsToDraft;
    }
}