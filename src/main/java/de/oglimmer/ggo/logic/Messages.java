package de.oglimmer.ggo.logic;

import lombok.Data;

/**
 * Server and client-side model for messages
 */
@Data
public class Messages {

	private String score;
	private String title;
	private String info;
	private String error;

	public void clearErrorInfo() {
		error = null;
		info = null;
	}

}
