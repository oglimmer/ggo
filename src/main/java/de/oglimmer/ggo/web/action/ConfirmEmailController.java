package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.db.GameNotificationsDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfirmEmailController extends BaseController {

	@GetMapping("/ConfirmEmail")
	public String show(@RequestParam String confirmId, RedirectAttributes redirectAttributes) {
		GameNotificationsDao.INSTANCE.confirmEmail(confirmId);
		redirectAttributes.addFlashAttribute("message", 
				"Your email address is now confirmed. We'll send you notifcations when someone craates a new game. Stay tuned.");
		return "redirect:/";
	}

}