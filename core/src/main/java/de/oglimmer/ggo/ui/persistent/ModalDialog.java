package de.oglimmer.ggo.ui.persistent;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

public class ModalDialog {

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
	public static class Option {

		private String id;
		private String description;

	}

}

/*
 * STATE proper server-side model, useful for real game objects other states availabe on the server (like conn status)
 * UI ELEMENTS (messages, dialogs) needs own data
 */