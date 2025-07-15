package de.oglimmer.ggo.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.phase.TutorialDelegateBasePhase;
import de.oglimmer.ggo.logic.phase.tutorial.TutorialStepFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class CreateTutorialQueryController extends BaseController {

	@GetMapping("/CreateTutorialQuery")
	public ResponseEntity<Result> show(HttpServletResponse response) {
		Game game = Games.<Game> getGames().createGame();
		TutorialDelegateBasePhase bp = new TutorialStepFactory().build(game);
		bp.setDelegate(game.getCurrentPhase());
		boolean initShouldBeCalled = game.setCurrentPhase(bp);
		assert !initShouldBeCalled;
		Player player = game.createPlayer();
		game.createPlayer();
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