package ch.openech.flottesohle.backend.provider.inactive;

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

public class BananenreifereiRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		LocalDate lastSaturday = start.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));
		if (lastSaturday.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 3; i++) {
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.lastInMonth(DayOfWeek.SUNDAY));

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
			danceEvent.line = "Dance with me";
			danceEvent.from = LocalTime.of(19, 0);

			danceEvent.description = "Jeden letzten Sonntag. Discofox, Walzer, Jive, Bachata, Rumba uvm.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(12);

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.name = "Bananenreiferei";
		location.address = "Pfingstweidstrasse 101";
		location.city = "8005 Zürich";
		location.url = "https://www.bananenreiferei.ch";
		location.region.add(Region.ZH);
		return location;
	}

}