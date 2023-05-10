package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class TanzZentrumImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "Tanz(übungs-)abend";
		event.from = LocalTime.of(20, 30);
		event.until = LocalTime.of(23, 30);
		event.price = BigDecimal.valueOf(15);
		event.priceReduced = BigDecimal.valueOf(0);
		event.tags.add(EventTag.Workshop);

		super.saveImportedEvent(event);
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
