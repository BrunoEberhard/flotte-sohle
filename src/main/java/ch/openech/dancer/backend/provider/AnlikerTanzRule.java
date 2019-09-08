package ch.openech.dancer.backend.provider;

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

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

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

			danceEvent.header = "Anliker";
			danceEvent.title = "Tanz Café";

			if (WINTER.contains(date.getMonth())) {
				danceEvent.from = LocalTime.of(20, 0);
				danceEvent.until = LocalTime.of(0, 0);
			} else {
				danceEvent.from = LocalTime.of(20, 30);
				danceEvent.until = LocalTime.of(0, 30);
			}

			danceEvent.description = "Die Anliker Dance Night. TANZcafé auf 2 Dance-Floors mit Standard/Latein, Salsa, Disco-Fox, West Coast Swing u.v.m.!";
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
		location.address = "Rorschacherstrasse 154";
		location.city = "9006 St. Gallen";
		location.name = "Tanzschule Anliker";
		location.url = "https://www.anliker-tanz.ch";
		location.region.add(Region.SG);
		return location;
	}

}