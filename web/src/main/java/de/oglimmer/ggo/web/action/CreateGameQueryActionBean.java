package de.oglimmer.ggo.web.action;

import static de.oglimmer.ggo.email.EmailService.EMAIL;

import javax.servlet.http.Cookie;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.logic.Game;
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
		Game game = Games.<Game> getGames().createGame();
		Player player = game.createPlayer();
		getContext().getResponse().addCookie(new Cookie("playerId", player.getId()));
		int numberOfNotifications = GameNotificationsDao.INSTANCE
				.allConfirmed(rec -> EMAIL.notifyGameCreatedRealtime(rec.getEmail(), rec.getConfirmId()));
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