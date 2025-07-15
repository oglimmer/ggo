package de.oglimmer.ggo.web;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class HelpController extends BaseController {

	@GetMapping("/Help")
	public String join(Model model) {
		addCommonAttributes(model);
		return "help";
	}

}