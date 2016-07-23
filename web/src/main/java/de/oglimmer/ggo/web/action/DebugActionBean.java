package de.oglimmer.ggo.web.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.oglimmer.atmospheremvc.com.AtmosphereResourceCache;
import de.oglimmer.atmospheremvc.com.AtmosphereResourceCache.Item;
import de.oglimmer.atmospheremvc.game.Game;
import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.phase.TutorialDelegateBasePhase;
import de.oglimmer.ggo.util.GridGameOneProperties;
import lombok.Getter;
import lombok.Setter;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

public class DebugActionBean extends BaseAction {

	private static final String VIEW_OVERVIEW = "/WEB-INF/jsp/debugOverview.jsp";

	@Getter
	@Setter
	@Validate(required = true)
	private String pass;

	@Getter
	private List<Item> atmosphereResources;

	@Getter
	private Collection<Game> games;

	@ValidationMethod
	public void validate(ValidationErrors errors) {
		if (!GridGameOneProperties.PROPERTIES.getRuntimePassword().equals(pass)) {
			errors.addGlobalError(new SimpleError("invalid password"));
		}
	}

	public Resolution resetGame() {
		Games.<Game> getGames().reset();
		return show();
	}

	@DefaultHandler
	public Resolution show() {
		atmosphereResources = AtmosphereResourceCache.INSTANCE.getItems();
		games = Games.<Game> getGames().getAllGames();
		return new ForwardResolution(VIEW_OVERVIEW);
	}

	public Collection<String> buildPhaseStack(Game game) {
		Collection<String> collectedPhases = new ArrayList<>();
		collectedPhases.add(game.getCurrentPhase().toString());
		if (game.getCurrentPhase() instanceof TutorialDelegateBasePhase) {
			TutorialDelegateBasePhase tdbp = (TutorialDelegateBasePhase) game.getCurrentPhase();
			while ((tdbp = tdbp.getNextPhase()) != null) {
				collectedPhases.add(tdbp.toString());
			}
		}
		return collectedPhases;
	}

	public Item getAtmosphereResources(Player p) {
		Optional<Item> item = atmosphereResources.stream().filter(ar -> ar.getPlayer() == p).findFirst();
		if (item.isPresent()) {
			return item.get();
		}
		return null;
	}

}