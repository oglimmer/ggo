package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.db.GameNotifications;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;

public class ConfirmEmailActionBean extends BaseAction {

	@Setter
	private String confirmId;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		GameNotifications.INSTANCE.confirmEmail(confirmId);
		getContext().getMessages().add(new SimpleMessage(
				"Your email address is now confirmed. We'll send you notifcations when someone craates a new game. Stay tuned."));
		return new RedirectResolution(LandingActionBean.class);
	}

}