package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Player;
import lombok.Getter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

public class JoinActionBean extends BaseAction {

	private static final String WAIT_VIEW = "/WEB-INF/jsp/waiting.jsp";

	@Getter
	private Game game;
	@Getter
	private Player player;

	private String gameId;

	public Resolution create() {
		game = Games.INSTANCE.createGame();
		player = game.createPlayer();
		getContext().getResponse().addCookie(new Cookie("playerId", player.getId()));
		return new ForwardResolution(WAIT_VIEW);
	}

	public Resolution join() {
		game = Games.INSTANCE.getGameById(gameId);
		player = game.createPlayer();
		getContext().getResponse().addCookie(new Cookie("playerId", player.getId()));
		RedirectResolution redirect = new RedirectResolution(BoardActionBean.class);
		redirect.addParameter("playerId", player.getId());
		return redirect;
	}

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		return new ForwardResolution(WAIT_VIEW);
	}

}