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
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class BallroomDancingRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		for (int i = 0; i <= 3; i++) {
			LocalDate date = LocalDate.now().plusMonths(i);
			date = date.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.FRIDAY));
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
			danceEvent.header = location.name;
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(15);
			danceEvent.from = LocalTime.of(20, 0);
			danceEvent.tags.add(EventTag.LiveBand);
			danceEvent.description = "Jeden 3. Freitag im Monat. Ausweispflicht & Erfassung der Kontaktdaten ist obligatorisch. Bitte um Reservation per Telefon oder E-Mail / 056 666 11 17 / www.ballroomdancingfreiamt.ch. Platzzahl ist limitiert.";

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.region.add(Region.LU);
		location.school = false;
		location.address = "Zentralstrasse 7";
		location.city = "5623 Boswil";
		location.name = "Gasthof Löwen (Chillout)";
		location.url = "https://www.ballroomdancingfreiamt.ch/";
		return location;
	}
}
