package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class SaborLatinoImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.description = "Dance Night: Ein fetziger Mix aus Musik und Tänzen auf 2 Dancefloors.<br>"
				+ "Floor 1: Discoswing, Jive und mehr mit DJ PiHunter.<br>"
				+ "Floor 2: Salsa, Bachata, Kizomba u. mehr DJ Eddie.<br>"
				+ "Vor der Party findet von 20:00 – 21:00 Uhr ein Discoswing Workshop statt für Mittelstufe Tänzer mit Cornelia  & Patrice.";
		event.from = LocalTime.of(21, 0);
		event.until = LocalTime.of(3, 0);
		event.price = BigDecimal.valueOf(10);
		event.line = "Disco/Jive & Salsa";
		event.tags.add(EventTag.Workshop);
		super.saveImportedEvent(event);
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Werkstrasse 16";
		location.city = "8400 Winterthur";
		location.region.add(Region.ZH);
		location.name = "Sabor Latino";
		location.url = "https://saborlatino.ch/";
		return location;
	}

}
