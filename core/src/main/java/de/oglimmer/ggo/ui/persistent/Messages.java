package de.oglimmer.ggo.ui.persistent;

import java.io.Serializable;

import lombok.Data;

/**
 * Server and client-side model for messages
 */
@Data
public class Messages implements Serializable {

	private static final long serialVersionUID = 1L;

	private String score;
	private String title;
	private String info;
	private String error;

	public void clearErrorInfo() {
		error = null;
		info = null;
	}

	public String getJsClass() {
		return "Messages";
	};

}
