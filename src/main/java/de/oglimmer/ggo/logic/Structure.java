package de.oglimmer.ggo.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Structure {
	public static final String CITY = "city";

	@Getter
	private String type;

	@Getter
	private Player player;

}
