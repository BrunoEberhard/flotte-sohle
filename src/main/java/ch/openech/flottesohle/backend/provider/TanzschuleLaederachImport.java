package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class TanzschuleLaederachImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.from = LocalTime.of(20, 30);
		event.until = LocalTime.of(0, 0);
		event.price = BigDecimal.valueOf(17);
		event.description = "Einmal im Monat heisst „Parkett frei“ für alle Tanzbegeisterten. An unserer Party ist jeder herzlich willkommen, aber die Teilnehmerzahl ist limitiert. Bitte reservieren Sie die Plätze für Sie und Ihre Begleitperson(en) vorab.";

		super.saveImportedEvent(event);
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Schaffhauserstrasse 330";
		location.city = "8050 Zürich";
		location.region.add(Region.ZH);
		location.name = "Tanzschule Läderach";
		location.url = "https://letsdance.ch/";
		return location;
	}

}
