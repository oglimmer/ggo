package de.oglimmer.ggo.com;

import static org.atmosphere.cpr.ApplicationConfig.MAX_INACTIVE;

import java.io.IOException;
import java.util.WeakHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.Heartbeat;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Games;
import de.oglimmer.ggo.logic.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ManagedService(path = "/srvcom", atmosphereConfig = MAX_INACTIVE + "=120000")
public class AtmosphereHandler {

	/**
	 * Later broadcast methods need a AtmosphereResourceImpl. As the DPI only inserts Proxy object, we need to safe the AtmosphereResourceImpl in the
	 * onReady event.
	 */
	private static WeakHashMap<String, AtmosphereResource> atmosphereResourceStore = new WeakHashMap<>();

	@Inject
	private BroadcasterFactory factory;

	@Inject
	@Named("/srvcom")
	private Broadcaster broadcaster;

	@Inject
	private AtmosphereResource r;

	@Inject
	private AtmosphereResourceEvent event;

	@Get
	public void init(AtmosphereResource r) {
		r.getResponse().setCharacterEncoding("UTF-8");
	}

	@Heartbeat
	public void onHeartbeat(final AtmosphereResourceEvent event) {
		log.trace("Heartbeat send by {}", event.getResource());
	}

	@Ready
	public void onReady(AtmosphereResource r) {
		log.debug("Browser {} connected", r.uuid());
		log.debug("BroadcasterFactory used {}", factory.getClass().getName());
		log.debug("Broadcaster injected {}", broadcaster.getID());
		atmosphereResourceStore.put(r.uuid(), r);
	}

	@Disconnect
	public void onDisconnect(/*
								 * If you don't want to use injection
								 * AtmosphereResourceEvent event
								 */) {
		if (event.isCancelled()) {
			log.debug("Browser {} unexpectedly disconnected", event.getResource().uuid());
		} else if (event.isClosedByClient()) {
			log.debug("Browser {} closed the connection", event.getResource().uuid());
		}
		if (atmosphereResourceStore.remove(r.uuid()) == null) {
			log.warn("Tried to remove {} from atmosphereResourceStore but not found.", r.uuid());
		}
		Games.INSTANCE.removeAtmosphereResource(r);
	}

	@Message(encoders = { JacksonEncoder.class }, decoders = { JacksonDecoder.class })
	public void onMessage(CommandMessage message) throws IOException {
		log.info("onMessage: {}", message);
		Game game = Games.INSTANCE.getGameByPlayerId(message.getPid());
		if ("join".equals(message.getCmd())) {
			AtmosphereResource ar = atmosphereResourceStore.get(r.uuid());
			assert ar != null;
			game.getChannelRegistry().register(message.getPid(), ar);
		}
		Player player = game.getPlayerById(message.getPid());
		assert player != null;
		game.getCurrentPhase().getMessages().clearMessages();
		game.getCurrentPhase().execCmd(player, message.getCmd(), message.getParam());
		game.getCurrentPhase().updateUI(player);
		game.getCurrentPhase().diffUIState(game);
		game.getCurrentPhase().getMessages().sendMessages();
	}

}
