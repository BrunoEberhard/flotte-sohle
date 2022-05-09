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

public class FelsenbarChurImport extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/flottesohle/data/felsenbar_chur.csv"));
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;

				event.line = "DISCO FOXX";
				event.from = LocalTime.of(21, 0); 
				event.price = BigDecimal.valueOf(15);
				event.description = "Das Musikgewitter im Klub! \"Home of Dance\" und \"Disco Swing\" bringen euch einen schaurig erfrischenden Platzregen aus Glückshormonen. Mit einer glitzrigen Selektion aus Disco Pop und groovy Dancemusic kann der Paartanz gefeiert werden. Eintritt inkl. 1 Getränkebon";
				event.status = EventStatus.generated;

				save(event, result);
			}
		}
		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Welschdörfli 1";
		location.city = "7000 Chur";
		location.region.add(Region.GR);
		location.name = "Felsenbar Chur";
		location.url = "https://felsenbar-chur.ch/";
		return location;
	}

}
