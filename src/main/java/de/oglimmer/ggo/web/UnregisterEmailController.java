package de.oglimmer.ggo.web;

import de.oglimmer.ggo.db.GameNotificationsDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
public class UnregisterEmailController extends BaseController {

	private GameNotificationsDao gameNotificationsDao;

	@GetMapping("/UnregisterEmail")
	public String show(@RequestParam String confirmId, RedirectAttributes redirectAttributes) {
		gameNotificationsDao.unregisterEmail(confirmId);
		redirectAttributes.addFlashAttribute("message", 
				"Your email address has been deleted. We hope you'll come back soon.");
		return "redirect:/";
	}

}