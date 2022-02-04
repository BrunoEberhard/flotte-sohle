package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

@Deprecated // Momentan gilt der Import
public class TanzcenterRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		for (int i = 0; i <= 3; i++) {
			LocalDate date = LocalDate.now().plusMonths(i);
			date = date.with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.FRIDAY));
			if (date.equals(LocalDate.of(2020, 04, 10))) {
				date = LocalDate.of(2020, 04, 17); // ostern
			}
			if (date.isBefore(LocalDate.now())) {
				continue;
			}
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

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
			danceEvent.price = BigDecimal.valueOf(10);
			danceEvent.from = LocalTime.of(20, 30);
			danceEvent.description = "Jeden 2. Freitag im Monat";

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Werkstrasse 2d";
		location.city = "8630 Rüti ZH";
		location.region.add(Region.ZH);
		location.region.add(Region.SG);
		location.name = "Tanzcenter";
		location.url = "http://www.tanzcenter.ch";
		return location;
	}

}
