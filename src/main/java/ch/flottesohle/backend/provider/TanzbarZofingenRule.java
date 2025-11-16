package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class TanzbarZofingenRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();
		updateEvents(DayOfWeek.TUESDAY, result);
		updateEvents(DayOfWeek.WEDNESDAY, result);
		return result;
	}

	private void updateEvents(DayOfWeek day, EventUpdateCounter result) {
		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != day) {
			start = start.plusDays(1);
		}

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);
			if (date.compareTo(LocalDate.of(2025, 12, 15)) >= 0) {
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

			
			danceEvent.line = day == DayOfWeek.TUESDAY ? "Paartanz ohne Schlager" : "Paartanz mit Schlager";
			danceEvent.from = LocalTime.of(20, 0);
			danceEvent.until = LocalTime.of(0, 30);
			danceEvent.description = "Im Preis ist ein Konsumationsbon von 6 enthalten. \"DJ Cube\" und \"Der Bischof\" wechseln sich als DJ's ab";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(10);

			save(danceEvent, result);
		}
	}
	
	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Untere Brühlstrasse 9";
		location.city = "4800 Zofingen";
		location.name = "Tanzbar Zofingen";
		location.url = "https://tan-z.ch/";
		location.region.add(Region.AG);
		return location;
	}

}