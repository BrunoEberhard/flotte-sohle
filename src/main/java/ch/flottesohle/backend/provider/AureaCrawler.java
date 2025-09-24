package ch.flottesohle.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class AureaCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://eventfrog.ch/de/events.html?showSearch=false&orgId=2080426";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		for (Element eventRow : doc.select(".event-list .event-list__events__tile")) {
			Element titleElement = eventRow.selectFirst(".event-list__events__tile__content__infos__title");
			if (titleElement == null || !titleElement.text().contains("Paartanz")) {
				continue;
			}
			
			Element dayElement = eventRow.selectFirst(".event-list__events__tile__content__date__day");
			Element monthElement = eventRow.selectFirst(".event-list__events__tile__content__date__month");

			LocalDate date = SilkkCrawler.parseDate(dayElement.text(), monthElement.text(), ("" + LocalDate.now().getYear()).substring(2)); 
			if (date.isBefore(LocalDate.now())) {
				date = date.plusYears(1);
			}
			
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.status == EventStatus.edited) {
				result.skippedEditedEvents++;
				continue;
			} else if (danceEvent.status == EventStatus.blocked) {
				result.skippedBlockedEvents++;
				continue;
			}
			
			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;
			danceEvent.from = LocalTime.of(20, 30);
			danceEvent.until = LocalTime.of(1, 0);
			danceEvent.price = BigDecimal.valueOf(15);
			danceEvent.location = location;
			save(danceEvent, result);
		}
		return result;
	}
	

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Baslerstrasse 15";
		location.city = "4310 Rheinfelden";
		location.name = "Aurea";
		location.url = "https://aurea-events.ch/";
		location.region.add(Region.AG);
		return location;
	}

}