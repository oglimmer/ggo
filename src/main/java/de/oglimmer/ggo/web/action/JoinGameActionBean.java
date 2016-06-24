package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Player;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

@Slf4j
public class JoinGameActionBean extends BaseAction {

	@Setter
	private String gameId;

	@DefaultHandler
	@DontValidate
	public Resolution join() {
		Game game = Games.INSTANCE.getGameById(gameId);
		if (game.getPlayers().size() == 1) {
			Player player = game.createPlayer();
			game.startGame();
			getContext().getResponse().addCookie(new Cookie("playerId", player.getId()));
			RedirectResolution redirect = new RedirectResolution(BoardActionBean.class);
			redirect.addParameter("playerId", player.getId());
			return redirect;
		}
		log.error("Join game called but game already had 2 player. GameId={}", gameId);
		return new RedirectResolution(LandingActionBean.class);
	}

}