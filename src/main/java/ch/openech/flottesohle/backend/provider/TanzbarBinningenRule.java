package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class TanzbarBinningenRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		for (int i = 0; i < 3; i++) {
			LocalDate date = LocalDate.now().plusMonths(i);
			date = date.with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
			date = date.plusWeeks(2);
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

			danceEvent.line = "Gesellschafts-Tanzparty";
			danceEvent.from = LocalTime.of(20, 0);
			danceEvent.price = BigDecimal.valueOf(15);
			danceEvent.priceReduced = BigDecimal.valueOf(9);
			danceEvent.description = "Jeden 3. Samstag im Monat hast du bei uns die Möglichkeit das Tanzbein zu schwingen und Gelerntes zu trainieren.";
			danceEvent.location = location;

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Hauptstrasse 129";
		location.city = "4106 Therwil";
		location.name = "Tanzbar Binningen";
		location.url = "http://www.tanzbarbinningen.ch/";
		location.region.add(Region.BS);
		return location;
	}

}