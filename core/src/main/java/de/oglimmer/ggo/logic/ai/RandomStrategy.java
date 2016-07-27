package de.oglimmer.ggo.logic.ai;

import java.awt.Point;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Unit;
import de.oglimmer.ggo.logic.UnitType;
import de.oglimmer.ggo.logic.battle.CommandType;
import de.oglimmer.ggo.logic.phase.CombatCommandPhase;
import de.oglimmer.ggo.logic.phase.DeployPhase;
import de.oglimmer.ggo.logic.phase.DraftPhase;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RandomStrategy implements AiStrategy {

	private Player player;

	private Game game;

	@Override
	public void draft() {
		DraftPhase draftPhase = (DraftPhase) game.getCurrentPhase();
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.draftUnit(player, UnitType.INFANTERY);
		draftPhase.playerDone(player);
	}

	@Override
	public void deploy() {
		Unit toDeploy = player.getUnitInHand().get(0);
		DeployPhase dp = (DeployPhase) game.getCurrentPhase();
		Field toDeployField = dp.getDeployFields(player).iterator().next();

		dp.execCmd(player, "selectHandCard", toDeploy.getId());
		dp.execCmd(player, "selectTargetField", toDeployField.getId());
	}

	@Override
	public void command() {
		CombatCommandPhase combatCommandPhase = (CombatCommandPhase) game.getCurrentPhase();
		game.getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.filter(f -> f.getUnit().getPlayer() == player).map(f -> f.getUnit()).forEach(u -> {

					Point pos = u.getDeployedOn().getPos();
					String targetFieldId = null;
					if (pos.x == 1 && pos.y == 2) {
						targetFieldId = "0:1";
					} else if (pos.x == 0 && pos.y == 1) {
						targetFieldId = "0:0";
					} else if (pos.x == 0 && pos.y == 3) {
						targetFieldId = "0:4";
					} else if (pos.x == 0 && pos.y == 5) {
						targetFieldId = "0:4";
					} else if (pos.x == 1 && pos.y == 6) {
						targetFieldId = "0:5";
					} else if (pos.x == 0 && pos.y == 7) {
						targetFieldId = "0:8";
					} else if (pos.x == 0 && pos.y == 9) {
						targetFieldId = "0:8";
					} else if (pos.x > 0) {
						targetFieldId = pos.x - 1 + ":" + pos.y;
					}
					if (targetFieldId != null) {
						Field targetField = game.getBoard().getField(targetFieldId);
						if (combatCommandPhase.getCc().getByTargetField(player, targetField).isEmpty()) {
							combatCommandPhase.getCc().addCommand(u, targetField, CommandType.MOVE);
						}
					}

				});
		combatCommandPhase.execDoneButton(player);
	}

}
