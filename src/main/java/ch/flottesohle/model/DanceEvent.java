package ch.flottesohle.model;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.minimalj.frontend.impl.json.JsonWriter;
import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.Decimal;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Searched;
import org.minimalj.model.annotation.Size;
import org.minimalj.model.annotation.Width;
import org.minimalj.util.LocaleContext;

import ch.flottesohle.backend.EventUpdateCounter;

public class DanceEvent {
	public static final DanceEvent $ = Keys.of(DanceEvent.class);
	private static final DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	public Object id;
	
	@NotEmpty
	public EventStatus status;

	@NotEmpty
	public LocalDate date;
	
	@NotEmpty
	@Size(Size.TIME_HH_MM)
	@Width(Width.SMALLER)
	public LocalTime from;

	@Size(Size.TIME_HH_MM)
	@Width(Width.SMALLER)
	public LocalTime until;

	public static boolean isDuringTheDay(LocalTime time) {
		return time != null && time.isBefore(LocalTime.of(18, 0));
	}

	public String getFromUntil() {
		if (Keys.isKeyObject(this))
			return Keys.methodOf(this, "fromUntil");

		if (until != null) {
			return Rendering.render(from) + " - " + Rendering.render(until);
		} else {
			return Rendering.render(from).toString();
		}
	}

	public String getDayOfWeek() {
		if (Keys.isKeyObject(this))
			return Keys.methodOf(this, "dayOfWeek", $.date);

		if (date != null) {
			DayOfWeek dayOfWeek = date.getDayOfWeek();
			return dayOfWeek.getDisplayName(TextStyle.FULL, LocaleContext.getCurrent());
		} else {
			return null;
		}
	}

	public String getDateFormatted() {
		return shortFormat.format(date);
	}

	@Size(100)
	@Searched
	@Width(300)
	public String line;

	@Size(4000)
	@Searched
	public String description;
	
	@Size(5)
	@Decimal(2)
	public BigDecimal price, priceReduced, priceWithWorkshop;

	@Size(1023)
	public String url;

	public Location location;
	
	public DeeJay deeJay, deeJay2;

	public final Set<EventTag> tags = new TreeSet<>();
	
	public boolean isCancelled() {
		return status == EventStatus.cancelled;
	}
	
	public boolean isLongLocationName() {
		return location.name.length() >= 19;
	}

	public String getJson() {
		JsonWriter writer = new JsonWriter();
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@context", "http://schema.org");
		values.put("@type", "DanceEvent");
		if (location != null) {
			values.put("organizer", createJsonOrganization(location));
			values.put("location", createJsonPlace(location));
		}
		values.put("name", location.name);
		if (price != null) {
			values.put("offers", createJsonOffer(price));
		}
		values.put("startDate", date.toString());
		values.put("endDate", date.toString());
		if (description != null) {
			values.put("description", description);
		}
		values.put("doorTime", from.toString());
		if (until != null) {
			Duration duration = Duration.between(from, until);
			if (duration.isNegative()) {
				duration = duration.plusDays(1);
			}
			values.put("duration", duration.toString());
		}
		if (deeJay != null) {
			values.put("performer", createJsonPerson(deeJay));
		}
		values.put("image", "https://www.flotte-sohle.ch/sohle_rot.png");
		values.put("url", "https://www.flotte-sohle.ch/event/" + id);
		// Das wurde von google bemängelt. Wir tanzen lieber offline :)
		values.put("eventAttendanceMode", "OfflineEventAttendanceMode"); 
		if (price != null) {
			values.put("offers", createOffers(this)); 
		}

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
	
	private static Map<String, Object> createOffers(DanceEvent event) {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("@type", "Offer");
		values.put("price", event.price.toPlainString());
		values.put("priceCurrency", "CHF");
		return values;
	}

	public boolean isImportable(EventUpdateCounter result) {
		if (status == EventStatus.edited) {
			result.skippedEditedEvents++;
			return false;
		} else if (status == EventStatus.blocked) {
			result.skippedBlockedEvents++;
			return false;
		}
		return true;
	}

}
