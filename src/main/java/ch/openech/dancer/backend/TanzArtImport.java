package ch.openech.dancer.backend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.util.CsvReader;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class TanzArtImport extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/tanz_art.csv"));
		int count = 0;
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;
				event.header = location.name;
				event.title = "Tanz Abend";
				event.from = LocalTime.of(20, 0);
				event.until = LocalTime.of(23, 0);
				event.status = EventStatus.generated;

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
		location.region.add(Region.LU);
		location.school = true;
		location.address = "Zentralstrasse 24";
		location.city = "6030 Ebikon";
		location.name = "Tanz Art";
		location.url = "https://www.tanz-art.ch/";
		return location;
	}

}
