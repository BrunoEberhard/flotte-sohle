package ch.flottesohle.backend.provider;

import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class MarinaLachenCrawler extends DanceLoungeCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getVenue() {
		return "marina";
	}
	
	@Override
	protected String getDescription() {
		return "17.30–19.00 Uhr Lounge Hour mit gratis Häppchen zum Drink als Warm-up. 19.00–23.00 Uhr Dance Music mit DJ Pete. Music-Style: Latin Vibes, 80’s Classics, House Beats. Freier Eintritt. Tischreservationen sind an diesen Abenden nicht möglich.";
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
