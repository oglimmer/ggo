package de.oglimmer.ggo.web.action;

import java.util.Collection;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import lombok.Getter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class LandingGameQueryActionBean extends BaseAction {

	private static final String VIEW = "/WEB-INF/jsp/landingGameQuery.jsp";

	@Getter
	private Collection<Game> availableGames;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		availableGames = Games.INSTANCE.getOpenGames();
		return new ForwardResolution(VIEW);
	}

}