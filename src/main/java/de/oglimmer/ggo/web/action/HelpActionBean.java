package de.oglimmer.ggo.web.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

public class HelpActionBean extends BaseAction {

	private static final String VIEW = "/WEB-INF/jsp/help.jsp";

	@DefaultHandler
	@DontValidate
	public Resolution join() {
		return new ForwardResolution(VIEW);
	}

}