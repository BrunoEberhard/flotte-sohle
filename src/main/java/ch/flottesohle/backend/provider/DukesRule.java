package ch.flottesohle.backend.provider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.EventTag;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class DukesRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		updateEvents(DayOfWeek.WEDNESDAY, result);
		updateEvents(DayOfWeek.SUNDAY, result);

		return result;
	}

	private void updateEvents(DayOfWeek dayOfWeek, EventUpdateCounter result) {
		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != dayOfWeek) {
			start = start.plusDays(1);
		}

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

			
			danceEvent.location = location;

			if (dayOfWeek == DayOfWeek.WEDNESDAY) {
				danceEvent.line = "Boogie-Woogie, Jive, RnR Party";
				danceEvent.from = LocalTime.of(20, 45);
				danceEvent.until = LocalTime.of(1, 0);
				danceEvent.description = "Tanz-Crash-Kurs ab 19:45";
				danceEvent.tags.add(EventTag.Workshop);
				danceEvent.tags.add(EventTag.Taxidancer);
			} else 
				if (dayOfWeek == DayOfWeek.SUNDAY) {
				danceEvent.line = "Sonntags Tanzparty";
				danceEvent.from = LocalTime.of(20, 00);
				danceEvent.until = LocalTime.of(0, 00);
				danceEvent.description = "Gratis Eintritt";
				danceEvent.tags.add(EventTag.Taxidancer);
			} else {
				continue;
			}

			save(danceEvent, result);
		}
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