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
			danceEvent.title = location.name;
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(15);
			danceEvent.priceReduced = BigDecimal.valueOf(10);
			danceEvent.from = LocalTime.of(19, 0);
			danceEvent.until = LocalTime.of(23, 0);
			danceEvent.description = "Ein Abend des gesellschaftlichen Paartanzes in stilvoller Atmosphäre. Dresscode eingehalten? Gratis Softdrink! "
					+ "Ein bunter Musikmix aus Standard, Latein und Modetänzen – abwechslungsreich und mit unter 15 % Discofox. "
					+ "Wenige Parkplätze (blaue Zone) vorhanden. Mit dem ÖV bis Ottiker- oder Röslistr. (Tram 7, 15)";

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Turnerstrasse 47";
		location.city = "8006 Zürich";
		location.region.add(Region.ZH);
		location.name = "Tanzclub Academia";
		location.url = "https://www.tc-academia.ch/";
		return location;
	}

}
