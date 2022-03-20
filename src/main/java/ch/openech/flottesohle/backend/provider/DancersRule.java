package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class DancersRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (!(start.getDayOfWeek() == DayOfWeek.SATURDAY)) {
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

			if (LocalDate.of(2019, 04, 20).equals(date)) {
				// Ostern
				danceEvent.status = EventStatus.blocked;
			} else {
				danceEvent.status = EventStatus.generated;
			}
			danceEvent.date = date;

			
			danceEvent.line = "Saturday-Dancers";
			danceEvent.from = LocalTime.of(20, 0);
			danceEvent.until = LocalTime.of(2, 0);
			danceEvent.description = "Der Klassiker - jeden Samstag mit dem bewährten Tanz-Mix, der keine Wünsche offen lässt. Daten, sowie eine Tabelle, wann jeweils zusätzliche Tanzflächen zur Verfügung stehen.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(18);
			danceEvent.priceReduced = BigDecimal.valueOf(5);

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Gewerbehallenstr. 2";
		location.city = "8304 Wallisellen";
		location.region.add(Region.ZH);
		location.name = "Dancers";
		location.url = "https://dancers.ch";
		return location;
	}

}