package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class ChezGeorgesRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.FRIDAY) {
			start = start.plusDays(1);
		}

		for (int i = 0; i <= 12; i++) {
			LocalDate date = start.plusWeeks(i);
			updateEvent(result, date);
			
			date = date.plusDays(1);
			updateEvent(result, date);
		}

		return result;
	}

	public void updateEvent(EventUpdateCounter result, LocalDate date) {
		Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

		DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
		if (danceEvent.status == EventStatus.edited) {
			result.skippedEditedEvents++;
			return;
		} else if (danceEvent.status == EventStatus.blocked) {
			result.skippedBlockedEvents++;
			return;
		}
		
		danceEvent.status = EventStatus.generated;
		danceEvent.date = date;
		danceEvent.location = location;
		danceEvent.price = BigDecimal.valueOf(8);
		danceEvent.from = LocalTime.of(20, 0);
		danceEvent.until = LocalTime.of(2, 0);
		if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
			danceEvent.line = "Oldies but Goldies";
			danceEvent.description = "Solo Tanz, Paartanz, Discofox, Jive, Rock N Roll";
			danceEvent.until = LocalTime.of(1, 0);
		} else {
			danceEvent.line = "Saturday Night Fever";
			danceEvent.description = "Immer Samstag mit DJ Klaus 45";
		}

		save(danceEvent, result);
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Baselstrasse 58";
		location.city = "4203 Grellingen";
		location.region.add(Region.BS);
		location.name = "Chez Georges";
		location.url = "https://www.chezgeorgesdancing.ch/";

		Location.Closing closing = new Location.Closing();
		closing.from = LocalDate.of(2022, 7, 22);
		closing.until = LocalDate.of(2022, 8, 22);
		closing.reason = "Sommerferien";
		location.closings.add(closing);
		return location;
	}

}
