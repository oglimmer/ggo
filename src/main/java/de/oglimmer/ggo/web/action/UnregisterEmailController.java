package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.db.GameNotificationsDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UnregisterEmailController extends BaseController {

	@GetMapping("/UnregisterEmail")
	public String show(@RequestParam String confirmId, RedirectAttributes redirectAttributes) {
		GameNotificationsDao.INSTANCE.unregisterEmail(confirmId);
		redirectAttributes.addFlashAttribute("message", 
				"Your email address has been deleted. We hope you'll come back soon.");
		return "redirect:/";
	}

}