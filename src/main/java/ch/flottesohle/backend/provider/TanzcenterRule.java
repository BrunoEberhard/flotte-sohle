package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
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

public class TanzcenterRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate date = LocalDate.of(2023, 2, 17);
		while (date.isBefore(LocalDate.now())) {
			date = date.plusDays(14);
		}
		
		for (int i = 0; i <= 7; i++) {
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
			
			danceEvent.location = location;
			danceEvent.line = "Dance Night Rüti";
			danceEvent.price = BigDecimal.valueOf(10);
			danceEvent.from = LocalTime.of(20, 30);
			danceEvent.until = LocalTime.of(23, 59);

			save(danceEvent, result);

			date = date.plusDays(14);
		}

		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Werkstrasse 2d";
		location.city = "8630 Rüti ZH";
		location.region.add(Region.ZH);
		location.region.add(Region.SG);
		location.name = "Tanzcenter";
		location.url = "http://www.tanzcenter.ch";
		return location;
	}

}
