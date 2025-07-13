package de.oglimmer.ggo.web.action;

import java.util.Collection;

import de.oglimmer.ggo.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingGameQueryController extends BaseController {

	@GetMapping("/LandingGameQuery")
	public String show(Model model) {
		Collection<Game> availableGames = Games.<Game> getGames().getOpenGames();
		model.addAttribute("availableGames", availableGames);
		addCommonAttributes(model);
		return "landingGameQuery";
	}

}