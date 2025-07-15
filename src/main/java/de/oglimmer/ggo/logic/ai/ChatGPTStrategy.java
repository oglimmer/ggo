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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.oglimmer.ggo.logic.Unit;

@Slf4j
@RequiredArgsConstructor
public class ChatGPTStrategy implements AiStrategy, Serializable {

    private static final String INSTRUCTIONS = """
            You are **AlphaBot**, an expert AI for the 10 × 10-hex strategy game “GridGameOne.”
            Follow every rule below exactly; never invent new actions or overlook restrictions.
            Unless a developer explicitly asks for natural language, reply only with the tool call or JSON they request.
            Do not reveal your private reasoning.
            
            ──────────────────────────────── RULES ────────────────────────────────
            OBJECTIVE
            • Score as many points as possible.
            • The Red player must capture the left-side cities.
            
            COMMAND TYPES
              – Fortify (No move; +1 DEF)
              – Move / Attack (Move into empty hex **or** attack enemy in target hex; cannot enter friendly-occupied hex; if units swap hexes they fight, otherwise no combat.) 
              – Support (Grant adjacent friendly +1 STR; both must stay adjacent.)
              – Bombard (Destroy enemy unit within range; only Helicopter [r = 1] or Artillery [r = 2])
            
            SCORING (apply at end of each turn)
            • Hold enemy city  +25
            • Kill via Move/Attack  +10
            • Kill via Bombard  +5
            
            UNITS 
            
            | Type        | STR | Abilities                                   | Cost |
            |-------------|-----|---------------------------------------------|------|
            | Infantry    | 1  | Support                                      | 100 |
            | Tank        | 2  | Support                                      | 250 |
            | Airborne    | 1  | Support; special deploy rule                 | 200 |
            | Helicopter  | 1  | Support; Bombard (r = 1)                     | 300 |
            | Artillery   | 0  | Bombard (r = 2); **cannot** Support          | 300 |
            
            ──────────────────────────────── END RULES ─────────────────────────────
            """;

    private static final String STRATEGY = """
            ──────────────────────────────── STRATEGY HINTS ────────────────────────────────
            Spend all credits.
            Buy Airborne if you have units around enemy cities. Buy 1 Artillery or Helicopter
            Spend the rest on Tanks, Infantry.
            
            Rush towards enemy cities.
            Defend your own cities.
            ──────────────────────────────── END STRATEGY HINTS ────────────────────────────────
            """;

    private final Player player;

    private final Game game;

