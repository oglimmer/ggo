package de.oglimmer.atmospheremvc.com;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceImpl;

import de.oglimmer.atmospheremvc.game.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum AtmosphereResourceCache {
	INSTANCE;

	private List<AtmosphereResourceCacheItem> items = Collections.synchronizedList(new ArrayList<>());

	public List<AtmosphereResourceCacheItem> getItems() {
		return new ArrayList<>(items);
	}

	/**
	 * Called on initial connect an re-connect
	 * 
	 * @param r
	 */
	public void connect(AtmosphereResourceImpl r) {
		AtmosphereResourceCacheItem item = getItem(r.uuid());
		if (item == null) {
			// connect
			item = new AtmosphereResourceCacheItem(r.uuid(), new WeakReference<>(r));
			items.add(item);
			log.debug("connect {}", item);
		} else if (item.getAr().get() != r) {
			// re-connect with different AtmosphereResourceImpl
			item.setAr(new WeakReference<>(r));
			log.debug("re-connect {}", item);
		} else {
			assert false;
		}
		item.setDisconnected(false);

		updateOtherPlayer(item);
		updateLastConnectionTime(item);
	}

	private void updateLastConnectionTime(AtmosphereResourceCacheItem item) {
		Player actingPlayer = item.getPlayer();
		if (actingPlayer != null) {
			actingPlayer.setLastConnection(new Date());
		}
	}

	public void disconnect(String uuid) {
		items.stream().filter(i -> i.getUuid().equals(uuid)).forEach(i -> i.setDisconnected(true));
		AtmosphereResourceCacheItem item = getItem(uuid);
		if (item != null) {
			updateOtherPlayer(item);
			updateLastConnectionTime(item);
		}
	}

	private void updateOtherPlayer(AtmosphereResourceCacheItem item) {
		Player actingPlayer = item.getPlayer();
		if (actingPlayer != null) {
			Player remainingPlayer = actingPlayer.getGame().getOtherPlayer(actingPlayer);
			if (remainingPlayer != null) {
				remainingPlayer.updateUI();
			}
		}
	}

	private AtmosphereResourceCacheItem getItem(String uuid) {
		Optional<AtmosphereResourceCacheItem> findFirst = items.stream().filter(i -> i.getUuid().equals(uuid))
				.findFirst();
		if (findFirst.isPresent()) {
			log.debug("get item for uuid {}", uuid);
			return findFirst.get();
		}
		log.debug("NO Resource for uuid {}", uuid);
		return null;
	}

	public boolean isConnected(Player player) {
		AtmosphereResourceCacheItem item = getItem(player);
		if (item != null) {
			return !item.isDisconnected();
		}
		return false;
	}

	public void registerPlayer(Player player, String uuid) {
		AtmosphereResourceCacheItem disconnectedPlayerItem = getItem(player);
		if (disconnectedPlayerItem != null) {
			remove(disconnectedPlayerItem.getUuid());
		}
		AtmosphereResourceCacheItem connectedUuidItem = getItem(uuid);
		if (connectedUuidItem != null && connectedUuidItem.getPlayer() != player) {
			connectedUuidItem.setPlayer(player);
			log.debug("set on {} player {}", uuid, player.getSide());
		}
	}

	public void remove(String uuid) {
		for (Iterator<AtmosphereResourceCacheItem> it = items.iterator(); it.hasNext();) {
			AtmosphereResourceCacheItem i = it.next();
			if (i.getUuid().equals(uuid)) {
				it.remove();
			}
		}
	}

	public AtmosphereResource get(Player player) {
		AtmosphereResourceCacheItem findFirst = getItem(player);
		if (findFirst != null) {
			AtmosphereResourceImpl atmosphereResourceImpl = findFirst.getAr().get();
			log.debug("get Resource for player {} = {}", player.getSide(), atmosphereResourceImpl.uuid());
			return atmosphereResourceImpl;
		}
		log.debug("NO Resource for player {}", player.getSide());
		return null;
	}

	public AtmosphereResourceCacheItem getItem(Player player) {
		Optional<AtmosphereResourceCacheItem> findFirst = items.stream().filter(i -> i.getPlayer() == player)
				.findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

}
