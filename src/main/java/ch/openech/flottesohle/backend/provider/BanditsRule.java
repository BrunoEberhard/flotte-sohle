package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
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

public class BanditsRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
			start = start.plusDays(1);
		}

		// DeeJay deeJayJanosch = Backend.find(DeeJay.class, By.field(DeeJay.$.name, "DJ
		// Janosch")).get(0);

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

			
			danceEvent.line = "Dancing Night";
			danceEvent.from = LocalTime.of(20, 0);
			danceEvent.until = LocalTime.of(1, 0);
			danceEvent.description = "DanceCharts & Dance Classics 80er & 90er Style & Latino (Salsas & Bachatas) & Oldies & Schlager u.s.w.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(5);
			danceEvent.tags.add(EventTag.Workshop);
			// danceEvent.deeJay = deeJayJanosch;
			danceEvent.deeJay = null;

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Mühlenenstrasse";
		location.city = "8856 Tuggen";
		location.name = "The Bandits";
		location.url = "http://www.thebandits.ch/";
		location.region.add(Region.ZH);
		return location;
	}

}