package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.db.GameNotification;
import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.websocket.game.Games;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
public class LandingController extends BaseController {

	private GameNotificationsDao gameNotificationsDao;
	private EmailService emailService;

	@GetMapping({"/", "/Landing"})
	public String show(HttpServletRequest request, Model model) {
		Game game = null;
		Player player = null;
		
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("playerId".equals(cookie.getName())) {
					game = Games.<Game> getGames().getGameByPlayerId(cookie.getValue());
					if (game != null) {
						player = game.getPlayerById(cookie.getValue());
					}
				}
			}
		}
		
		model.addAttribute("game", game);
		model.addAttribute("player", player);
		addCommonAttributes(model);
		
		return "landing";
	}

	@PostMapping("/Landing")
	public String register(@RequestParam String email, RedirectAttributes redirectAttributes) {
		GameNotification rec = gameNotificationsDao.addEmail(email);
		emailService.sendConfirmation(email, rec.getId(), rec.getConfirmId());
		redirectAttributes.addFlashAttribute("message", "We sent you a confirmation email. Please look in your inbox/spam folder.");
		return "redirect:/";
	}

}