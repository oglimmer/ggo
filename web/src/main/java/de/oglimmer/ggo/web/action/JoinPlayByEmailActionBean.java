package de.oglimmer.ggo.web.action;

import javax.servlet.http.Cookie;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.db.GameNotification;
import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;

@Slf4j
public class JoinPlayByEmailActionBean extends BaseAction {

	@Setter
	private String gameId;
	@Setter
	private String confirmId;

	@DefaultHandler
	@DontValidate
	public Resolution join() {
		Game game = Games.<Game> getGames().getGameById(gameId);
		if (game.getPlayers().size() == 1) {
			GameNotification gameNoti = GameNotificationsDao.INSTANCE.getByConfirmId(confirmId);
			if (gameNoti != null) {
				Player player = game.createPlayer(gameNoti.getEmail());
				game.startGame();
				getContext().getResponse().addCookie(new Cookie("playerId", player.getId()));
				return new RedirectResolution(BoardActionBean.class).addParameter("playerId", player.getId());
			}
		}
		getContext().getMessages().add(new SimpleMessage("Someone else was faster! The game has already 2 players!"));
		log.error("Join game called but game already had 2 player. GameId={}", gameId);
		return new RedirectResolution(LandingActionBean.class);
	}

}