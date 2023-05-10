package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;

// angefragt am 14.7.19 ob sie überhaupt aufgenommen werden wollen
public class TanzPunktRynachImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "Tanzabend";
		event.from = LocalTime.of(20, 00);
		event.until = LocalTime.of(2, 00);
		event.price = BigDecimal.valueOf(20);
		event.priceReduced = BigDecimal.valueOf(15);
		event.tags.add(EventTag.Workshop);

		event.description = "Der Verein tanzpunktrynach organisiert vier Mal im Jahr einen Tanzabend in Reinach. "
				+ "Vor dem Anlass werden in einem Workshop die grundlegenden Tanzschritte für Neueinsteiger geübt, für Fortgeschrittene bietet sich die Gelegenheit das früher erlerntes wiederaufzufrischen und Neues zu lernen, ein erfahrener Tanzlehrer hilft dir dabei. "
				+ "Nach einer Stunde geht es los: von 20:00 Uhr bis 02:00 Uhr wird getanzt und zwar zu Discofox-Gesellschaftstanz, Standard- und Latin!";

		super.saveImportedEvent(event);
	}
	
	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		// location.region.add(Region.SG);
		location.school = true;
		location.address = "";
		location.city = "";
		location.name = "Tanz Punkt Rynach";
		location.url = "https://www.tanzpunktrynach.ch/";
		return location;
	}

}
