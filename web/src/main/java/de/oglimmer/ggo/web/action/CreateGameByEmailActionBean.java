package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import lombok.Getter;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.Validate;

public class CreateGameByEmailActionBean extends BaseAction {

	private static final String WAIT_VIEW = "/WEB-INF/jsp/createGameByEmail.jsp";

	@Setter
	@Getter
	@Validate(converter = EmailTypeConverter.class, required = true)
	private String email1;
	@Setter
	@Getter
	@Validate(converter = EmailTypeConverter.class, required = true)
	private String email2;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		return new ForwardResolution(WAIT_VIEW);
	}

	public Resolution createEmail() {
		Game game = Games.<Game> getGames().createGame();
		Player player1 = game.createPlayer(email1);
		getContext().getResponse().addCookie(new Cookie("playerId", player1.getId()));
		game.createPlayer(email2);
		game.startGame();
		return new RedirectResolution(BoardActionBean.class).addParameter("playerId", player1.getId());
	}

}