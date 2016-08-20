package de.oglimmer.ggo.log;

import de.oglimmer.utils.BaseConfigurator;

/**
 * Checks first in -D"APP_NAME"-logback, then /etc/logback-custom.xml, then $CLASSPATH/logback-custom.xml
 *
 */
public class Configurator extends BaseConfigurator {

	public Configurator() {
		super("ggo");
	}

}
