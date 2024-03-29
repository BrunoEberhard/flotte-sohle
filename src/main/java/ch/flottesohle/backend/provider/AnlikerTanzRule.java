package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class AnlikerTanzRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final List<Month> WINTER = Arrays.asList(Month.NOVEMBER, Month.DECEMBER, Month.JANUARY, Month.FEBRUARY, Month.MARCH);

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		LocalDate firstSaturday = start.with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
		if (firstSaturday.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 3; i++) {
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
			if (date.getYear() == 2019 && date.getMonth() == Month.APRIL)
				continue;

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
			danceEvent.line = "Tanz Café";

			if (WINTER.contains(date.getMonth())) {
				danceEvent.from = LocalTime.of(21, 0);
				danceEvent.until = LocalTime.of(0, 0);
			} else {
				danceEvent.from = LocalTime.of(21, 00);
				danceEvent.until = LocalTime.of(0, 30);
			}

			danceEvent.description = "Die Anliker Dance Night: Tanzen so viel & was man will! Mit Standard/Latein, Salsa, Disco-Fox, West Coast Swing uvm.!";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(14);
			danceEvent.priceReduced = BigDecimal.valueOf(12);

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Rorschacherstrasse 268";
		location.city = "9016 St. Gallen";
		location.name = "Tanzschule Anliker";
		location.url = "https://www.anliker-tanz.ch";
		location.region.add(Region.SG);
		return location;
	}

}