package de.oglimmer.ggo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class GameNotification {

	private int id;
	private String email;
	private Timestamp createdOn;
	private Timestamp confirmed;
	private String confirmId;

	public GameNotification(int id, String email, String confirmId) {
		this.id = id;
		this.email = email;
		this.confirmId = confirmId;
	}

	public GameNotification(ResultSet rs) throws SQLException {
		this.id = rs.getInt("id");
		this.email = rs.getString("email");
		this.createdOn = rs.getTimestamp("createdOn");
		this.confirmed = rs.getTimestamp("confirmed");
		this.confirmId = rs.getString("confirmId");
	}
}