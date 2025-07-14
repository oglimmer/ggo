package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.websocket.game.Games;
import de.oglimmer.ggo.logic.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
public class WaitingForOtherPlayerQueryController extends BaseController {

	@GetMapping("/WaitingForOtherPlayerQuery")
	public ResponseEntity<Result> show(@RequestParam String gameId) {
		Game game = Games.<Game> getGames().getGameById(gameId);
		Result resultJson;
		if (game.getPlayers().size() == 2) {
			resultJson = new Result("redirect");
		} else {
			resultJson = new Result("wait");
		}
		return ResponseEntity.ok(resultJson);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Result {
		private String action;
	}

}