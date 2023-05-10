package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.flottesohle.backend.LocationProvider;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.EventTag;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class BallroomDancingImport extends LocationProvider {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.from = LocalTime.of(20, 00);
		event.until = LocalTime.of(0, 30);
		event.price = BigDecimal.valueOf(15);
		event.status = EventStatus.generated;
		event.tags.add(EventTag.Taxidancer);

		super.saveImportedEvent(event);
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.region.add(Region.LU);
		location.school = false;
		location.address = "Zentralstrasse 7";
		location.city = "5623 Boswil";
		location.name = "Gasthof Löwen";
		location.url = "https://www.ballroomdancingfreiamt.ch/";
		return location;
	}
}
