package ch.openech.flottesohle.backend.provider;

import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class TanzenMitHerzImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "Tanzabend";
		event.from = LocalTime.of(20, 0);
		event.until = LocalTime.of(23, 30);

		super.saveImportedEvent(event);
	}
	
	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Zürichstrasse 38";
		location.city = "8306 Brüttisellen";
		location.name = "Tanzen mit Herz";
		location.url = "https://www.tanzenmitherz.ch";
		location.region.add(Region.ZH);
		return location;
	}

}
