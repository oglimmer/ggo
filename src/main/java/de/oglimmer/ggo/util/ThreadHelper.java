package de.oglimmer.ggo.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadHelper {

	private ThreadHelper() {

	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			log.error(e.toString(), e);
		}
	}
}
