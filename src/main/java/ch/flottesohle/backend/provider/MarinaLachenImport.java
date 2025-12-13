package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.flottesohle.backend.LocationProvider;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class MarinaLachenImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "DJ Night";
		event.from = LocalTime.of(17, 30);
		event.until = LocalTime.of(23, 00);
		event.description = "17.30–19.00 Uhr Lounge Hour mit gratis Häppchen zum Drink als Warm-up. 19.00–23.00 Uhr Dance Music mit DJ Pete. Music-Style: Latin Vibes, 80’s Classics, House Beats. Freier Eintritt. Tischreservationen sind an diesen Abenden nicht möglich";
		event.price = BigDecimal.ZERO;
		super.saveImportedEvent(event);
	}
	
	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Hafenstrasse 4";
		location.city = "8853 Lachen SZ";
		location.name = "Marina Lachen";
		location.url = "https://www.marinalachen.ch";
		location.region.add(Region.ZH);
		return location;
	}

}
