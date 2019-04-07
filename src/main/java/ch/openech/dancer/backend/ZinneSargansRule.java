package ch.openech.dancer.backend;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class ZinneSargansRule extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		LocalDate start = LocalDate.now();
		LocalDate end = start.plusWeeks(12);

		int generated = 0;
		LocalDate date = start.minusDays(1);
		while (date.isBefore(end)) {
			date = date.plusDays(1);

			DayOfWeek day = date.getDayOfWeek();
			if (day != DayOfWeek.WEDNESDAY && day != DayOfWeek.FRIDAY && day != DayOfWeek.SATURDAY) {
				continue;
			}

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.status == EventStatus.edited) {
				continue;
			}

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;

			danceEvent.header = location.name;
			danceEvent.title = "Dancing zur Zinne";
			danceEvent.description = "Livemusik zum Tanzen";
			danceEvent.from = LocalTime.of(20, 30);
			danceEvent.until = LocalTime.of(0, 0);
			danceEvent.location = location;
			danceEvent.tags.add(EventTag.LiveBand);

			Backend.save(danceEvent);
			generated++;
		}

		return generated;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bahnhofstrasse 12";
		location.city = "7320 Sargans";
		location.name = "Zinne Sargans";
		location.url = "http://www.zinnesargans.ch/";
		location.region.add(Region.SG);
		return location;
	}

}