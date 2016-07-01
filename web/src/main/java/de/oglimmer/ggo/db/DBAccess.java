package de.oglimmer.ggo.db;

import static de.oglimmer.ggo.util.GridGameOneProperties.PROPERTIES;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DBAccess {
	DB;

	<R> R execOnCon(Function<Connection, R> fkt) {
		try (Connection conn = getConnection(buildUrl())) {
			return fkt.apply(conn);
		} catch (SQLException e) {
			log.error("Failed to exec query", e);
			return null;
		}
	}

	int execQuery(String query, Consumer<ResultSet> callback, Object... params) {
		return DB.execOnCon(con -> {
			try {
				try (PreparedStatement preparedStmt = con.prepareStatement(query)) {
					int i = 1;
					for (Object param : params) {
						preparedStmt.setObject(i++, param);
					}
					try (ResultSet rs = preparedStmt.executeQuery()) {
						int count = 0;
						while (rs.next()) {
							count++;
							callback.accept(rs);
						}
						return count;
					}
				}
			} catch (SQLException e) {
				log.error("Failed to execQuery", e);
			}
			return 0;
		});
	}

	int executeUpdate(String sql, Object... params) {
		return DB.execOnCon(con -> {
			try {
				try (PreparedStatement preparedStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
					int i = 1;
					for (Object param : params) {
						preparedStmt.setObject(i++, param);
					}
					preparedStmt.executeUpdate();
					try (ResultSet rs = preparedStmt.getGeneratedKeys()) {
						if (rs.next()) {
							return rs.getInt(1);
						}
					}
				}
			} catch (SQLException e) {
				log.error("Failed to executeUpdate", e);
			}
			return 0;
		});
	}

	private Connection getConnection(String url) throws SQLException {
		return DriverManager.getConnection(url, PROPERTIES.getDbUser(), PROPERTIES.getDbPassword());
	}

	private String buildUrl() {
		String url = PROPERTIES.getDbServerUrl() + PROPERTIES.getDbSchema();
		if (PROPERTIES.getDbParameter() != null) {
			url += "?" + PROPERTIES.getDbParameter();
		}
		return url;
	}

}
