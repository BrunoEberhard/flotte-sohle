package ch.openech.dancer.backend.provider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class DukesRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		updateEvents(DayOfWeek.THURSDAY, result);
		updateEvents(DayOfWeek.SUNDAY, result);

		return result;
	}

	private void updateEvents(DayOfWeek dayOfWeek, EventUpdateCounter result) {
		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != dayOfWeek) {
			start = start.plusDays(1);
		}

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

			
			danceEvent.location = location;

//			if (dayOfWeek == DayOfWeek.THURSDAY) {
//				danceEvent.title = "Schlager Party";
//				danceEvent.line = "Schlager & Charts";
//				danceEvent.from = LocalTime.of(20, 30);
//				danceEvent.until = LocalTime.of(1, 0);
//				danceEvent.description = "50/50 Schlager & Charts. Mit dem Dukestänzer. Für alle Ladies: 1 Getränk gratis bis 21.45 (ausser Spirituosen).";
//				danceEvent.tags.add(EventTag.Taxidancer);
//			} else 
				if (dayOfWeek == DayOfWeek.SUNDAY) {
				danceEvent.line = "Party Tanznacht";
				danceEvent.from = LocalTime.of(20, 00);
				danceEvent.until = LocalTime.of(0, 30);
				danceEvent.description = "Für die Damen ein Freigetränk von 20.00 bis 21.00 (ausser Spirituosen).";
				danceEvent.tags.add(EventTag.Taxidancer);
			} else {
				continue;
			}

			save(danceEvent, result);
		}
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Sihlbrugg 3";
		location.city = "8816 Hirzel";
		location.name = "Dukes";
		location.url = "http://www.dukes.ch";
		location.region.add(Region.ZH);
		return location;
	}

}