package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.phase.TutorialDelegateBasePhase;
import de.oglimmer.ggo.logic.phase.tutorial.TutorialStepFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.JsonResolution;
import net.sourceforge.stripes.action.Resolution;

public class CreateTutorialQueryActionBean extends BaseAction {

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		Game game = Games.<Game> getGames().createGame();
		TutorialDelegateBasePhase bp = new TutorialStepFactory().build(game);
		bp.setDelegate(game.getCurrentPhase());
		assert !game.setCurrentPhase(bp);
		Player player = game.createPlayer();
		game.createPlayer();
		game.getCurrentPhase().init();
		getContext().getResponse().addCookie(new Cookie("playerId", player.getId()));
		return new JsonResolution(new Result(player.getId()));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	class Result {
		private String playerId;
	}

}