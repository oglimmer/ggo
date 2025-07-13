package de.oglimmer.ggo.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BoardController extends BaseController {

	@GetMapping("/Board")
	public String show(@RequestParam(required = false) String playerId, Model model) {
		model.addAttribute("playerId", playerId);
		addCommonAttributes(model);
		return "board";
	}
}