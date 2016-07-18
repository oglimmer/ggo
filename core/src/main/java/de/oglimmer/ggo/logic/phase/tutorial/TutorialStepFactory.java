package de.oglimmer.ggo.logic.phase.tutorial;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import de.oglimmer.ggo.logic.Field;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.UnitType;
import de.oglimmer.ggo.logic.phase.BasePhase;
import de.oglimmer.ggo.logic.phase.DraftPhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateBasePhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateCodeExecPhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateCombatPhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateDeployPhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateDraftPhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateTextPhase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

public class TutorialStepFactory {

	public TutorialDelegateBasePhase build(Game game) {

		return new Builder(game)

				/* Some texts at the beginning */

				.newPhase(TextBuilder.class)
				.setTitle("Welcome commander! This is the tutorial for Grid Game One. It will show you how to "
						+ "play this game."
						+ " Keep in mind that a summary of the rules are always available from the 'Instructions' "
						+ "link on the top right. Now click the 'Done' button at the bottom.")
				.setHideScore(true).setHideInfo(true).end()

				.newPhase(TextBuilder.class)
				.setTitle("The goal of the game to score the most points in 5 turns, where each turn has 3 phases:"
						+ " Draft, "
						+ "Deploy and Combat/Move. Let's start with the first phase in the first turn: Draft units. "
						+ "Click the done button.")
				.setHideScore(true).setHideInfo(true).end()

				/* Draft */

				.newPhase(DraftBuilder.class)
				.setTitle("For each turn you get plus " + DraftPhase.CREDITS_PER_TURN
						+ " credits. You can spend or save them. Let's start with drafting an infantery unit for "
						+ UnitType.INFANTERY.getCost() + " credits by clicking on the icon at the bottom.")
				.setUnitType(UnitType.INFANTERY).end()

				.newPhase(DraftBuilder.class)
				.setTitle("You got 1 infantry unit into your hand. Now let's buy a tank unit. Tank units are very "
						+ "similar to infantries, but stronger in strength. We'll talk about strength later."
						+ " Click on the tank icon and buy a tank unit for " + UnitType.TANK.getCost() + " credits.")
				.setUnitType(UnitType.TANK).end()

				.newPhase(DraftBuilder.class)
				.setTitle("Now buy a helicopter unit. Helicopters have a strength of 1, but can also bombard "
						+ "enemy units within the range of 1 field.")
				.setUnitType(UnitType.HELICOPTER).end()

				.newPhase(DraftBuilder.class)
				.setTitle("Finally buy an artillery unit. They have a strength of 0, but can bombard enemy units "
						+ "within a range of 2 fields.")
				.setUnitType(UnitType.ARTILLERY).end()

				.newPhase(TextBuilder.class).setTitle("You don't have enough credits left to buy another unit."
						+ " Press done to complete your draft phase.")
				.end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					BasePhase currentPhase = game.getCurrentPhase();
					Player opponent = game.getPlayers().get(1);
					currentPhase.execCmd(opponent, "button", "buy" + UnitType.INFANTERY);
					currentPhase.execCmd(opponent, "button", "buy" + UnitType.TANK);
					currentPhase.execCmd(opponent, "button", "buy" + UnitType.HELICOPTER);

					currentPhase.execCmd(opponent, "button", "doneButton");
				}).end()

				/* Deploy */

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					game.getCurrentPhase().execCmd(opponent, "selectHandCard", opponent.getUnitInHand().get(0).getId());
					game.getCurrentPhase().execCmd(opponent, "selectTargetField", "5:5");
				}).end()

				.newPhase(DeployBuilder.class)
				.setTitle("The deploy phase was started by your opponent, who put an infantry on the board. "
						+ "While the draft phase was played in parallel by both players, in the deploy phase every "
						+ "player deploys one unit at a time. So it is your turn now. Deploy the "
						+ "infantry! Click on the unit.")
				.setUnitType(UnitType.INFANTERY).end()

				.newPhase(DeployBuilder.class)
				.setTitle("You can deploy a unit to all fields on your side of the board. For this tutorial deploy the"
						+ " infantry on the one highlighted field to oppose the enemy infantry.")
				.setField(game.getBoard().getField("4:5")).end()

				.newPhase(TextBuilder.class)
				.setTitle("Now the enemy player deploys a second unit. Press done to see what the opponent does.").end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					game.getCurrentPhase().execCmd(opponent, "selectHandCard", opponent.getUnitInHand().get(0).getId());
					game.getCurrentPhase().execCmd(opponent, "selectTargetField", "5:4");
				}).end()

				.newPhase(DeployBuilder.class)
				.setTitle("The opponent deployed a tank next to his infantry. "
						+ "It's time for you to deploy your second unit. Click on the tank.")
				.setUnitType(UnitType.TANK).end()

				.newPhase(DeployBuilder.class).setTitle("Deploy your tank to the highlighted field.")
				.setField(game.getBoard().getField("4:4")).end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					game.getCurrentPhase().execCmd(opponent, "selectHandCard", opponent.getUnitInHand().get(0).getId());
					game.getCurrentPhase().execCmd(opponent, "selectTargetField", "5:3");
				}).end()

				.newPhase(DeployBuilder.class).setTitle("Click on the helicopter.").setUnitType(UnitType.HELICOPTER)
				.end()

				.newPhase(DeployBuilder.class).setTitle("Deploy this helicopter to the highlighted field.")
				.setField(game.getBoard().getField("4:3")).end()

				.newPhase(DeployBuilder.class)
				.setTitle(
						"The opponent didn't buy a 4th unit. So you can deploy your last unit right away. Click on the artillary.")
				.setUnitType(UnitType.ARTILLERY).end()

				.newPhase(DeployBuilder.class).setTitle("Deploy this artillary to the highlighted field.")
				.setField(game.getBoard().getField("3:5")).end()

				/* Combat */

				.newPhase(CombatBuilder.class)
				.setTitle("After the last unit is deployed the game proceeds to the combat/move phase. "
						+ "This phase is devided into 3 turns. Each turn has a command and a view part."
						+ "In the command part you can give each unit one of up to 4 commands: fortify, move/attack, support or bombarb."
						+ "While every unit has fortify and move, not every unit has attack, support or bombarb. "
						+ "Let's start with giving a command to the infatry unit. Click the infantry unit.")
				.setUnit(game.getBoard().getField("4:5")).end()

				.newPhase(CombatBuilder.class)
				.setTitle("To command the infantry 'move into the left field' -"
						+ " where the enemy infantry is currently located - click on that highlighted field.")
				.setField(game.getBoard().getField("5:5")).end()

				.newPhase(TextBuilder.class).setTitle("END.").end()

				.getFirst();
	}

}

