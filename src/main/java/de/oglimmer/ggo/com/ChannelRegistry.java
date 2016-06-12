package de.oglimmer.ggo.com;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelRegistry {

	private Map<String, AtmosphereResource> map = new HashMap<>();

	public void register(String pid, AtmosphereResource r) {
		map.put(pid, r);
	}

	public AtmosphereResource get(String pid) {
		return map.get(pid);
	}

	public void remove(AtmosphereResource r) {
		for (Iterator<Map.Entry<String, AtmosphereResource>> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, AtmosphereResource> en = it.next();
			if (en.getValue().uuid().equals(r.uuid())) {
				it.remove();
				return;
			}
		}
		log.warn("Tried to remove {} from ChannelRegistry but not found", r.uuid());
	}

}
