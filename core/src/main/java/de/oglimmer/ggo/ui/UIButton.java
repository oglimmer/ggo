package de.oglimmer.ggo.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class UIButton {

	// IMMUTABLE

	@Getter
	private String id;
	@Getter
	private String text;
	@Getter
	private String graphic;
	@Getter
	private Integer width;
	@Getter
	private Integer height;

	// CHANGABLE

	@Getter
	private Boolean hidden;

	public UIButton(UIButton b) {
		this.id = b.getId();
		this.text = b.getText();
		this.graphic = b.getGraphic();
		this.width = b.getWidth();
		this.height = b.getHeight();
		this.hidden = b.getHidden();
	}

}
