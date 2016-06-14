package de.oglimmer.ggo.logic;

import de.oglimmer.ggo.util.RandomString;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(exclude = { "player" })
public class Structure {
	public static final String CITY = "city";

	@Getter
	private String id = RandomString.getRandomStringHex(8);

	@Getter
	@NonNull
	private String type;

	@Getter
	@NonNull
	private Player player;

}
