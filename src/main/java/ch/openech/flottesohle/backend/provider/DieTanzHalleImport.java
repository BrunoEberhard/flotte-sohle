package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class DieTanzHalleImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.from = LocalTime.of(20, 00);
		event.until = LocalTime.of(0, 0);
		event.price = BigDecimal.valueOf(15);
		event.priceReduced = BigDecimal.valueOf(10);
		event.description = "10 x 20m reine Tanzfläche. Dank beschränkter Anzahl von Sitzplätzen keine Überfüllung. Reservation wird empfohlen ab 4 Personen.";
		super.saveImportedEvent(event);
	}
	
	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.region.add(Region.AG);
		location.school = false;
		location.address = "Lenzburgerstrasse 2";
		location.city = "5702 Niederlenz";
		location.name = "Die TanzHalle";
		location.url = "http://dietanzhalle.ch/";
		return location;
	}
}
