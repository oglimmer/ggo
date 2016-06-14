package de.oglimmer.ggo.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

@ToString
public class UIBoard {

	@Getter
	private Map<String, UIField> corToFields = new HashMap<>();
	@Getter
	private Map<String, UIUnit> idToUnits = new HashMap<>();
	@Getter
	private Set<String> unitsToRemove = new HashSet<>();
	@Getter
	private Map<String, UIHandItem> idToHanditems = new HashMap<>();
	@Getter
	private Set<String> handitemsToRemove = new HashSet<>();

	public boolean hasChange() {
		return !corToFields.isEmpty() || !idToUnits.isEmpty() || !unitsToRemove.isEmpty() || !idToHanditems.isEmpty()
				|| !handitemsToRemove.isEmpty();
	}

}