@RequiredArgsConstructor
abstract class BaseBuilder<T extends BaseBuilder<?>> {

	@Getter
	@NonNull
	private Builder build;

	private String title;

	@Setter(value = AccessLevel.PACKAGE)
	private boolean autoEnd;

	@SuppressWarnings("unchecked")
	public T setTitle(String title) {
		this.title = title;
		return (T) this;
	}

	public Builder autoEnd() {
		this.autoEnd = true;
		return endIntern();
	}

	public Builder end() {
		return endIntern();
	}

	abstract protected <M extends TutorialDelegateBasePhase> Builder endIntern();

	@SneakyThrows
	protected <M extends TutorialDelegateBasePhase> Builder endIntern(Class<M> clazz, Consumer<M> cons) {
		M p = clazz.getConstructor(Game.class).newInstance(build.getGame());

		if (build.getFirst() == null) {
			build.setFirst(p);
		}
		if (build.getLast() != null) {
			build.getLast().setNextPhase(p);
		}

		p.setTitle(title);
		p.setAutoEnd(autoEnd);

		if (cons != null) {
			cons.accept(p);
		}

		build.setLast(p);
		return build;
	}

}

class TextBuilder extends BaseBuilder<TextBuilder> {

	private boolean hideScore;
	private boolean hideInfo;

	public TextBuilder(Builder build) {
		super(build);
	}

	public TextBuilder setHideScore(boolean hideScore) {
		this.hideScore = hideScore;
		return this;
	}

	public TextBuilder setHideInfo(boolean hideInfo) {
		this.hideInfo = hideInfo;
		return this;
	}

	@Override
	protected <M extends TutorialDelegateBasePhase> Builder endIntern() {
		return endIntern(TutorialDelegateTextPhase.class, p -> {
			p.setHideScore(this.hideScore);
			p.setHideInfo(this.hideInfo);
		});
	}

}

class DraftBuilder extends BaseBuilder<DraftBuilder> {

	private UnitType unitType;

	public DraftBuilder(Builder build) {
		super(build);
	}

	public DraftBuilder setUnitType(UnitType unitType) {
		this.unitType = unitType;
		return this;
	}

	@Override
	protected <M extends TutorialDelegateBasePhase> Builder endIntern() {
		return endIntern(TutorialDelegateDraftPhase.class, p -> {
			p.setUnitType(this.unitType);
		});
	}

}

class DeployBuilder extends BaseBuilder<DeployBuilder> {

	private UnitType unitType;
	private Field field;

	public DeployBuilder(Builder build) {
		super(build);
	}

	public DeployBuilder setField(Field field) {
		this.field = field;
		return this;
	}

	public DeployBuilder setUnitType(UnitType unitType) {
		this.unitType = unitType;
		return this;
	}

	@Override
	protected <M extends TutorialDelegateBasePhase> Builder endIntern() {
		return endIntern(TutorialDelegateDeployPhase.class, p -> {
			p.setUnitType(this.unitType);
			p.setField(this.field);
		});
	}

}

class CombatBuilder extends BaseBuilder<CombatBuilder> {

	private Field unit;
	private Field field;

	public CombatBuilder(Builder build) {
		super(build);
	}

	public CombatBuilder setField(Field field) {
		this.field = field;
		return this;
	}

	public CombatBuilder setUnit(Field unit) {
		this.unit = unit;
		return this;
	}

	@Override
	protected <M extends TutorialDelegateBasePhase> Builder endIntern() {
		return endIntern(TutorialDelegateCombatPhase.class, p -> {
			p.setUnit(this.unit);
			p.setField(this.field);
		});
	}

}

class CodeExecBuilder extends BaseBuilder<CodeExecBuilder> {

	private Runnable execCode;

	public CodeExecBuilder(Builder build) {
		super(build);
		setAutoEnd(true);
	}

	public CodeExecBuilder exec(Runnable execCode) {
		this.execCode = execCode;
		return this;
	}

	@Override
	protected <M extends TutorialDelegateBasePhase> Builder endIntern() {
		return endIntern(TutorialDelegateCodeExecPhase.class, p -> {
			p.setExecCode(execCode);
		});
	}
}

@RequiredArgsConstructor
class Builder {

	@NonNull
	@Getter
	private Game game;

	@Setter
	@Getter
	private TutorialDelegateBasePhase first;

	@Setter
	@Getter
	private TutorialDelegateBasePhase last;

	public <T extends BaseBuilder<?>> T newPhase(Class<T> clazz) {
		try {
			return clazz.getConstructor(Builder.class).newInstance(this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

}
