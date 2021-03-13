package ch.openech.dancer.backend.provider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;
import org.minimalj.util.CsvReader;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;

// angefragt am 14.7.19 ob sie überhaupt aufgenommen werden wollen
public class TanzPunktRynachImport extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/tanz_punkt_rynach.csv"));
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;
				event.header = location.name;
				event.line = "Tanzabend";
				event.from = LocalTime.of(20, 00);
				event.until = LocalTime.of(2, 00);
				event.price = BigDecimal.valueOf(20);
				event.priceReduced = BigDecimal.valueOf(15);
				event.status = EventStatus.generated;
				event.tags.add(EventTag.Workshop);

				event.description = "Der Verein tanzpunktrynach organisiert vier Mal im Jahr einen Tanzabend in Reinach. "
						+ "Vor dem Anlass werden in einem Workshop die grundlegenden Tanzschritte für Neueinsteiger geübt, für Fortgeschrittene bietet sich die Gelegenheit das früher erlerntes wiederaufzufrischen und Neues zu lernen, ein erfahrener Tanzlehrer hilft dir dabei. "
						+ "Nach einer Stunde geht es los: von 20:00 Uhr bis 02:00 Uhr wird getanzt und zwar zu Discofox-Gesellschaftstanz, Standard- und Latin!";

				save(event, result);
			}
		}
		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		// location.region.add(Region.SG);
		location.school = true;
		location.address = "";
		location.city = "";
		location.name = "Tanz Punkt Rynach";
		location.url = "https://www.tanzpunktrynach.ch/";
		return location;
	}

}
