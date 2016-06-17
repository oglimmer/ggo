package de.oglimmer.ggo.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@ToString
public class UIMessages {

	private MemorizingString title = new MemorizingString();
	private MemorizingString info = new MemorizingString();
	private MemorizingString error = new MemorizingString();

	public String getTitle() {
		return title.getCurrentValue();
	}

	public void setTitle(String title) {
		this.title.setCurrentValue(title);
	}

	public String getInfo() {
		return info.getCurrentValue();
	}

	public void setInfo(String info) {
		this.info.setCurrentValue(info);
	}

	public String getError() {
		return error.getCurrentValue();
	}

	public void setError(String error) {
		this.error.setCurrentValue(error);
	}

	public boolean hasChange() {
		return title.hasChange() || info.hasChange() || error.hasChange();
	}

	public void clearError() {
		error.setCurrentValue("");
	}

	public UIMessages calcDiffMessages() {
		UIMessages transfer = new UIMessages();
		transfer.setTitle(title.calcDiffMessages());
		transfer.setInfo(info.calcDiffMessages());
		transfer.setError(error.calcDiffMessages());
		return transfer;
	}

}
