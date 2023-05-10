package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.flottesohle.backend.LocationProvider;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class DancersWorldImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		event.line = "Dancer’s Night";

		event.status = EventStatus.generated;
		event.description = "Ein stilvolles, klimatisiertes Ambiente lädt dich zum Verweilen ein. Dank gutem Mix von modernen Songs und klassischer Tanzmusik ist für alle Teilnehmer/innen das Passende dabei. "
				+ "Getanzt und geübt werden kann zu Standard- und Lateinmusik, Discofox, Tango Argentino und Salsa. Verbringe einen schönen Abend mit anderen Tanzbegeisterten.";
		event.price = BigDecimal.valueOf(10);
		event.from = LocalTime.of(20, 0);
		event.until = LocalTime.of(23, 30);

		super.saveImportedEvent(event);
	}
	
	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bernstrasse 388";
		location.city = "8953 Dietikon";
		location.region.add(Region.ZH);
		location.name = "Dancer's World";
		location.url = "https://dancers-world.ch/";
		return location;
	}

}
