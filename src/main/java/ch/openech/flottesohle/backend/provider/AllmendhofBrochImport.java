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
import ch.openech.flottesohle.model.DeeJay;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class AllmendhofBrochImport extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		DeeJay dj = getDeeJay("Erwin Live", "https://www.erwinlive.ch");

		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/flottesohle/data/allmendhof_broch.csv"));
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;

				event.from = LocalTime.of(19, 30);
				event.until = LocalTime.of(0, 0);
				event.description = "Unsere fröhlichen Tanzabende. Essen ab 18.30h, Tanzen ab 19.30h, Live Musik mit Erwin";
				event.price = BigDecimal.valueOf(0);
				event.deeJay = dj;
				event.status = EventStatus.generated;
				event.tags.add(EventTag.LiveBand);

				save(event, result);
			}
		}
		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.region.add(Region.AG);
		location.school = false;
		location.address = "Allmend 79";
		location.city = "5637 Beinwil/Freiamt";
		location.name = "Allmendhof Bloch";
		location.url = "https://www.allmendhof-broch.ch";
		return location;
	}

}
