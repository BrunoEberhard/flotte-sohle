package ch.openech.dancer.frontend;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.impl.json.JsonWriter;
import org.minimalj.frontend.page.HtmlPage;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.DeeJay;
import ch.openech.dancer.model.Location;

public class EventPage extends HtmlPage {

	private final Object id;

	public EventPage(Object id) {
		super(createHtml(id));
		this.id = id;
	}

	@Override
	public String getTitle() {
		// TODO DanceEvent nur einmal lesen
		DanceEvent event = Backend.read(DanceEvent.class, id);
		return event.header;
	}

	public Object getId() {
		return id;
	}

	private static String template;
	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	static {
		template = JsonFrontend.readStream(EventPage.class.getResourceAsStream("/ch/openech/dancer/event.html"));
	}

	public static String createHtml(Object id) {
		DanceEvent event = Backend.read(DanceEvent.class, id);
		return fillTemplate(event);
	}

	private static String fillTemplate(DanceEvent event) {
		String result = template.replace("$title", event.title);
		result = result.replace("$description", event.description != null ? event.description : "");
		result = result.replace("$fromUntil", event.getFromUntil());
		if (event.location != null) {
			result = result.replace("$location", event.location.name);
			result = result.replace("$address", StringUtils.emptyIfNull(event.location.address));
			result = result.replace("$city", StringUtils.emptyIfNull(event.location.city));
		} else {
			result = result.replace("$location", "");
			result = result.replace("$address", "");
			result = result.replace("$city", "");
		}

		String date = event.getDayOfWeek() + ", " + shortFormat.format(event.date);
		result = result.replace("$date", date);

		if (!StringUtils.isEmpty(event.location.url)) {
			result = result.replace("$url", event.location.url);
		}
		
		result = result.replace("$json", createJson(event));
		return result;
	}
	
	private static String createJson(DanceEvent event) {
		JsonWriter writer = new JsonWriter();
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@context", "http://schema.org");
		values.put("@type", "DanceEvent");
		if (event.location != null) {
			values.put("organizer", createJsonOrganization(event.location));
			values.put("location", createJsonPlace(event.location));
		}
		values.put("name", event.title);
		if (event.price != null) {
			values.put("offers", createJsonOffer(event.price));
		}
		values.put("startDate", event.date.toString());
		values.put("endDate", event.date.toString());
		if (event.description != null) {
			values.put("description", event.description);
		}
		values.put("doorTime", event.from.toString());
		if (event.until != null) {
			Duration duration = Duration.between(event.from, event.until);
			if (duration.isNegative()) {
				duration = duration.plusDays(1);
			}
			values.put("duration", duration.toString());
		}
		if (event.deeJay != null) {
			values.put("performer", createJsonPerson(event.deeJay));
		}
		values.put("image", "https://www.flotte-sohle.ch/sohle_rot.png");
		values.put("url", "https://www.flotte-sohle.ch/event/" + event.id);
		return writer.write(values);
	}
	
	private static Map<String, Object> createJsonOrganization(Location location) {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@type", "Organization");
		values.put("name", location.name);
		values.put("url", location.url);
		return values;
	}
	
	private static Map<String, Object> createJsonPlace(Location location) {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@type", "Place");
		values.put("name", location.name);
		values.put("address", createJsonPostalAddress(location));
		return values;
	}
	
	private static Map<String, Object> createJsonPostalAddress(Location location) {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@type", "PostalAddress");
		values.put("addressLocality", location.city);
		values.put("streetAddress", location.address);
		values.put("addressCountry", location.country);
		return values;
	}

	private static Map<String, Object> createJsonOffer(BigDecimal price) {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@type", "Offer");
		values.put("price", price.toPlainString());
		values.put("priceCurrency", "CHF");
		return values;
	}
	
	private static Map<String, Object> createJsonPerson(DeeJay deeJay) {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@type", "Person");
		values.put("name", deeJay.name);
		if (deeJay.url != null) {
			values.put("url", deeJay.url);
		}
		return values;
	}
	
}
