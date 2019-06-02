package ch.openech.dancer.frontend;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.Frontend.IContent;
import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.impl.json.JsonReader;
import org.minimalj.frontend.impl.json.JsonWriter;
import org.minimalj.frontend.page.HtmlPage;
import org.minimalj.repository.query.By;
import org.minimalj.util.resources.Resources;

import ch.openech.dancer.model.Location;

public class LocationMapPage extends HtmlPage {

	private static String template;
	static {
		template = JsonFrontend.readStream(LocationMapPage.class.getResourceAsStream("/ch/openech/dancer/location_map.html"));
	}

	public LocationMapPage() {
		super(template, "Tanzkarte");
	}

	public String getTitle() {
		return Resources.getString(getClass());
	}

	@Override
	public IContent getContent() {
		String htmlOrUrl = JsonFrontend.readStream(LocationMapPage.class.getResourceAsStream("/ch/openech/dancer/location_map.html"));

		List<Location> locations = Backend.find(Location.class, By.all());

		// https://nominatim.openstreetmap.org/search?country=CH&city=Bern&format=jsonv2

		List<Map<String, Object>> locs = new ArrayList<>();
		for (Location l : locations) {
			Map<String, Object> lMap = new HashMap<>();
			lMap.put("name", l.name);
			lMap.put("address", l.address);
			lMap.put("city", l.city);
			lMap.put("url", l.url);

			if (l.latitude == null) {
				try {
					URIBuilder b = new URIBuilder("https://nominatim.openstreetmap.org");
					b.addParameter("country", "CH");
					b.addParameter("city", l.city.substring(l.city.indexOf(' ') + 1));
					b.addParameter("street", l.address);
					b.addParameter("format", "jsonv2");

					URL url = b.build().toURL();
					try (InputStreamReader isr = new InputStreamReader(url.openStream())) {
						List result = (List) JsonReader.read(isr);
						Map<String, Object> values = (Map<String, Object>) result.get(0);
						l.latitude = new BigDecimal((String) values.get("lat"));
						l.longitude = new BigDecimal((String) values.get("lon"));
					}
					Backend.update(l);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			lMap.put("latitude", l.latitude);
			lMap.put("longitude", l.longitude);

			locs.add(lMap);
		}

		String json = new JsonWriter().write(locs);

		htmlOrUrl = htmlOrUrl.replace("$LOCS", json);

		return Frontend.getInstance().createHtmlContent(htmlOrUrl);
	}

}
