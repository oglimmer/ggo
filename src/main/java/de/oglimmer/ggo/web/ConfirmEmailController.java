package de.oglimmer.ggo.web;

import de.oglimmer.ggo.db.GameNotificationsDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
public class ConfirmEmailController extends BaseController {

	private GameNotificationsDao gameNotificationsDao;

	@GetMapping("/ConfirmEmail")
	public String show(@RequestParam String confirmId, RedirectAttributes redirectAttributes) {
		gameNotificationsDao.confirmEmail(confirmId);
		redirectAttributes.addFlashAttribute("message", 
				"Your email address is now confirmed. We'll send you notification when someone creates a new game. Stay tuned.");
		return "redirect:/";
	}

}