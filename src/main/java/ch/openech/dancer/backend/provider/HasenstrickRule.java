package ch.openech.dancer.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class HasenstrickRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.THURSDAY) {
			start = start.plusDays(1);
		}

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);
			Month month = date.getMonth();
			if (month.getValue() < 4 || month.getValue() > 9) {
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

			
			danceEvent.line = "Tanz im Hasenstrick";
			danceEvent.from = LocalTime.of(19, 0);
			danceEvent.until = LocalTime.of(23, 59);
			danceEvent.price = BigDecimal.valueOf(10);
			
			danceEvent.description = "Wir sind bereit für einen tollen Sommer im Hasenstrick, bei gutem Wetter wie gewohnt Draussen mit atemberaubender Aussicht , bei schlechtem Wetter in der schönen Schür. Deejays Janosch , Bär & Sigi spielen beliebte Tanzmusik.";
			danceEvent.location = location;

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Höhenstrasse 15";
		location.city = "8635 Dürnten";
		location.name = "Hasenstrick";
		location.url = "http://landgasthof-hasenstrick.ch";
		location.region.add(Region.ZH);
		return location;
	}
}
