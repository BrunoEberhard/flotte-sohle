package ch.openech.flottesohle.backend.provider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class ZinneSargansRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		LocalDate end = start.plusWeeks(12);

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
				result.skippedEditedEvents++;
				continue;
			} else if (danceEvent.status == EventStatus.blocked) {
				result.skippedBlockedEvents++;
				continue;
			}

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;

			
			danceEvent.line = "Dancing zur Zinne";
			danceEvent.description = "Livemusik zum Tanzen";
			danceEvent.from = LocalTime.of(20, 00);
			danceEvent.until = LocalTime.of(0, 0);
			danceEvent.location = location;
			if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY) {
				danceEvent.tags.add(EventTag.LiveBand);
			} else {
				danceEvent.tags.remove(EventTag.LiveBand);
			}

			save(danceEvent, result);
		}

		return result;
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