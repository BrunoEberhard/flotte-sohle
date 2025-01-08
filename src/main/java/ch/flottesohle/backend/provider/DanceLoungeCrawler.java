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
				Element timeElement = row.selectFirst(".tribe-events-calendar-list__event-datetime");
				LocalDate date = LocalDate.parse(timeElement.attr("datetime"));
				
//				Element costElement = row.selectFirst(".tribe-events-calendar-list__event-cost");
				Element titleElement = row.selectFirst(".tribe-events-calendar-list__event-title");
				var title = titleElement.text();
				var titleLowerCase = title.toLowerCase();
				if (!titleLowerCase.contains("swing") && !titleLowerCase.contains("salsa")) {
					
					Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

					DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
					danceEvent.from = LocalTime.of(20, 0);
					danceEvent.until = LocalTime.of(0, 30);
					danceEvent.status = EventStatus.generated;
					danceEvent.date = date;
					danceEvent.location = location;
					danceEvent.price = BigDecimal.valueOf(18);
					danceEvent.description = titleElement.text();
					
					save(danceEvent, result);
				}
			} catch (Exception x) {
				result.failedEvents++;
			}
		});
		return result;
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