package de.oglimmer.ggo.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.slf4j.impl.StaticLoggerBinder;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * Checks first in -D"APP_NAME"-logback, then /etc/logback-custom.xml, then
 * $CLASSPATH/logback-custom.xml
 *
 */
public class Configurator extends ContextAwareBase implements ch.qos.logback.classic.spi.Configurator {

	private static final String CP_LOGBACK_CUSTOM_XML = "/logback-custom.xml";
	private static final String ETC_LOGBACK_CUSTOM_XML = "/etc/logback-custom.xml";
	private static final String APP_NAME = "ggo";

	@Override
	public void configure(LoggerContext loggerContext) {
		addInfo("Setting up custom configuration.");
		LoggerContext context = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(context);
		context.reset();
		context.putProperty("application-name", APP_NAME);
		openStream(is -> configure(jc, is));
	}

	private void configure(JoranConfigurator jc, InputStream is) {
		try {
			jc.doConfigure(is);
		} catch (JoranException e) {
			addError("Failed to configure JoranConfigurator", e);
		}
	}

	private void openStream(Consumer<InputStream> consumer) {
		InputStream is = null;
		try {
			is = lookSystemPropertyReferencedFile();
			if (is == null) {
				is = lookForEtcFile();
			}
			if (is == null) {
				is = lookForClasspathFile();
			}
			if (is != null) {
				consumer.accept(is);
			} else {
				addError("NO LOGBACK-CUSTOM.XML FOUND! NO LOGGING INITIALIZED.");
			}
		} catch (FileNotFoundException e) {
			addError("Failed to load file", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					addError("Failed to close input stream", e);
				}
			}
		}
	}

	private InputStream lookForClasspathFile() {
		InputStream is = getClass().getResourceAsStream(CP_LOGBACK_CUSTOM_XML);
		if (is != null) {
			addInfo("Could find resource [CP:" + CP_LOGBACK_CUSTOM_XML + "]");
		} else {
			addError("Could NOT find resource [CP:" + CP_LOGBACK_CUSTOM_XML + "]");
		}
		return is;
	}

	private InputStream lookForEtcFile() throws FileNotFoundException {
		File etcFile = new File(ETC_LOGBACK_CUSTOM_XML);
		if (etcFile.exists()) {
			addInfo("Could find resource [file:" + ETC_LOGBACK_CUSTOM_XML + "]");
			return new FileInputStream(etcFile);
		} else {
			addInfo("Could NOT find resource [file:" + ETC_LOGBACK_CUSTOM_XML + "]");
		}
		return null;
	}

	private InputStream lookSystemPropertyReferencedFile() throws FileNotFoundException {
		String systemProperty = System.getProperty(APP_NAME + "-logback");
		if (systemProperty != null) {
			addInfo("Could find resource reference [-D" + APP_NAME + "-logback = " + systemProperty + "]");
			File customFile = new File(systemProperty);
			if (customFile.exists()) {
				addInfo("Could find resource file [file:" + systemProperty + "]");
				return new FileInputStream(customFile);
			} else {
				addError("File file:" + systemProperty + " does NOT exist.");
			}
		} else {
			addInfo("Could NOT find resource reference [-D" + APP_NAME + "-logback]");
		}
		return null;
	}

}
