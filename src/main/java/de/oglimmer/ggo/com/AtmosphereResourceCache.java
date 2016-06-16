package de.oglimmer.ggo.com;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceImpl;

import de.oglimmer.ggo.logic.Player;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum AtmosphereResourceCache {
	INSTANCE;

	private List<Item> items = new ArrayList<>();

	@RequiredArgsConstructor
	@Data
	class Item {

		@NonNull
		private String uuid;

		@NonNull
		private WeakReference<AtmosphereResourceImpl> ar;

		private Player player;

		private boolean disconnected;
	}

	/**
	 * Called on initial connect an re-connect
	 * 
	 * @param r
	 */
	public void connect(AtmosphereResourceImpl r) {
		Item item = getItem(r.uuid());
		if (item == null) {
			// connect
			item = new Item(r.uuid(), new WeakReference<>(r));
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
	}

	public void disconnect(String uuid) {
		items.stream().filter(i -> i.getUuid().equals(uuid)).forEach(i -> i.setDisconnected(true));
	}

	private Item getItem(String uuid) {
		Optional<Item> findFirst = items.stream().filter(i -> i.getUuid().equals(uuid)).findFirst();
		if (findFirst.isPresent()) {
			log.debug("get item for uuid {}", uuid);
			return findFirst.get();
		}
		log.debug("NO Resource for uuid {}", uuid);
		return null;
	}

	public void registerPlayer(Player player, String uuid) {
		Item disconnectedPlayerItem = getItem(player);
		if (disconnectedPlayerItem != null) {
			remove(disconnectedPlayerItem.getUuid());
		}
		Item connectedUuidItem = getItem(uuid);
		if (connectedUuidItem.getPlayer() != player) {
			connectedUuidItem.setPlayer(player);
			log.debug("set on {} player {}", uuid, player.getSide());
		}
	}

	private void remove(String uuid) {
		for (Iterator<Item> it = items.iterator(); it.hasNext();) {
			Item i = it.next();
			if (i.getUuid().equals(uuid)) {
				it.remove();
			}
		}
	}

	public AtmosphereResource get(Player player) {
		Item findFirst = getItem(player);
		if (findFirst != null) {
			AtmosphereResourceImpl atmosphereResourceImpl = findFirst.getAr().get();
			log.debug("get Resource for player {} = {}", player.getSide(), atmosphereResourceImpl.uuid());
			return atmosphereResourceImpl;
		}
		log.debug("NO Resource for player {}", player.getSide());
		return null;
	}

	private Item getItem(Player player) {
		Optional<Item> findFirst = items.stream().filter(i -> i.getPlayer() == player).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

}
