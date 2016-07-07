package de.oglimmer.ggo.logic;

import java.io.Serializable;

import de.oglimmer.ggo.logic.util.RandomString;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(exclude = { "id", "player" })
public class Structure implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	private String id = RandomString.getRandomStringHex(8);

	@Getter
	@NonNull
	private StructureType type;

	@Getter
	@NonNull
	private Player player;

}
