package ch.flottesohle.backend.provider.inactive;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class ElSocialRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		LocalDate firstSunday = start.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
		if (firstSunday.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 3; i++) {
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));

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

			
			danceEvent.from = LocalTime.of(20, 0);
			danceEvent.until = LocalTime.of(23, 0);
			danceEvent.description = "Auch TänzerInnen ohne TanzpartnerIn sind herzlich eingeladen";
			danceEvent.location = location;

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Viaduktstrasse 67";
		location.city = "8005 Zürich";
		location.name = "el social";
		location.url = "http://www.elsocial.ch";
		location.region.add(Region.ZH);
		return location;
	}

}