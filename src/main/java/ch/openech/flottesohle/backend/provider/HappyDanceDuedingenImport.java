package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;
import org.minimalj.util.CsvReader;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class HappyDanceDuedingenImport extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/flottesohle/data/happy_dance_duedingen.csv"));
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && !event.date.isBefore(LocalDate.now())) {
				event.location = this.location;

				event.status = EventStatus.generated;
				event.from = LocalTime.of(19, 30);
				event.until = LocalTime.of(23, 0);
				event.price = BigDecimal.valueOf(30);
				event.priceReduced = BigDecimal.valueOf(25);
				event.description = "Der gemütlich gesellige Tanzabend mit Musik von Walzer bis Cha-Cha-Cha.\n"
						+ "Ein kleiner Imbiss ist dabei, denn es gehört einfach dazu, etwas zusammen zu sitzen, "
						+ "Neuigkeiten auszutauschen und zu lachen. Die Tanz-Party ist vor allem für Leute interessant, "
						+ "welche mindestens ein wenig Kenntnisse in den 10 Standard- und Lateintänzen haben.\nBitte Auf Webseite anmelden";

				save(event, result);
			}
		}
		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bonnstrasse 22A";
		location.city = "3186 Düdingen";
		location.region.add(Region.BE);
		location.name = "Happy Dance";
		location.url = "http://www.happydance.ch";
		return location;
	}

}
