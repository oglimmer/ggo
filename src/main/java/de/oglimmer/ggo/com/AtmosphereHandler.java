package de.oglimmer.ggo.com;

import static org.atmosphere.cpr.ApplicationConfig.MAX_INACTIVE;

import java.io.IOException;

import javax.inject.Inject;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.Heartbeat;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceImpl;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ManagedService(path = "/srvcom", atmosphereConfig = MAX_INACTIVE + "=120000")
public class AtmosphereHandler {

	@Inject
	private AtmosphereResource r;

	@Get
	public void init(AtmosphereResource r) {
		r.getResponse().setCharacterEncoding("UTF-8");
	}

	@Heartbeat
	public void onHeartbeat(final AtmosphereResourceEvent event) {
		log.debug("Heartbeat send by {}", event.getResource());
	}

	@Ready
	public void onReady(AtmosphereResource r) {
		log.debug("Browser {} connected", r.uuid());
		AtmosphereResourceCache.INSTANCE.connect((AtmosphereResourceImpl) r);
	}

	/*
	 * If you don't want to use injection AtmosphereResourceEvent event
	 */
	@Disconnect
	public void onDisconnect() {
		log.debug("Browser {} disconnect", r.uuid());
		AtmosphereResourceCache.INSTANCE.disconnect(r.uuid());
	}

	@Message(encoders = { JacksonEncoder.class }, decoders = { JacksonDecoder.class })
	public void onMessage(CommandMessage message) throws IOException {
		log.info("onMessage: {}", message);
		Game game = Games.INSTANCE.getGameByPlayerId(message.getPid());
		Player player = game.getPlayerById(message.getPid());
		assert player != null;
		if ("join".equals(message.getCmd())) {
			AtmosphereResourceCache.INSTANCE.registerPlayer(player, r.uuid());
		}
		game.getCurrentPhase().getMessages().clearMessages();
		game.getCurrentPhase().execCmd(player, message.getCmd(), message.getParam());
		game.getCurrentPhase().updateUI(player);
		game.getCurrentPhase().diffUIState(game);
		game.getCurrentPhase().getMessages().sendMessages();
	}

}
