package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.db.GameNotifications;
import de.oglimmer.ggo.db.GameNotifications.GameNotification;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import lombok.Getter;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;

public class LandingActionBean extends BaseAction {

	private static final String VIEW = "/WEB-INF/jsp/landing.jsp";

	@Getter
	private Game game;

	@Getter
	private Player player;

	@Setter
	@Getter
	private String email;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		if (getContext().getRequest().getCookies() != null) {
			for (Cookie cookie : getContext().getRequest().getCookies()) {
				if ("playerId".equals(cookie.getName())) {
					game = Games.<Game> getGames().getGameByPlayerId(cookie.getValue());
					if (game != null) {
						player = game.getPlayerById(cookie.getValue());
					}
				}
			}
		}
		return new ForwardResolution(VIEW);
	}

	@DontValidate
	public Resolution register() {
		GameNotification rec = GameNotifications.INSTANCE.addEmail(email);
		EmailService.INSTANCE.sendConfirmation(email, rec.getId(), rec.getConfirmId());
		email = "";
		getContext().getMessages()
				.add(new SimpleMessage("We sent you a confirmation email. Please look in your inbox/spam folder."));
		return show();
	}

	@DontValidate
	public Resolution resetGame() {
		getContext().getMessages().add(new SimpleMessage("All games reseted"));
		Games.<Game> getGames().reset();
		return show();
	}
}