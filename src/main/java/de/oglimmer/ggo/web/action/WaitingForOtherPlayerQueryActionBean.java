package de.oglimmer.ggo.web.action;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.JsonResolution;
import net.sourceforge.stripes.action.Resolution;

public class WaitingForOtherPlayerQueryActionBean extends BaseAction {

	@Setter
	private String gameId;

	@DefaultHandler
	@DontValidate
	public Resolution show() {
		Game game = Games.INSTANCE.getGameById(gameId);
		Result resultJson;
		if (game.getPlayers().size() == 2) {
			resultJson = new Result("redirect");
		} else {
			resultJson = new Result("wait");
		}
		return new JsonResolution(resultJson);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	class Result {
		private String action;
	}

}