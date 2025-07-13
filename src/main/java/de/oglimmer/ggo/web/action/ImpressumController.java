package de.oglimmer.ggo.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ImpressumController extends BaseController {

	@GetMapping("/Impressum")
	public String show(Model model) {
		addCommonAttributes(model);
		return "impressum";
	}

}