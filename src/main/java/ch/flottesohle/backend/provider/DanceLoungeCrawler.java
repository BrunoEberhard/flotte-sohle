package ch.flottesohle.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;
import org.minimalj.util.DateUtils;
import org.minimalj.util.StringUtils;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class DanceLoungeCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.dancelounge.ch/events/liste/";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		Element tribeEventsContainer = doc.selectFirst(".tribe-events-l-container");

		Elements rows = tribeEventsContainer.select(".tribe-events-calendar-list__event-row");
		
		rows.forEach(row -> {
			try {
				var dateTimeElement = row.selectFirst(".tribe-events-calendar-list__event-datetime");
				var dateTime = dateTimeElement.text();
				LocalDate date = LocalDate.parse(dateTimeElement.attr("datetime"));
				
				var titleElement = row.selectFirst(".tribe-events-calendar-list__event-title");
				var title = titleElement.text();
				var titleLowerCase = title.toLowerCase();

				var venueElement = row.selectFirst(".tribe-events-calendar-list__event-venue");
				var venue = venueElement != null ? venueElement.text() : null;
				var venueLowerCase = venue != null ? venue.toLowerCase() : "";
				
				if (venueLowerCase.contains(getVenue()) && !titleLowerCase.contains("swing") && !titleLowerCase.contains("salsa") && !titleLowerCase.contains("privat") ) {
					
					Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

					DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
				
					var priceElement = row.selectFirst(".tribe-events-c-small-cta__price");
					var price = priceElement != null ? priceElement.text() : "";
					
					
					danceEvent.from = extractFrom(dateTime);
					if (danceEvent.from == null) {
						danceEvent.from = LocalTime.of(20, 0);
					}
					danceEvent.until = extractUntil(dateTime);
					danceEvent.status = EventStatus.generated;
					danceEvent.date = date;
					danceEvent.location = location;
					danceEvent.price = extractPrice(price);
					danceEvent.description = getDescription();
					if (danceEvent.description == null) {
						danceEvent.description = titleElement.text();
					}
					
					save(danceEvent, result);
				}
			} catch (Exception x) {
				result.failedEvents++;
			}
		});
		return result;
	}
	
	static LocalTime extractFrom(String dateTime) {
		if (!StringUtils.isEmpty(dateTime)) {
			int pos = dateTime.indexOf("@");
			return extractTime(dateTime, pos);
		}
		return null;
	}

	static LocalTime extractUntil(String dateTime) {
		if (!StringUtils.isEmpty(dateTime)) {
			int pos = dateTime.indexOf("-");
			return extractTime(dateTime, pos);
		}
		return null;
	}
	
	static LocalTime extractTime(String dateTime, int pos) {
		if (pos < 0) {
			return null;
		}
		while (!Character.isDigit(dateTime.charAt(pos)) && pos < dateTime.length()) {
			pos++;
		}
		if (pos < dateTime.length() - 4) {
			int endPos = pos + 1;
			while (endPos < dateTime.length() && (Character.isDigit(dateTime.charAt(endPos)) || dateTime.charAt(endPos) == ':')) {
				endPos++;
			}
			return DateUtils.parseTime(dateTime.substring(pos, endPos), false);
		} else {
			return null;
		}
	}
	
	static BigDecimal extractPrice(String price) {
		if (!StringUtils.isEmpty(price) && !"Kostenlos".equals(price)) {
			int pos = 0;
			while (pos < price.length() && !Character.isDigit(price.charAt(pos))) {
				pos++;
			}
			int endPos = pos;
			while (endPos < price.length() && (Character.isDigit(price.charAt(endPos)) || price.charAt(endPos) == '.')) {
				endPos++;
			}
			return new BigDecimal(price.substring(pos, endPos));
		}
		return null;
	}
	
	protected String getDescription() {
		return null;
	}
	
	protected String getVenue() {
		return "dancelounge";
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Spinnereistrasse 29";
		location.city = "8640 Rapperswil-Jona";
		location.name = "Dance Lounge";
		location.url = "https://www.dancelounge.ch/";
		location.region.add(Region.SG);
		location.region.add(Region.ZH);
		return location;
	}
	
	public static void main(String[] args) throws IOException {
		new DanceLoungeCrawler().updateEvents();
	}

}