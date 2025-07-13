package de.oglimmer.ggo.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreateGameController extends BaseController {

	@GetMapping("/CreateGame")
	public String show(Model model) {
		addCommonAttributes(model);
		return "createGame";
	}

}