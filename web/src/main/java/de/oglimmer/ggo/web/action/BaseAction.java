package de.oglimmer.ggo.web.action;

import de.oglimmer.utils.VersionFromManifest;
import lombok.Getter;
import lombok.Setter;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.controller.LifecycleStage;

public abstract class BaseAction implements ActionBean {

	private static String longVersionCache;

	@Getter
	@Setter
	private ActionBeanContext context;

	@Getter
	@Setter
	private String longVersion;

	@Before(stages = LifecycleStage.BindingAndValidation)
	public void retrieveVersion() {
		if (longVersionCache == null) {
			VersionFromManifest vfm = new VersionFromManifest();
			vfm.initFromFile(getContext().getServletContext().getRealPath("/META-INF/MANIFEST.MF"));
			longVersionCache = vfm.getLongVersion();
		}

		longVersion = longVersionCache;
	}
}