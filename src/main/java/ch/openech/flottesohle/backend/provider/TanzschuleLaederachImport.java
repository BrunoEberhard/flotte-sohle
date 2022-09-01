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

public class TanzschuleLaederachImport extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/flottesohle/data/tanzschule_laederach.csv"));
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && !event.date.isBefore(LocalDate.now())) {
				event.location = this.location;

				event.status = EventStatus.generated;
				event.from = LocalTime.of(20, 30);
				event.until = LocalTime.of(0, 0);
				event.price = BigDecimal.valueOf(17);
				event.description = "Einmal im Monat heisst „Parkett frei“ für alle Tanzbegeisterten. An unserer Party ist jeder herzlich willkommen, aber die Teilnehmerzahl ist limitiert. Bitte reservieren Sie die Plätze für Sie und Ihre Begleitperson(en) vorab.";
				save(event, result);
			}
		}
		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Schaffhauserstrasse 330";
		location.city = "8050 Zürich";
		location.region.add(Region.ZH);
		location.name = "Tanzschule Läderach";
		location.url = "https://letsdance.ch/";
		return location;
	}

}
