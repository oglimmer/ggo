package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.db.GameNotifications;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

public class UnregisterEmailActionBean extends BaseAction {

	@Setter
	private String confirmId;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		GameNotifications.INSTANCE.unregisterEmail(confirmId);
		return new RedirectResolution(LandingActionBean.class);
	}

}