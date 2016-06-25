package de.oglimmer.ggo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.function.Function;

import de.oglimmer.ggo.util.GridGameOneProperties;
import de.oglimmer.ggo.util.RandomString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum GameNotifications {
	INSTANCE;

	private GameNotifications() {
		GridGameOneProperties.INSTANCE.registerOnReload(() -> gamePropertiesChanged());
		gamePropertiesChanged();
	}

	private void gamePropertiesChanged() {
		try {
			Class.forName(GridGameOneProperties.INSTANCE.getDbDriver());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void allConfirmed(Consumer<GameNotification> callback) {
		execQuery(con -> {
			try {
				String query = "select id,email,confirmId from game_notification where confirmed is not null";
				try (PreparedStatement preparedStmt = con.prepareStatement(query)) {
					try (ResultSet rs = preparedStmt.executeQuery()) {
						while (rs.next()) {
							callback.accept(new GameNotification(rs.getInt("id"), rs.getString("email"),
									rs.getString("confirmId")));
						}
					}
				}
			} catch (SQLException e) {
				log.error("Failed to load allConfirmed", e);
			}
			return 0;
		});
	}

	public GameNotification addEmail(String email) {
		return execQuery(con -> {
			try {
				String confirmId = RandomString.getRandomStringHex(32);
				String query = " insert into game_notification (email, confirmId) values (?,?)";
				try (PreparedStatement preparedStmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
					preparedStmt.setString(1, email);
					preparedStmt.setString(2, confirmId);
					preparedStmt.executeUpdate();
					try (ResultSet rs = preparedStmt.getGeneratedKeys()) {
						rs.next();
						return new GameNotification(rs.getInt(1), email, confirmId);
					}
				}
			} catch (SQLException e) {
				log.error("Failed to addEmail", e);
			}
			return null;
		});
	}

	public void confirmEmail(String confirmId) {
		execQuery(con -> {
			try {
				String query = "update game_notification set confirmed = now()  where confirmId = ? and confirmed is null";
				try (PreparedStatement preparedStmt = con.prepareStatement(query)) {
					preparedStmt.setString(1, confirmId);
					preparedStmt.executeUpdate();
				}
			} catch (SQLException e) {
				log.error("Failed to confirmEmail", e);
			}
			return 0;
		});
	}

	private <R> R execQuery(Function<Connection, R> fkt) {
		String url = GridGameOneProperties.INSTANCE.getDbServerUrl() + GridGameOneProperties.INSTANCE.getDbSchema();
		if (GridGameOneProperties.INSTANCE.getDbParameter() != null) {
			url += "?" + GridGameOneProperties.INSTANCE.getDbParameter();
		}
		try (Connection conn = DriverManager.getConnection(url, GridGameOneProperties.INSTANCE.getDbUser(),
				GridGameOneProperties.INSTANCE.getDbPassword())) {
			return fkt.apply(conn);
		} catch (SQLException e) {
			log.error("Failed to exec query", e);
		}
		return null;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public class GameNotification {
		private int id;
		private String email;
		private String confirmId;
	}

}
