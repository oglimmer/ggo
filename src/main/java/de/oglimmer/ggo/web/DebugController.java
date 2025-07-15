package de.oglimmer.ggo.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.websocket.WebSocketSessionCache;
import de.oglimmer.ggo.websocket.WebSocketSessionCacheItem;
import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.db.GameNotification;
import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.phase.TutorialDelegateBasePhase;
import de.oglimmer.ggo.util.GridGameOneProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@AllArgsConstructor
@Controller
public class DebugController extends BaseController {

	private GameNotificationsDao gameNotificationsDao;
	private GridGameOneProperties properties;

	@GetMapping("/Debug")
	public String show(@RequestParam String pass, Model model, RedirectAttributes redirectAttributes) {
		if (pass == null || !properties.getRuntimePassword().equals(pass)) {
			redirectAttributes.addFlashAttribute("error", "invalid password");
			return "redirect:/Debug";
		}
		
		List<WebSocketSessionCacheItem> webSocketSessions = WebSocketSessionCache.INSTANCE.getItems();
		Collection<Game> games = Games.<Game> getGames().getAllGames();
		Collection<GameNotification> gameNotifications = gameNotificationsDao.all();
		
		model.addAttribute("webSocketSessions", webSocketSessions);
		model.addAttribute("games", games);
		model.addAttribute("pass", pass);
		model.addAttribute("gameNotifications", gameNotifications);
		addCommonAttributes(model);
		return "debugOverview";
	}

	@GetMapping("/Debug/resetGame")
	public String resetGame(@RequestParam String pass, RedirectAttributes redirectAttributes) {
		if (!properties.getRuntimePassword().equals(pass)) {
			redirectAttributes.addFlashAttribute("error", "invalid password");
			return "redirect:/Debug";
		}
		
		Games.<Game> getGames().reset();
		return "redirect:/Debug?pass=" + pass;
	}

	public static Collection<String> buildPhaseStack(Game game) {
		Collection<String> collectedPhases = new ArrayList<>();
		collectedPhases.add(game.getCurrentPhase().toString());
		if (game.getCurrentPhase() instanceof TutorialDelegateBasePhase) {
			TutorialDelegateBasePhase tdbp = (TutorialDelegateBasePhase) game.getCurrentPhase();
			while ((tdbp = tdbp.getNextPhase()) != null) {
				collectedPhases.add(tdbp.toString());
			}
		}
		return collectedPhases;
	}

	public static WebSocketSessionCacheItem getWebSocketSessions(Player p, List<WebSocketSessionCacheItem> webSocketSessions) {
		Optional<WebSocketSessionCacheItem> item = webSocketSessions.stream().filter(ar -> ar.getPlayer() == p)
				.findFirst();
        return item.orElse(null);
    }

}