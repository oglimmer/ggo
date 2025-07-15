package de.oglimmer.ggo.web;

import de.oglimmer.ggo.logic.ai.ChatGPTStrategy;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class CreateAiQueryController extends BaseController {

	@GetMapping("/CreateAiQuery")
	public ResponseEntity<Result> show(HttpServletResponse response) {
		Game game = Games.<Game>getGames().createGame();
		Player player = game.createPlayer();
		game.createAiPlayer(ChatGPTStrategy.class);
//		game.createAiPlayer(RandomStrategy.class);
		game.getCurrentPhase().init();
		response.addCookie(new Cookie("playerId", player.getId()));
		return ResponseEntity.ok(new Result(player.getId()));
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Result {
		private String playerId;
	}

}