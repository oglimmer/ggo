package de.oglimmer.ggo.web;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class CreateGameController extends BaseController {

	@GetMapping("/CreateGame")
	public String show(Model model) {
		addCommonAttributes(model);
		return "createGame";
	}

}