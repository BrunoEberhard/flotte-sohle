package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.util.CsvReader;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Organizer;

public class DanceCubeImport extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		byte[] flyer = new byte[34925];
		try {
			getClass().getResource("/ch/openech/dancer/data/dance_cube_2019.jpg").openStream().read(flyer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/dance_cube_2019.csv"));
		int count = 0;
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));

			if (!danceEventOptional.isPresent() && event.date.isAfter(LocalDate.now())) {
				event.location = this.location;
				event.status = EventStatus.published;
				event.flyer = flyer;
				event.tags.add(EventTag.Workshop);

				Backend.insert(event);
				count++;
			}
		}
		return count;
	}

	@Override
	protected Organizer createOrganizer() {
		Organizer organizer = new Organizer();
		organizer.country = "Schweiz";
		organizer.address = "Gewerbestrasse 4";
		organizer.city = "9445 Rebstein";
		organizer.name = "DanceCube";
		organizer.url = "http://www.dancecube.ch";
		return organizer;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Gewerbestrasse 4";
		location.city = "9445 Rebstein";
		location.name = "DanceCube";
		location.url = "http://www.dancecube.ch";
		return location;
	}

}
