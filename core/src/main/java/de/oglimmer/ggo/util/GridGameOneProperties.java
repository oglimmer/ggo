package de.oglimmer.ggo.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import de.oglimmer.ggo.logic.util.RandomString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GridGameOneProperties extends AbstractProperties {

	public static final GridGameOneProperties PROPERTIES = new GridGameOneProperties();

	protected JsonObject createExtraInitAttributes() {
		if (getJson().getString("runtime.password", null) == null) {
			JsonObjectBuilder job = Json.createObjectBuilder();
			String randomStringHex = RandomString.getRandomStringHex(8);
			job.add("runtime.password", randomStringHex);
			log.info("Created random runtime.password: " + randomStringHex);
			return merge(getJson(), job.build());
		}
		return getJson();
	}

	protected GridGameOneProperties() {
		super("ggo.properties");
	}

	public String getRuntimePassword() {
		return getJson().getString("runtime.password");
	}

	public String getSmtpUser() {
		return getJson().getString("smtp.user");
	}

	public String getSmtpPassword() {
		return getJson().getString("smtp.password");
	}

	public String getSmtpHost() {
		return getJson().getString("smtp.host");
	}

	public int getSmtpPort() {
		return getJson().getInt("smtp.port");
	}

	public boolean getSmtpSSL() {
		return getJson().getBoolean("smtp.ssl");
	}

	public String getSmtpFrom() {
		return getJson().getString("smtp.from");
	}

	public String getDbUser() {
		return getJson().getString("db.user");
	}

	public String getDbPassword() {
		return getJson().getString("db.password");
	}

	public String getDbServerUrl() {
		return getJson().getString("db.url");
	}

	public String getDbSchema() {
		return getJson().getString("db.schema");
	}

	public String getDbParameter() {
		return getJson().getString("db.parameter");
	}

	public String getDbDriver() {
		return getJson().getString("db.driver");
	}

	public boolean isEmailDisabled() {
		return getJson().getBoolean("email.disabled");
	}

	public String getDomain() {
		return getJson().getString("app.domain");
	}

	public String getUrlPath() {
		return getJson().getString("app.urlPath");
	}

}
