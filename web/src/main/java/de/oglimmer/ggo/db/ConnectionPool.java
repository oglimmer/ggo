package de.oglimmer.ggo.db;

import static de.oglimmer.ggo.util.GridGameOneProperties.PROPERTIES;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionPool {
	public static final ConnectionPool INSTANCE = new ConnectionPool();

	private ComboPooledDataSource cpds;

	private ConnectionPool() {
		PROPERTIES.registerOnReload(this::init);
		init();
	}

	public synchronized void shutdown() {
		if (cpds != null) {
			log.debug("Shutdown c3p0 connection pool");
			try {
				final int WAIT_TIME = 5;
				long c = 0; // wait for max 30 sec
				while (cpds.getNumBusyConnections() > 0 && c < 30_000) {
					TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
					c += WAIT_TIME;
				}
			} catch (SQLException | InterruptedException e) {
				// SQLException can actually never happen
				// In case of InterruptedException we just proceed
			}
			cpds.close();
			cpds = null;
		}
	}

	@SneakyThrows(value = PropertyVetoException.class)
	private synchronized void init() {
		shutdown();
		String jdbcUrl = buildUrl();
		log.debug("Open c3p0 connection pool for {}:{}", jdbcUrl, PROPERTIES.getDbUser());
		cpds = new ComboPooledDataSource();
		cpds.setDriverClass(PROPERTIES.getDbDriver());
		cpds.setJdbcUrl(jdbcUrl);
		cpds.setUser(PROPERTIES.getDbUser());
		cpds.setPassword(PROPERTIES.getDbPassword());
	}

	public synchronized Connection getCon() throws SQLException {
		return cpds.getConnection();
	}

	private String buildUrl() {
		String url = PROPERTIES.getDbServerUrl() + PROPERTIES.getDbSchema();
		if (PROPERTIES.getDbParameter() != null) {
			url += "?" + PROPERTIES.getDbParameter();
		}
		return url;
	}

}
