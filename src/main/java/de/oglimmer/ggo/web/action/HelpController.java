package de.oglimmer.ggo.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController extends BaseController {

	@GetMapping("/Help")
	public String join(Model model) {
		addCommonAttributes(model);
		return "help";
	}

}