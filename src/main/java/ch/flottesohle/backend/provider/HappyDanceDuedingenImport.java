package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.flottesohle.backend.LocationProvider;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class HappyDanceDuedingenImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.from = LocalTime.of(19, 30);
		event.until = LocalTime.of(23, 0);
		event.price = BigDecimal.valueOf(30);
		event.priceReduced = BigDecimal.valueOf(25);
		event.description = "Der gemütlich gesellige Tanzabend mit Musik von Walzer bis Cha-Cha-Cha.\n"
				+ "Ein kleiner Imbiss ist dabei, denn es gehört einfach dazu, etwas zusammen zu sitzen, "
				+ "Neuigkeiten auszutauschen und zu lachen. Die Tanz-Party ist vor allem für Leute interessant, "
				+ "welche mindestens ein wenig Kenntnisse in den 10 Standard- und Lateintänzen haben.\nBitte Auf Webseite anmelden";

		super.saveImportedEvent(event);
	}
	
	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bonnstrasse 22A";
		location.city = "3186 Düdingen";
		location.region.add(Region.BE);
		location.name = "Happy Dance";
		location.url = "http://www.happydance.ch";
		return location;
	}

}
