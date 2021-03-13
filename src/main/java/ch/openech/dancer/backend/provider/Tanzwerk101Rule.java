package ch.openech.dancer.backend.provider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class Tanzwerk101Rule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.TUESDAY) {
			start = start.plusDays(1);
		}

//		DeeJay deeJayJanosch = Backend.find(DeeJay.class, By.field(DeeJay.$.name, "DJ Janosch")).get(0);

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

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

			danceEvent.header = location.name;
			danceEvent.line = "Tanzabend";
			danceEvent.from = LocalTime.of(20, 30);
			danceEvent.until = LocalTime.of(23, 0);
			danceEvent.description = "Auch TänzerInnen ohne TanzpartnerIn sind herzlich eingeladen";
			danceEvent.location = location;
			// danceEvent.deeJay = deeJayJanosch;
			danceEvent.deeJay = null;
			danceEvent.tags.add(EventTag.Taxidancer);

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Pfingstweidstrasse 101";
		location.city = "8005 Zürich";
		location.name = "Tanzwerk 101";
		location.url = "http://www.tanzwerk101.ch";
		location.region.add(Region.ZH);
		return location;
	}

}