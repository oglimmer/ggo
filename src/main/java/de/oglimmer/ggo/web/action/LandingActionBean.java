package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.logic.Games;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class LandingActionBean extends BaseAction {

	private static final String VIEW = "/WEB-INF/jsp/landing.jsp";

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		return new ForwardResolution(VIEW);
	}

	@DontValidate
	public Resolution resetGame() {
		Games.INSTANCE.reset();
		return new ForwardResolution(VIEW);
	}
}