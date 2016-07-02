package de.oglimmer.ggo.ui.persistent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
public class UIButton {

	// IMMUTABLE

	public String getJsClass() {
		return "Button";
	};

	@Getter
	@NonNull
	private String id;
	@Getter
	private String text;
	@Getter
	private String graphic;
	@Getter
	@NonNull
	private Integer width;
	@Getter
	@NonNull
	private Integer height;

	// CHANGABLE

	@Getter
	@NonNull
	private Boolean hidden;

}
