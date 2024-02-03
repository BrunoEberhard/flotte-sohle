package ch.flottesohle.backend.provider;

import java.time.LocalTime;

import ch.flottesohle.backend.LocationProvider;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class SilbandoImport extends LocationProvider {

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "Tanzabend";
		event.from = LocalTime.of(20, 30);
		super.saveImportedEvent(event);
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.region.add(Region.ZH);
		location.school = false;
		location.address = "Förrlibuckstrasse 62";
		location.city = "8005 Zürich";
		location.name = "Silbando";
		location.url = "https://www.tanzabend.ch//";
		return location;
	}

}
