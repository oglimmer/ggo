package de.oglimmer.ggo.ui.persistent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

public class ModalDialog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private boolean show;

	@Getter
	@Setter
	private String title;

	@Getter
	private List<Option> options = new ArrayList<>();

	public String getJsClass() {
		return "ModalDialog";
	};

	@Value
	public static class Option implements Serializable {

		private static final long serialVersionUID = 1L;

		private String id;
		private String description;

	}

}

/*
 * STATE proper server-side model, useful for real game objects other states
 * availabe on the server (like conn status) UI ELEMENTS (messages, dialogs)
 * needs own data
 */