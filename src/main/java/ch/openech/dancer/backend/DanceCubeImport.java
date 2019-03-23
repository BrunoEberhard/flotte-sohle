package ch.openech.dancer.backend;

import java.time.LocalDate;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.util.CsvReader;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class DanceCubeImport extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/dance_cube_2019.csv"));
		int count = 0;
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;
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
		location.address = "Gewerbestrasse 4";
		location.city = "9445 Rebstein";
		location.region.add(Region.SG);
		location.name = "DanceCube";
		location.url = "http://www.dancecube.ch";
		return location;
	}

}
