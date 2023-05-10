package ch.flottesohle;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonReader;
import org.minimalj.frontend.impl.json.JsonWriter;
import org.minimalj.repository.query.By;

import ch.flottesohle.model.Location;

public class LocationMapDataProvider {
	private static final Logger LOG = Logger.getLogger(LocationMapDataProvider.class.getName());

	public String getLocationMapData() {
		List<Location> locations = Backend.find(Location.class, By.all());

		// https://nominatim.openstreetmap.org/search?country=CH&city=Bern&format=jsonv2

		List<Map<String, Object>> locs = new ArrayList<>();
		for (Location location : locations) {

			Map<String, Object> lMap = new HashMap<>();
			lMap.put("name", location.name);
			lMap.put("address", location.address);
			lMap.put("city", location.city);
			lMap.put("url", location.url);

			if (location.latitude == null) {
				location = getPosition(location);
			}
			if (location.latitude != null) {
				lMap.put("latitude", location.latitude);
				lMap.put("longitude", location.longitude);
				locs.add(lMap);
			}
		}

		String json = new JsonWriter().write(locs);
		return json;
	}

	private Location getPosition(Location location) {
		try {
			StringBuilder s = new StringBuilder("https://nominatim.openstreetmap.org/");
			s.append("?country=").append(URLEncoder.encode(location.country, "UTF-8"));
			s.append("&city=").append(URLEncoder.encode(location.city.substring(location.city.indexOf(' ') + 1), "UTF-8"));
			s.append("&street=").append(URLEncoder.encode(location.address, "UTF-8"));
			s.append("&format=jsonv2");

			URL url = new URL(s.toString());
			try (InputStreamReader isr = new InputStreamReader(url.openStream())) {
				List<?> result = (List<?>) JsonReader.read(isr);
				@SuppressWarnings("unchecked")
				Map<String, Object> values = (Map<String, Object>) result.get(0);
				location.latitude = new BigDecimal((String) values.get("lat"));
				location.longitude = new BigDecimal((String) values.get("lon"));
			}
			return Backend.save(location);
		} catch (Exception x) {
			LOG.warning("Could not retrieve position of " + location.name + " (" + x.getMessage() + ")");
			return location;
		}
	}
}
