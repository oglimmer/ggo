package de.oglimmer.ggo.web.action;

import static de.oglimmer.ggo.email.EmailService.EMAIL;

import javax.servlet.http.Cookie;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.db.GameNotificationsDao;
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
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;

public class CreateGameByEmailActionBean extends BaseAction {

	private static final String CREATE_BY_EMAIL_VIEW = "/WEB-INF/jsp/createGameByEmail.jsp";
	private static final String WAIT_VIEW = "/WEB-INF/jsp/waitForPlayerJoin.jsp";

	@Setter
	@Getter
	@Validate(converter = EmailTypeConverter.class, required = true)
	private String email1;

	@Setter
	@Getter
	@Validate(converter = EmailTypeConverter.class, required = false)
	private String email2;

	@Setter
	@Getter
	private boolean searchForOne;

	@ValidationMethod(when = ValidationState.ALWAYS)
	public void validateSomething(ValidationErrors errors) {
		if (!searchForOne && (email2 == null || email2.trim().isEmpty())) {
			if (!errors.containsKey("email2")) {
				errors.add("email2", new SimpleError(
						"Either you need to add an opponent''s email or select the checkbox to use the player''s database"));
			}
		}
	}

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		return new ForwardResolution(CREATE_BY_EMAIL_VIEW);
	}

	public Resolution createEmail() {
		Game game = Games.<Game> getGames().createGame();
		Player player1 = game.createPlayer(email1);
		getContext().getResponse().addCookie(new Cookie("playerId", player1.getId()));
		if (searchForOne || email2 == null || email2.trim().isEmpty()) {
			GameNotificationsDao.INSTANCE.allConfirmed(
					rec -> EMAIL.notifyGameCreatedByEmail(rec.getEmail(), rec.getConfirmId(), game.getId()));
			return new ForwardResolution(WAIT_VIEW);
		} else {
			game.createPlayer(email2);
			game.startGame();
			return new RedirectResolution(BoardActionBean.class).addParameter("playerId", player1.getId());
		}
	}

}