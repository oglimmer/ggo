package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Player;
import lombok.Getter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class LandingActionBean extends BaseAction {

	private static final String VIEW = "/WEB-INF/jsp/landing.jsp";

	@Getter
	private Game game;

	@Getter
	private Player player;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		for (Cookie cookie : getContext().getRequest().getCookies()) {
			if ("playerId".equals(cookie.getName())) {
				game = Games.INSTANCE.getGameByPlayerId(cookie.getValue());
				if (game != null) {
					player = game.getPlayerById(cookie.getValue());
				}
			}
		}
		return new ForwardResolution(VIEW);
	}

	@DontValidate
	public Resolution resetGame() {
		Games.INSTANCE.reset();
		return show();
	}
}