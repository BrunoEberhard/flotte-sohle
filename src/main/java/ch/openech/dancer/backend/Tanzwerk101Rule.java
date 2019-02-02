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
import ch.openech.dancer.model.Organizer;

public class Tanzwerk101Rule extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.TUESDAY) {
			start = start.plusDays(1);
		}

		int generated = 0;
		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			if (!danceEventOptional.isPresent()) {
				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;

				danceEvent.title = "Tanzabend";
				danceEvent.from = LocalTime.of(20, 30);
				danceEvent.until = LocalTime.of(23, 0);
				danceEvent.description = "Auch TänzerInnen ohne TanzpartnerIn sind herzlich eingeladen";
				danceEvent.location = location;

				Backend.save(danceEvent);
				generated++;
			}
		}

		return generated;
	}

	@Override
	public Organizer createOrganizer() {
		return null;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Viaduktstrasse 67";
		location.city = "8005 Zürich";
		location.name = "Tanzwerk 101";
		location.url = "http://www.elsocial.ch";
		return location;
	}

}