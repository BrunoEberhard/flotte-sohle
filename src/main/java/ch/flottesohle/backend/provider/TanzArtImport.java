package ch.flottesohle.backend.provider;

import java.time.LocalTime;

import ch.flottesohle.backend.LocationProvider;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class TanzArtImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "Tanzparty";
		event.from = LocalTime.of(20, 0);
		event.until = LocalTime.of(23, 0);
		super.saveImportedEvent(event);
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
