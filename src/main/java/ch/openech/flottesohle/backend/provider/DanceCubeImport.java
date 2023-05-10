package ch.openech.flottesohle.backend.provider;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class DanceCubeImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.tags.add(EventTag.Workshop);
		super.saveImportedEvent(event);
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
