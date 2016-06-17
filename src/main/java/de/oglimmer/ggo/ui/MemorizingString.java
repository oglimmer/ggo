package de.oglimmer.ggo.ui;

import lombok.Getter;
import lombok.Setter;

public class MemorizingString {

	@Getter
	@Setter
	private String currentValue;

	private String memory;

	public String calcDiffMessages() {
		if (different()) {
			memory = currentValue;
			return currentValue;
		}
		return null;
	}

	private boolean different() {
		if (currentValue == null && memory == null) {
			return false;
		}
		if (currentValue != null && currentValue.equals(memory)) {
			return false;
		}
		return true;
	}

	public boolean hasChange() {
		return currentValue != null;
	}

}
