package ch.openech.dancer.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class SchuetzenhausRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		LocalDate firstTuesday = start.with(TemporalAdjusters.firstInMonth(DayOfWeek.TUESDAY));
		LocalDate thirdTuesDay = firstTuesday.plusWeeks(2);
		if (thirdTuesDay.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 3; i++) {
			// jeweils am 3. Dienstag des Monats
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.firstInMonth(DayOfWeek.TUESDAY));
			date = date.plusWeeks(2);

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

			danceEvent.header = "Schützenhaus";
			danceEvent.title = "Stubete";
			danceEvent.line = "Stubete";
			danceEvent.from = LocalTime.of(19, 0);
			danceEvent.until = LocalTime.of(23, 0);

			danceEvent.description = "Genießen Sie mit uns die Abende in geselliger Runde an der Stubete.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(0);

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Schaffhauserstrasse 201";
		location.city = "8400 Winterthur";
		location.name = "Schützenhaus";
		location.url = "https://www.tanzhalle-schuetzenhaus.ch/stubete";
		location.region.add(Region.ZH);
		return location;
	}

}
