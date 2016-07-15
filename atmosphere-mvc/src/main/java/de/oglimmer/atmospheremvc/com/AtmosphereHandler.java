package de.oglimmer.atmospheremvc.com;

import static org.atmosphere.cpr.ApplicationConfig.MAX_INACTIVE;

import java.io.IOException;

import javax.inject.Inject;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceImpl;

import de.oglimmer.atmospheremvc.game.Game;
import de.oglimmer.atmospheremvc.game.Games;
import de.oglimmer.atmospheremvc.game.Player;
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
		log.debug("onMessage: {}", message);
		Game game = Games.<Game>getGames().getGameByPlayerId(message.getPid());
		if (game == null) {
			// @TODO: send 'game not exists'
		} else {
			Player player = game.getPlayerById(message.getPid());
			assert player != null;
			if ("join".equals(message.getCmd())) {
				AtmosphereResourceCache.INSTANCE.registerPlayer(player, r.uuid());
			}
			game.getCurrentPhase().execCmd(player, message.getCmd(), message.getParam());
			game.getCurrentPhase().updateMessages();
			game.getCurrentPhase().updateModalDialgs();
			MessageQueue messages = new MessageQueue(game);
			messages.process();
		}
	}

}
