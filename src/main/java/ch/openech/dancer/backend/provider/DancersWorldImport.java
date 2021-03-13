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
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class DancersWorldImport extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/dancers_world.csv"));
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;
				event.line = "Dancer’s Night";

				event.status = EventStatus.generated;
				event.description = "Ein stilvolles, klimatisiertes Ambiente lädt dich zum Verweilen ein. Dank gutem Mix von modernen Songs und klassischer Tanzmusik ist für alle Teilnehmer/innen das Passende dabei. "
						+ "Getanzt und geübt werden kann zu Standard- und Lateinmusik, Discofox, Tango Argentino und Salsa. Verbringe einen schönen Abend mit anderen Tanzbegeisterten.";
				event.price = BigDecimal.valueOf(10);
				event.from = LocalTime.of(20, 0);
				event.until = LocalTime.of(23, 30);

				save(event, result);
			}
		}
		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bernstrasse 388";
		location.city = "8953 Dietikon";
		location.region.add(Region.ZH);
		location.name = "Dancer's World";
		location.url = "https://dancers-world.ch/";
		return location;
	}

}
