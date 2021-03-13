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

public class HomeOfDanceRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		LocalDate lastSaturday = start.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));
		if (lastSaturday.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 4; i++) {
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));

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
			
			danceEvent.price = BigDecimal.valueOf(5);
			danceEvent.from = LocalTime.of(21, 0);
			danceEvent.until = LocalTime.of(1, 0);
			danceEvent.description = "Practice Party mit viel Disco Swing";
			danceEvent.location = location;
			danceEvent.deeJay = getDeeJay("DJ Bär");

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Kalchbühlstrasse 12";
		location.city = "7000 Chur";
		location.name = "Home of Dance";
		location.url = "https://homeofdance.ch/";
		location.region.add(Region.GR);
		location.school = true;
		return location;
	}

}