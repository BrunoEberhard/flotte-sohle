package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class TanzclubAcademiaRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		for (int i = 0; i <= 3; i++) {
			LocalDate date = LocalDate.now().plusMonths(i);
			date = date.with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.SATURDAY));
			if (date.isBefore(LocalDate.now())) {
				continue;
			}
			if (date.getMonth() == Month.AUGUST) {
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
			
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(17);
			danceEvent.priceReduced = BigDecimal.valueOf(12);
			danceEvent.from = LocalTime.of(19, 30);
			danceEvent.until = LocalTime.of(23, 0);
			danceEvent.description = "Ein Abend des gesellschaftlichen Paartanzes in stilvoller Atmosphäre mit einem bunten Musikmix aus Standard, Latein und "
					+ "Modetänzen – abwechslungsreich und mit unter 15 % Discofox.";

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Ackersteinstrasse 188";
		location.city = "8049 Zürich";
		location.region.add(Region.ZH);
		location.name = "Tanzclub Academia";
		location.url = "https://www.tc-academia.ch/";
		return location;
	}

}
