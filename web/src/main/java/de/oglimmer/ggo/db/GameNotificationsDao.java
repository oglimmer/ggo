package de.oglimmer.ggo.db;

import static de.oglimmer.ggo.db.DBAccess.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import de.oglimmer.ggo.logic.util.RandomString;
import de.oglimmer.ggo.util.GridGameOneProperties;
import lombok.SneakyThrows;

public enum GameNotificationsDao {
	INSTANCE;

	private GameNotificationsDao() {
		GridGameOneProperties.PROPERTIES.registerOnReload(() -> gamePropertiesChanged());
		gamePropertiesChanged();
	}

	@SneakyThrows(value = ClassNotFoundException.class)
	private void gamePropertiesChanged() {
		Class.forName(GridGameOneProperties.PROPERTIES.getDbDriver());
	}

	public int allConfirmed(Consumer<GameNotification> callback) {
		if (GridGameOneProperties.PROPERTIES.isEmailDisabled()) {
			return -1;
		}
		String query = "select id,email,createdOn,confirmed,confirmId from game_notification where confirmed is not null";
		return DB.execQuery(query, rs -> convertResultSetToGameNotification(callback, rs));
	}

	@SneakyThrows(value = SQLException.class)
	private void convertResultSetToGameNotification(Consumer<GameNotification> callback, ResultSet rs) {
		callback.accept(new GameNotification(rs));
	}

	public GameNotification addEmail(String email) {
		String confirmId = RandomString.getRandomStringHex(32);
		String query = " insert into game_notification (email, confirmId) values (?,?)";
		return new GameNotification(DB.executeUpdate(query, email, confirmId), email, confirmId);
	}

	public void unregisterEmail(String confirmId) {
		String query = "delete from game_notification where confirmId = ?";
		DB.executeUpdate(query, confirmId);
	}

	public void confirmEmail(String confirmId) {
		String query = "update game_notification set confirmed = now()  where confirmId = ? and confirmed is null";
		DB.executeUpdate(query, confirmId);
	}

	public GameNotification getByConfirmId(String confirmId) {
		String query = "select id,email,createdOn,confirmed,confirmId from game_notification where confirmId = ?";
		GameNotification[] retObj = new GameNotification[1];
		DB.execQuery(query, rs -> convertResultSetToGameNotification(gm -> retObj[0] = gm, rs), confirmId);
		return retObj[0];
	}

	public Collection<GameNotification> all() {
		Collection<GameNotification> coll = new ArrayList<>();
		String query = "select id,email,createdOn,confirmed,confirmId from game_notification";
		DB.execQuery(query, rs -> convertResultSetToGameNotification(gn -> coll.add(gn), rs));
		return coll;
	}

}
