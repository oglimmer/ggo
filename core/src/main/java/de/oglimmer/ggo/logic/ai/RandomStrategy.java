package de.oglimmer.ggo.logic.ai;

import java.awt.Point;
import java.util.Optional;

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
		int cheaptestUnit = UnitType.INFANTERY.getCost();
		while (player.getCredits() > cheaptestUnit) {
			UnitType ut = null;
			double rnd = Math.random();
			if (rnd < 0.2 && player.getCredits() >= UnitType.ARTILLERY.getCost()) {
				ut = UnitType.ARTILLERY;
			} else if (rnd < 0.4 && player.getCredits() >= UnitType.HELICOPTER.getCost()) {
				ut = UnitType.HELICOPTER;
			} else if (rnd < 0.5 && player.getCredits() >= UnitType.AIRBORNE.getCost()) {
				ut = UnitType.AIRBORNE;
			} else if (rnd < 1 && player.getCredits() >= UnitType.TANK.getCost()) {
				ut = UnitType.TANK;
			} else {
				ut = UnitType.INFANTERY;
			}
			draftPhase.draftUnit(player, ut);
		}
		draftPhase.playerDone(player);

	}

	@Override
	public void deploy() {
		Unit toDeploy = player.getUnitInHand().get(0);
		DeployPhase dp = (DeployPhase) game.getCurrentPhase();
		Field toDeployField = null;
		int x = 5;
		if (toDeploy.getUnitType() == UnitType.ARTILLERY) {
			x = 4;
		}
		int tryForColumn = 5;
		while (toDeployField == null) {
			int y = (int) (Math.random() * 10);
			if (game.getBoard().getField(x + ":" + y).getUnit() == null) {
				toDeployField = game.getBoard().getField(x + ":" + y);
			}
			tryForColumn--;
			if (tryForColumn == 0 && x > 0) {
				x--;
				tryForColumn = 5;
			}
		}

		dp.execCmd(player, "selectHandCard", toDeploy.getId());
		dp.execCmd(player, "selectTargetField", toDeployField.getId());
	}

	@Override
	public void command() {
		CombatCommandPhase combatCommandPhase = (CombatCommandPhase) game.getCurrentPhase();
		game.getBoard().getFields().stream().filter(f -> f.getUnit() != null)
				.filter(f -> f.getUnit().getPlayer() == player).map(f -> f.getUnit()).forEach(u -> {

					boolean setMove = true;
					if (u.getUnitType() == UnitType.ARTILLERY || u.getUnitType() == UnitType.HELICOPTER) {
						Optional<Field> targetField = u.getTargetableFields().stream().filter(f -> f.getUnit() != null)
								.filter(f -> f.getUnit().getPlayer() != player).findAny();
						if (targetField.isPresent()) {
							combatCommandPhase.getCc().addCommand(u, targetField.get(), CommandType.BOMBARD);
							setMove = false;
						}
					}
					if (setMove) {

						if (u.getUnitType() != UnitType.ARTILLERY) {

							Optional<Field> fieldToSupport = u.getSupportableFields(combatCommandPhase.getCc()).stream()
									.filter(f -> f.getUnit() != null).filter(f -> f.getUnit().getPlayer() != player)
									.findAny();
							if (fieldToSupport.isPresent()) {
								combatCommandPhase.getCc().addCommand(u, fieldToSupport.get(), CommandType.SUPPORT);
								setMove = false;
							}

						}

						if (setMove && Math.random() > 0.2) {

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
						}
					}

				});
		combatCommandPhase.execDoneButton(player);
	}

}
