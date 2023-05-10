package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class DanceVisionImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "Saturday Dance";
		event.from = LocalTime.of(20, 30);
		event.until = LocalTime.of(0, 30);
		event.price = BigDecimal.valueOf(12);
		event.status = EventStatus.generated;
		
		super.saveImportedEvent(event);
	}
	
	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bernstrasse 85";
		location.city = "3613 Steffisburg";
		location.name = "Dance Vision";
		location.url = "https://www.dance-vision.ch/";
		location.region.add(Region.BE);
		return location;
	}

}
