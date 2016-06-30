package de.oglimmer.ggo.web.action;

import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Game;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;

public class RemoveAbandonedGameActionBean extends BaseAction {

	@Setter
	private String gameId;

	@DefaultHandler
	@DontValidate
	public Resolution remove() {
		Games.<Game> getGames().removeAbandonedGame(gameId);
		return new ErrorResolution(200);
	}

}