    private String createMessageInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"playerRole\":\"red\",");
        sb.append("\"gameState\":{");
        sb.append("\"fields\":[");

        boolean firstField = true;
        for (Field f : game.getBoard().getFields()) {
            if (!firstField) {
                sb.append(",");
            }
            firstField = false;

            sb.append("{");
            sb.append("\"x\":").append(f.getPos().x).append(",");
            sb.append("\"y\":").append(f.getPos().y).append(",");

            if (f.getUnit() != null && f.getUnit().getPlayer() == player) {
                sb.append("\"occupiedBy\":\"you\",");
                sb.append("\"unitType\":\"").append(f.getUnit().getUnitType()).append("\",");
            } else if (f.getUnit() != null) {
                sb.append("\"occupiedBy\":\"enemy\",");
                sb.append("\"unitType\":\"").append(f.getUnit().getUnitType()).append("\",");
            } else {
                sb.append("\"occupiedBy\":\"none\",");
            }

            sb.append("\"neighbors\":[");
            boolean firstNeighbor = true;
            for (Field n : f.getNeighbors()) {
                if (!firstNeighbor) {
                    sb.append(",");
                }
                firstNeighbor = false;
                sb.append("{\"x\":").append(n.getPos().x).append(",\"y\":").append(n.getPos().y).append("}");
            }
            sb.append("]");
            sb.append("}");
        }

        sb.append("]");
        sb.append("}");
        sb.append("}");

        return sb.toString();
    }

    private ChatCompletionCreateParams.Builder builder(String msg) {
        return ChatCompletionCreateParams.builder()
                .addSystemMessage(INSTRUCTIONS + "\n" + STRATEGY + "\n" + createMessageInformation())
                .addUserMessage(msg)
                .model(ChatModel.GPT_4O_MINI);
    }

    @Override
    public void draft() {
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();
        DraftPhase draftPhase = (DraftPhase) game.getCurrentPhase();
        boolean success = false;
        while (!success) {
            try {
                StringBuilder sb = new StringBuilder();

                int credits = player.getCredits();
                sb.append("You have ").append(credits).append(" credits.");
                sb.append("""
                        ### Draft selection (unit types only)
                        
                        Consult your internal strategy guide and decide which **unit types** you will purchase this turn.
                        
                        Allowed values (lower-case):
                          "infantry", "tank", "airborne", "helicopter", "artillery"
                        
                        Respond with **nothing but** a JSON array of strings in the exact form shown below—no Markdown, no keys, no commentary.
                        """);

                log.debug("ChatGPT input: {}", sb.toString());

                StructuredChatCompletionCreateParams<DraftList> params = builder(sb.toString()).responseFormat(DraftList.class).build();

                client.chat().completions().create(params).choices().stream().flatMap(choice -> choice.message().content().stream()).flatMap(e -> e.unitsToDraft.stream()).forEach(unit -> {
                    UnitType ut = UnitType.getUnitType(unit);
                    if (ut != null) {
                        log.debug("Drafting unit: {}", unit);
                        draftPhase.draftUnit(player, ut);
                    } else {
                        log.warn("Unknown unit type: {}", unit);
                        throw new CmdException(CmdException.Type.UNKNOWN_UNIT);
                    }
                });
                success = true;
            } catch (CmdException e) {
                log.warn("Command failed: {}", e.getMessage());
            }
        }

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
                sb.append("[");
                sb.append(player.getUnitInHand().stream().map(Unit::getUnitType).map(UnitType::toString).collect(Collectors.joining(",")));
                sb.append("]");
                String s = """
                        ### Deploy one unit
                        
                        You still have these undeployed units in hand:
                        """;
                s += sb.toString();
                s += """
                        
                        Choose **exactly one** of them and place it on the map.
                        
                        Placement rules
                        • Target field must be empty.
                        • For infantry or tank or helicopter or artillery: pick a field on the **right half** of the 10×10 board with targetFieldX = 5,6,7,8,9.
                        • If the chosen unit were “airborne”, you could instead pick any empty field adjacent to one of your own units, but that does **not** apply here.
                        • Consult your strategy guide—select a field that is tactically valuable this turn.
                        
                        Respond with **nothing but** the JSON object shown below—no Markdown, no extra keys, no commentary:
                        
                        ```json
                        {
                          "unitType": "<one of: infantry | tank | helicopter | artillery | airborne>",
                          "targetFieldX":  "<target x, e.g. \\"7\\">",
                          "targetFieldY":  "<target y, e.g. \\"3\\">"
                        }
                        """;

                log.debug("ChatGPT input: {}", s);

                StructuredChatCompletionCreateParams<DeployDecision> params = builder(sb.toString()).responseFormat(DeployDecision.class).build();

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
                                    if (!deployPhase.isSelectable(toDeployField, player)) {
                                        throw new CmdException(CmdException.Type.ILLEGAL_FIELD_TO_DEPLOY);
                                    }
                                    log.debug("Deploying unit to field: {}", toDeployField.getId());
                                    deployPhase.execCmd(player, "selectTargetField", toDeployField.getId());
                                });
                            } else {
                                log.warn("Unknown unit type: {}", deployDecision.unitType);
                                throw new CmdException(CmdException.Type.UNKNOWN_UNIT);
                            }
                        });
                success = true;
            } catch (CmdException e) {
                log.warn("Command failed: {}", e.getMessage());
            }
        }
    }

    private static boolean hasConflictingMoves(List<CombatPhaseDecisionDecoded> decisions) {
        return decisions.stream().filter(d -> d.getCommand() == CommandType.MOVE).map(CombatPhaseDecisionDecoded::getTargetField).collect(Collectors.groupingBy(f -> f, Collectors.counting())).values().stream().anyMatch(count -> count > 1);
    }

    private static boolean areSupportsValid(List<CombatPhaseDecisionDecoded> decisions) {
        Map<Field, Field> finalPositions = decisions.stream().filter(d -> d.getCommand() == CommandType.MOVE).collect(Collectors.toMap(CombatPhaseDecisionDecoded::getSourceField, CombatPhaseDecisionDecoded::getTargetField));

        return decisions.stream().filter(d -> d.getCommand() == CommandType.SUPPORT).allMatch(support -> {
            Field supporterFinalPos = finalPositions.getOrDefault(support.getSourceField(), support.getSourceField());
            Field targetFinalPos = finalPositions.getOrDefault(support.getTargetField(), support.getTargetField());
            return supporterFinalPos.getNeighbors().contains(targetFinalPos);
        });
    }

    @Override
    public void command() {
        CombatCommandPhase combatCommandPhase = (CombatCommandPhase) game.getCurrentPhase();
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        List<Unit> playerUnits = game.getBoard().getFields().stream()
                .filter(f -> f.getUnit() != null && f.getUnit().getPlayer() == player)
                .map(Field::getUnit)
                .toList();


        boolean success = false;
        while (!success) {
            try {
                int roundIndex = combatCommandPhase.getCombatPhaseRoundCounter().getCurrentRound();
                List<CombatPhaseDecisionDecoded> list = new ArrayList<>();
                for (Unit u : playerUnits) {
                    StringBuilder sb = new StringBuilder()
                            .append("### Command phase – round ").append(roundIndex).append('\n')
                            .append("Unit: ").append(u.getUnitType().name().toLowerCase())
                            .append(" with current field: ").append(u.getDeployedOn().asPosString()).append('\n')
                            .append('\n')
                            .append("Legal actions this round:\n");

                    if (u.getUnitType() == UnitType.ARTILLERY) {
                        String legalBombardTargets = u.getTargetableFields().stream()
                                .map(Field::asPosString)
                                .collect(Collectors.joining(", "));
                        sb.append("• bombard (range ≤ 2)  – targets: ").append(legalBombardTargets).append('\n');
                    }
                    if (u.getUnitType() == UnitType.HELICOPTER) {
                        String legalBombardTargets = u.getTargetableFields().stream()
                                .map(Field::asPosString)
                                .collect(Collectors.joining(", "));
                        sb.append("• bombard (range ≤ 1)  – targets: ").append(legalBombardTargets).append('\n');
                    }

                    String legalMoveHexes = u.getDeployedOn().getNeighbors().stream()
                            .filter(f -> f.getUnit() == null)
                            .map(Field::asPosString)
                            .collect(Collectors.joining(", "));
                    String legalAttackTargets = u.getDeployedOn().getNeighbors().stream()
                            .filter(f -> f.getUnit() != null && f.getUnit().getPlayer() != player)
                            .map(Field::asPosString)
                            .collect(Collectors.joining(", "));
                    String legalSupportTargets = u.getDeployedOn().getNeighbors().stream()
                            .filter(f -> f.getUnit() != null && f.getUnit().getPlayer() == player)
                            .map(Field::asPosString)
                            .collect(Collectors.joining(", "));
                    sb.append("• move  – empty adjacent fields: ").append(legalMoveHexes).append('\n')
                            .append("• move – attacks an enemy units in adjacent fields: ").append(legalAttackTargets).append('\n')
                            .append("• support – adjacent friendlies: ").append(legalSupportTargets).append('\n')
                            .append("• fortify – remain in place (+1 DEF)\n\n")
                            .append("Respond with **only** this JSON object – no markdown, no comments:\n")
                            .append("{\n")
                            .append("  \"command\": \"<fortify | move | support | bombard>\",\n")
                            .append("  \"targetFieldX\" : \"<x value of field>\",\n")
                            .append("  \"targetFieldY\" : \"<y value of field>\"\n")
                            .append("}");

                    log.debug("ChatGPT input: {}", sb);

                    StructuredChatCompletionCreateParams<CombatPhaseDecision> params = builder(sb.toString())
                            .responseFormat(CombatPhaseDecision.class).build();

                    client.chat().completions().create(params).choices().stream()
                            .flatMap(choice -> choice.message().content().stream())
                            .forEach(combatDecision -> {
                                log.debug("Executing command: {} ", combatDecision);
                                CommandType commandType = CommandType.fromString(combatDecision.command);
                                if (commandType != CommandType.FORTIFY) {
                                    int targetFieldX = combatDecision.targetFieldX;
                                    int targetFieldY = combatDecision.targetFieldY;
                                    Field sourceField = u.getDeployedOn();
                                    Field targetField = game.getBoard().getField(new Point(targetFieldX, targetFieldY));
                                    log.debug("Executing command: {} from field {} to field {}", commandType, sourceField, targetField);
                                    CombatPhaseDecisionDecoded decoded = new CombatPhaseDecisionDecoded();
                                    decoded.setCommand(commandType);
                                    decoded.setSourceField(sourceField);
                                    decoded.setTargetField(targetField);
                                    list.add(decoded);
                                }
                            });
                }

                // Check if multiple units are trying to move to the same field
                if (hasConflictingMoves(list)) {
                    throw new CmdException(CmdException.Type.ERROR);
                }

                // Validate move ranges
                list.stream().filter(d -> d.getCommand() == CommandType.MOVE).forEach(move -> {
                    if (!move.getSourceField().getNeighbors().contains(move.getTargetField())) {
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
    public static class DraftList {
        @ArraySchema(maxItems = 100)
        private List<String> unitsToDraft;
    }
}