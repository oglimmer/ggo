package de.oglimmer.ggo.web.action;

import static de.oglimmer.ggo.email.EmailService.EMAIL;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import de.oglimmer.ggo.atmospheremvc.game.Games;
import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreateGameQueryController extends BaseController {

	@GetMapping("/CreateGameQuery")
	public ResponseEntity<Result> show(HttpServletResponse response) {
		Game game = Games.<Game> getGames().createGame();
		Player player = game.createPlayer();
		response.addCookie(new Cookie("playerId", player.getId()));
		int numberOfNotifications = GameNotificationsDao.INSTANCE
				.allConfirmed(rec -> EMAIL.notifyGameCreatedRealtime(rec.getEmail(), rec.getConfirmId()));
		return ResponseEntity.ok(new Result(game.getId(), player.getId(), numberOfNotifications));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class Result {
		private String gameId;
		private String playerId;
		private int numberOfNotifications;
	}

}