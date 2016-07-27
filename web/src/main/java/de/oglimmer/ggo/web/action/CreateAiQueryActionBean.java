package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.ai.RandomStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.JsonResolution;
import net.sourceforge.stripes.action.Resolution;

public class CreateAiQueryActionBean extends BaseAction {

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		Game game = Games.<Game>getGames().createGame();
		Player player = game.createPlayer();
		game.createAiPlayer(RandomStrategy.class);
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