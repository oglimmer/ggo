package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import lombok.Getter;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class WaitingForOtherPlayerQueryActionBean extends BaseAction {

	private static final String VIEW = "/WEB-INF/jsp/waitingForOtherPlayerQuery.jsp";

	@Setter
	private String gameId;

	@Getter
	private String resultJson;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		Game game = Games.INSTANCE.getGameById(gameId);
		if (game.getPlayers().size() == 2) {
			resultJson = "{ \"action\": \"redirect\" }";
		} else {
			resultJson = "{ \"action\": \"wait\" }";
		}
		return new ForwardResolution(VIEW);
	}

}