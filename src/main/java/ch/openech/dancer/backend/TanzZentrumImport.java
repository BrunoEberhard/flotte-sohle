package ch.openech.dancer.backend;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.util.CsvReader;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class TanzZentrumImport extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/tanz_zentrum.csv"));
		int count = 0;
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;
				event.title = "Tanz(übungs-)abend";
				event.from = LocalTime.of(20, 30);
				event.until = LocalTime.of(23, 30);
				event.price = BigDecimal.valueOf(15);
				event.priceReduced = BigDecimal.valueOf(0);
				event.status = EventStatus.published;
				event.tags.add(EventTag.Workshop);

				Backend.insert(event);
				count++;
			}
		}
		return count;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.region.add(Region.SG);
		location.school = true;
		location.address = "Haggenstrasse 44";
		location.city = "9014 St. Gallen";
		location.name = "Tanz Zentrum";
		location.url = "http://www.tanz-zentrum.ch";
		return location;
	}

}
