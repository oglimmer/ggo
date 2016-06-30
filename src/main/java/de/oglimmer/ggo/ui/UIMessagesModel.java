package de.oglimmer.ggo.ui;

import lombok.Data;

/**
 * Server and client-side model for messages
 */
@Data
public class UIMessagesModel {

	private String score;
	private String title;
	private String info;
	private String error;

	public void clearErrorInfo() {
		error = null;
		info = null;
	}

}
