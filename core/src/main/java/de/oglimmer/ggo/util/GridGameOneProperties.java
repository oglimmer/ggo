package de.oglimmer.ggo.util;

public class GridGameOneProperties extends AbstractProperties {

	public static final GridGameOneProperties PROPERTIES = new GridGameOneProperties();

	protected GridGameOneProperties() {
		super("ggo.properties");
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
