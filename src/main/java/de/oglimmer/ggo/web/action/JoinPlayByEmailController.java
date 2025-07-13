package de.oglimmer.ggo.web.action;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import de.oglimmer.ggo.atmospheremvc.game.Games;
import de.oglimmer.ggo.db.GameNotification;
import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class JoinPlayByEmailController extends BaseController {

	@GetMapping("/JoinPlayByEmail")
	public String join(@RequestParam String gameId, @RequestParam String confirmId, 
					   HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Game game = Games.<Game> getGames().getGameById(gameId);
		if (game.getPlayers().size() == 1) {
			GameNotification gameNoti = GameNotificationsDao.INSTANCE.getByConfirmId(confirmId);
			if (gameNoti != null) {
				Player player = game.createPlayer(gameNoti.getEmail());
				game.startGame();
				response.addCookie(new Cookie("playerId", player.getId()));
				return "redirect:/Board?playerId=" + player.getId();
			}
		}
		redirectAttributes.addFlashAttribute("message", "Someone else was faster! The game has already 2 players!");
		log.error("Join game called but game already had 2 player. GameId={}", gameId);
		return "redirect:/";
	}

}