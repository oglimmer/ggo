package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@ToString
public class UIMessages {

	@Setter
	@Getter
	private String title;
	@Setter
	@Getter
	private String info;
	@Setter
	@Getter
	private String error;

	public boolean hasChange() {
		return title != null || info != null || error != null;
	}

	public void clearError() {
		error = "";
	}

}
