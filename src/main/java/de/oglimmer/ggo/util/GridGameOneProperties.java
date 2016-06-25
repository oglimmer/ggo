package de.oglimmer.ggo.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GridGameOneProperties {

	public static final GridGameOneProperties INSTANCE = new GridGameOneProperties();

	private static final boolean DEBUG = true;

	private static final String GGO_PROPERTIES = "ggo.properties";

	private JsonObject json = Json.createObjectBuilder().build();
	private boolean running = true;
	private Thread propertyFileWatcherThread;
	private List<Runnable> reloadables = new ArrayList<>();
	private String sourceLocation;

	private GridGameOneProperties() {
		init();
	}

	private void init() {
		loadDefaultProperties();
		sourceLocation = System.getProperty(GGO_PROPERTIES);
		if (sourceLocation != null) {
			try {
				if (sourceLocation.startsWith("memory:")) {
					String memoryConfigStr = sourceLocation.substring("memory:".length());
					try (InputStream is = new ByteArrayInputStream(memoryConfigStr.getBytes(StandardCharsets.UTF_8))) {
						mergeJson(is);
					}
				} else {
					try (InputStream fis = new FileInputStream(sourceLocation)) {
						mergeJson(fis);
					}
					if (propertyFileWatcherThread == null) {
						propertyFileWatcherThread = new Thread(new PropertyFileWatcher());
						propertyFileWatcherThread.start();
					}
				}
			} catch (IOException e) {
				log.error("Failed to load properties file " + sourceLocation, e);
			}
		}
		if (DEBUG) {
			System.out.println("Used config: " + prettyPrint(json));
		}
	}

	private void loadDefaultProperties() {
		try (JsonReader rdr = Json.createReader(this.getClass().getResourceAsStream("/default.properties"))) {
			json = rdr.readObject();
			System.out.println("Successfully loaded properties from /default.properties");
		}
	}

	private String prettyPrint(JsonObject json) {
		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);

		StringWriter sw = new StringWriter();
		try (JsonWriter jsonWriter = writerFactory.createWriter(sw)) {
			jsonWriter.writeObject(json);

		}
		return sw.toString();
	}

	private void mergeJson(InputStream is) {
		try (JsonReader rdr = Json.createReader(is)) {
			JsonObject toBeMerged = rdr.readObject();
			json = merge(json, toBeMerged);
			System.out.println("Successfully loaded " + GGO_PROPERTIES + " from " + sourceLocation + " for merge");
		}
	}

	private JsonObject merge(JsonObject base, JsonObject toOverwrite) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		for (Entry<String, JsonValue> entry : base.entrySet()) {
			String key = entry.getKey();
			if (toOverwrite.containsKey(key)) {
				job.add(key, toOverwrite.get(key));
			} else {
				job.add(key, entry.getValue());
			}
		}
		return job.build();
	}

	public String getSmtpUser() {
		return json.getString("smtp.user");
	}

	public String getSmtpPassword() {
		return json.getString("smtp.password");
	}

	public String getSmtpHost() {
		return json.getString("smtp.host");
	}

	public int getSmtpPort() {
		return json.getInt("smtp.port");
	}

	public boolean getSmtpSSL() {
		return json.getBoolean("smtp.ssl");
	}

	public String getSmtpFrom() {
		return json.getString("smtp.from");
	}

	public String getDbUser() {
		return json.getString("db.user");
	}

	public String getDbPassword() {
		return json.getString("db.password");
	}

	public String getDbServerUrl() {
		return json.getString("db.url");
	}

	public String getDbSchema() {
		return json.getString("db.schema");
	}

	public String getDbParameter() {
		return json.getString("db.parameter");
	}

	public String getDbDriver() {
		return json.getString("db.driver");
	}

	public boolean isEmailDisabled() {
		return json.getBoolean("email.disabled");
	}

	public String getDomain() {
		return json.getString("app.domain");
	}

	public void registerOnReload(Runnable toCall) {
		reloadables.add(toCall);
	}

	void reload() {
		init();
		reloadables.forEach(Runnable::run);
	}

	public void shutdown() {
		running = false;
		propertyFileWatcherThread.interrupt();
	}

	class PropertyFileWatcher implements Runnable {

		public void run() {
			File toWatch = new File(sourceLocation);
			log.info("PropertyFileWatcher started");
			try {
				final Path path = FileSystems.getDefault().getPath(toWatch.getParent());
				try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
					path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
					while (running) {
						final WatchKey wk = watchService.take();
						for (WatchEvent<?> event : wk.pollEvents()) {
							// we only register "ENTRY_MODIFY" so the context is always a Path.
							final Path changed = (Path) event.context();
							if (changed.endsWith(toWatch.getName())) {
								log.debug("{} changed => reload", toWatch.getAbsolutePath());
								reload();
							}
						}
						boolean valid = wk.reset();
						if (!valid) {
							log.warn("The PropertyFileWatcher's key has been unregistered.");
						}
					}
				}
			} catch (InterruptedException e) {
			} catch (Exception e) {
				log.error("PropertyFileWatcher failed", e);
			}
			log.info("PropertyFileWatcher ended");
		}
	}

}
