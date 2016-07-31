package de.oglimmer.atmospheremvc.com;

import java.lang.ref.WeakReference;
import java.util.Date;

import org.atmosphere.cpr.AtmosphereResourceImpl;

import de.oglimmer.atmospheremvc.game.Player;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AtmosphereResourceCacheItem {

	private Date created = new Date();

	@NonNull
	private String uuid;

	@NonNull
	private WeakReference<AtmosphereResourceImpl> ar;

	private Player player;

	private boolean disconnected;

	public String toString() {
		return "Item [uuid=" + uuid + ", ar=" + (ar != null ? ar.hashCode() : null) + ", player="
				+ (player != null ? player.getId() : null) + ", disconnected=" + disconnected + ", created=" + created
				+ "]";
	}
}