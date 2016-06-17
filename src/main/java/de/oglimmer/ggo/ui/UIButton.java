package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UIButton {

	// IMMUTABLE

	@Getter
	@Setter
	private String id;
	@Getter
	@Setter
	private String text;

	// CHANGABLE

	@Getter
	@Setter
	private DiffableBoolean hidden;

	public void copy(UIButton b) {
		this.id = b.getId();
		this.text = b.getText();
		this.hidden = b.getHidden();
	}

	public UIButton diffAndUpdate(UIButton newState) {
		UIButton diff = new UIButton();
		boolean changed = false;
		changed |= hidden.diffAndUpdate(newState.getHidden().getVal(), diff::setHidden);
		return changed ? diff : null;
	}

}
