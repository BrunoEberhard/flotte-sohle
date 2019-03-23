package ch.openech.dancer.backend;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class DancersRule extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		LocalDate start = LocalDate.now();
		while (!(start.getDayOfWeek() == DayOfWeek.SATURDAY)) {
			start = start.plusDays(1);
		}

		int generated = 0;
		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			if (!danceEventOptional.isPresent()) {
				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				danceEvent.status = EventStatus.published;
				danceEvent.date = date;

				danceEvent.title = "Saturday-Dancers";
				danceEvent.from = LocalTime.of(20, 0);
				danceEvent.until = LocalTime.of(2, 0);
				danceEvent.description = "Der Klassiker - jeden Samstag mit dem bewährten Tanz-Mix, der keine Wünsche offen lässt. Daten, sowie eine Tabelle, wann jeweils zusätzliche Tanzflächen zur Verfügung stehen.";
				danceEvent.location = location;

				Backend.save(danceEvent);
				generated++;
			}
		}

		return generated;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Gewerbehallenstr. 2";
		location.city = "8304 Wallisellen";
		location.region.add(Region.ZH);
		location.name = "Dancers";
		location.url = "https://dancers.ch";
		return location;
	}

}