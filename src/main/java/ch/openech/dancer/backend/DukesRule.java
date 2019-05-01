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

public class DukesRule extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.THURSDAY) {
			start = start.plusDays(1);
		}

		int generated = 0;
		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.status == EventStatus.edited) {
				continue;
			}

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;

			danceEvent.header = location.name;
			danceEvent.title = "Schlager Party";
			danceEvent.line = "Schlager & Charts";
			danceEvent.from = LocalTime.of(20, 30);
			danceEvent.until = LocalTime.of(1, 0);
			danceEvent.description = "50/50 Schlager & Charts. Mit dem Dukestänzer. Für alle Ladies: 1 Getränk gratis bis 21.45 (ausser Spirituosen).";
			danceEvent.location = location;
			danceEvent.tags.add(EventTag.Taxidancer);

			Backend.save(danceEvent);
			generated++;
		}

		return generated;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Sihlbrugg 3";
		location.city = "8816 Hirzel";
		location.name = "Dukes";
		location.url = "http://www.dukes.ch";
		location.region.add(Region.ZH);
		return location;
	}

}