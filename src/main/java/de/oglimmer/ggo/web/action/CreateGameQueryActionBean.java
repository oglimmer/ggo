package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.ggo.db.GameNotifications;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.JsonResolution;
import net.sourceforge.stripes.action.Resolution;

public class CreateGameQueryActionBean extends BaseAction {

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		Game game = Games.INSTANCE.createGame();
		Player player = game.createPlayer();
		getContext().getResponse().addCookie(new Cookie("playerId", player.getId()));
		int numberOfNotifications = GameNotifications.INSTANCE
				.allConfirmed(rec -> EmailService.INSTANCE.notifyGameCreated(rec.getEmail(), rec.getConfirmId()));
		return new JsonResolution(new Result(game.getId(), player.getId(), numberOfNotifications));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	class Result {
		private String gameId;
		private String playerId;
		private int numberOfNotifications;
	}

}