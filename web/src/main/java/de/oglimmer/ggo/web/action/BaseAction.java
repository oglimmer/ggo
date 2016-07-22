package de.oglimmer.ggo.web.action;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import lombok.Getter;
import lombok.Setter;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.controller.LifecycleStage;

public abstract class BaseAction implements ActionBean {

	private static final int DATEFORMAT = DateFormat.FULL;

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
			String commit;
			String gitUrl;
			String version;
			String creationDate;
			try (InputStream is = new FileInputStream(
					getContext().getServletContext().getRealPath("/META-INF/MANIFEST.MF"))) {
				Manifest mf = new Manifest(is);
				Attributes attr = mf.getMainAttributes();
				commit = attr.getValue("git-commit");
				gitUrl = attr.getValue("git-url");
				version = attr.getValue("GGO-Version");
				long time = Long.parseLong(attr.getValue("Creation-Date"));
				creationDate = DateFormat.getDateTimeInstance(DATEFORMAT, DATEFORMAT).format(new Date(time));
			} catch (Exception e) {
				commit = "?";
				gitUrl = "?";
				creationDate = DateFormat.getDateTimeInstance(DATEFORMAT, DATEFORMAT).format(new Date());
				version = "?";
			}

			longVersionCache = "V" + version + " [<a href='" + gitUrl + "/commits/ggo-" + version + "'>Commit#" + commit
					+ "</a>] build " + creationDate;
		}

		longVersion = longVersionCache;
	}
}