package de.oglimmer.atmospheremvc.com;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum AtmosphereResourceCacheCleaner {
	INSTANCE;

	private static final int SECONDS_UNTIL_EXPIRARY = 60 * 60;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public void start() {
		executor.schedule(new Runnable() {
			@Override
			public void run() {
				AtmosphereResourceCache.INSTANCE.getItems().stream().filter(this::isNoPlayerAssignedForTooLong)
						.map(AtmosphereResourceCacheItem::getUuid).forEach(AtmosphereResourceCache.INSTANCE::remove);
			}

			private boolean isNoPlayerAssignedForTooLong(AtmosphereResourceCacheItem i) {
				return i.getPlayer() == null && expired(i.getCreated());
			}

			private boolean expired(Date created) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, -SECONDS_UNTIL_EXPIRARY);
				return created.before(cal.getTime());
			}
		}, 1L, TimeUnit.MINUTES);
	}

	public void shutdown() {
		executor.shutdown();
	}

}
