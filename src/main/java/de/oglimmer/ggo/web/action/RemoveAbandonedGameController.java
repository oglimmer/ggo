package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RemoveAbandonedGameController extends BaseController {

	@PostMapping("/RemoveAbandonedGame")
	public ResponseEntity<Void> remove(@RequestParam String gameId) {
		Games.<Game> getGames().removeAbandonedGame(gameId);
		return ResponseEntity.ok().build();
	}

}