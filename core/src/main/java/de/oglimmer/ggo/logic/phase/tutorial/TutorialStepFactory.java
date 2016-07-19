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
import de.oglimmer.ggo.logic.phase.TutorialDelegateEndPhase;
import de.oglimmer.ggo.logic.phase.TutorialDelegateTextPhase;
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
				.setTitle("The goal of the game is to score the most points in 5 turns, where each turn has 3 phases:"
						+ " Draft, Deploy and Combat/Move. Let's start with the first phase in the first turn: Draft units. "
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

				/* --round:1 */

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

				.newPhase(TextBuilder.class)
				.setTitle("The red arrow indicates that a unit will move to another field."
						+ " A battle will take place on that target field if it has two opposing "
						+ "units after the movements are done. So no battle occur if the enemy player moves his unit away."
						+ " All units showing an 'F' will fortify. That means they will get +1 strength. "
						+ "Press the done button to finish your command round.")
				.end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					BasePhase currentPhase = game.getCurrentPhase();
					currentPhase.execCmd(opponent, "selectUnit", game.getBoard().getField("5:5").getUnit().getId());
					currentPhase.execCmd(opponent, "selectTargetField", "4:5");

					currentPhase.execCmd(opponent, "button", "doneButton");
				}).end()

				.newPhase(TextBuilder.class)
				.setTitle("After both players finsihed their command round, you will see your and the"
						+ " opponents commands. In this game the opponent only commanded his infantry to move towards your infantry."
						+ " This leads to the only situation where to units battle each other while not ending up on the same field."
						+ " That means crossing an enemy unit will also let those units battle each other. As both units have a strength"
						+ " of 1, they both get killed and thus removed from play. Press the done button to proceed to combat round 2.")
				.end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					BasePhase currentPhase = game.getCurrentPhase();
					currentPhase.execCmd(opponent, "button", "doneButton");
				}).end()

				/* --round:2 */

				.newPhase(CombatBuilder.class)
				.setTitle("Before we give our commands for the second round, let's have a look at the score resulting "
						+ "round 1. You can see that both players scored 10 points. A player scores 10 points when"
						+ "a unit moves to another field and kills an enemy unit. A player socres 5 points for "
						+ "each bombardment. A player scores 25 points for occupying a city at the end of a turn. "
						+ "In the second round we will use the bombard ability from the artillery to destroy the "
						+ "enemy tank. Click the artillery unit.")
				.setUnit(game.getBoard().getField("3:5")).end()

				.newPhase(CombatBuilder.class)
				.setTitle("Bombardments will always destroy the enemy unit. Select the enemy tank as the destination.")
				.setField(game.getBoard().getField("5:4")).end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					game.getCurrentPhase().execCmd(game.getPlayers().get(0), "button", "doneButton");
					game.getCurrentPhase().execCmd(game.getPlayers().get(1), "button", "doneButton");
				}).end()

				.newPhase(TextBuilder.class)
				.setTitle("The opponent didn't give any command to an emeny unit. As a result"
						+ " of this round only the enemy tank got destroyed.")
				.end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					BasePhase currentPhase = game.getCurrentPhase();
					currentPhase.execCmd(opponent, "button", "doneButton");
				}).end()

				/* --round:3 */

				.newPhase(CombatBuilder.class)
				.setTitle("Round 3 of the combat phase. Let's have a look at the last command: "
						+ "support. All units except for the artillery can support another unit. Supporting another "
						+ "unit will give the supported unit +1 strength. Let's try that. Click the tank.")
				.setUnit(game.getBoard().getField("4:4")).end()

				.newPhase(CombatBuilder.class)
				.setTitle("Now click the helicopter to support it. As a helicopter has a strength of 1"
						+ ", with the support from the tank and its own fortify command, the helicopter has a total strength of 3.")
				.setField(game.getBoard().getField("4:3")).end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					BasePhase currentPhase = game.getCurrentPhase();
					currentPhase.execCmd(opponent, "selectUnit", game.getBoard().getField("5:3").getUnit().getId());
					currentPhase.execCmd(opponent, "selectTargetField", "4:3");
					currentPhase.execCmd(opponent, "selectModalDialog", "BOMBARD");

					game.getCurrentPhase().execCmd(game.getPlayers().get(0), "button", "doneButton");
					game.getCurrentPhase().execCmd(game.getPlayers().get(1), "button", "doneButton");
				}).end()

				.newPhase(TextBuilder.class)
				.setTitle("The opponent commanded his helicopter to bombard your helicopter. As a bombard "
						+ "always results in defeading a unit, your helicopter is lost. Maybe it would have been wiser"
						+ " to give your helicopter also a bombard command to the enemy helicopter.")
				.end()

				.newPhase(CodeExecBuilder.class).exec(() -> {
					Player opponent = game.getPlayers().get(1);
					BasePhase currentPhase = game.getCurrentPhase();
					currentPhase.execCmd(opponent, "button", "doneButton");
				}).end()

				/* Draft */

				.newPhase(EndBuilder.class)
				.setTitle("At this point the second turn has started. Once again you"
						+ " get additional 1000 credits to draft new units, which you will deploy on the board, which"
						+ " will end up moving/supporting/bombarding/fortifying. Before you close this and create a real game"
						+ " against a human opponent, keep in mind that the instructions are always available via the top right"
						+ " link 'instructions'. Also remember that you win a game by scoring more points than your opponent -"
						+ " not having more units or surviving longer.")
				.end()

				.newPhase(EndBuilder.class).setTitle("NEVER REACHABLE.").end()

				.getFirst();
	}

}

@RequiredArgsConstructor
abstract class BaseBuilder<T extends BaseBuilder<?>> {

	@Getter
	@NonNull
	private Builder build;

	private String title;

	@SuppressWarnings("unchecked")
	public T setTitle(String title) {
		this.title = title;
		return (T) this;
	}

	public Builder end() {
		return endIntern();
	}

	abstract protected <M extends TutorialDelegateBasePhase> Builder endIntern();

	@SneakyThrows
	protected <M extends TutorialDelegateBasePhase> Builder endIntern(Class<M> clazz, Consumer<M> cons) {
		M tutorialPhase = clazz.getConstructor(Game.class).newInstance(build.getGame());

		if (build.getFirst() == null) {
			build.setFirst(tutorialPhase);
		}
		if (build.getLast() != null) {
			build.getLast().setNextPhase(tutorialPhase);
		}

		tutorialPhase.setTitle(title);

		if (cons != null) {
			cons.accept(tutorialPhase);
		}

		build.setLast(tutorialPhase);
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

class EndBuilder extends BaseBuilder<EndBuilder> {

	public EndBuilder(Builder build) {
		super(build);
	}

	@Override
	protected <M extends TutorialDelegateBasePhase> Builder endIntern() {
		return endIntern(TutorialDelegateEndPhase.class, null);
	}

}

class CodeExecBuilder extends BaseBuilder<CodeExecBuilder> {

	private Runnable execCode;

	public CodeExecBuilder(Builder build) {
		super(build);
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
