package de.oglimmer.ggo.web.action;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import de.oglimmer.ggo.websocket.game.Games;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Slf4j
@Controller
public class JoinGameController extends BaseController {

	@GetMapping("/JoinGame")
	public String join(@RequestParam String gameId, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Game game = Games.<Game> getGames().getGameById(gameId);
		if (game.getPlayers().size() == 1) {
			Player player = game.createPlayer();
			game.startGame();
			response.addCookie(new Cookie("playerId", player.getId()));
			return "redirect:/Board?playerId=" + player.getId();
		}
		redirectAttributes.addFlashAttribute("message", "Someone else was faster! The game has already 2 players!");
		log.error("Join game called but game already had 2 player. GameId={}", gameId);
		return "redirect:/";
	}

}