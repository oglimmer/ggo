package de.oglimmer.ggo.web.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class CreateGameActionBean extends BaseAction {

	private static final String WAIT_VIEW = "/WEB-INF/jsp/createGame.jsp";

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		return new ForwardResolution(WAIT_VIEW);
	}

}