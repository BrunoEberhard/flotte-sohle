package ch.openech.flottesohle.backend.provider.inactive;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.openech.flottesohle.backend.LocationProvider;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.DeeJay;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class AllmendhofBrochImport extends LocationProvider {
	private static final long serialVersionUID = 1L;

	@Override
	protected void saveImportedEvent(DanceEvent event) {
		DeeJay dj = getDeeJay("Erwin Live", "https://www.erwinlive.ch");

		event.from = LocalTime.of(19, 30);
		event.until = LocalTime.of(0, 0);
		event.description = "Unsere fröhlichen Tanzabende. Essen ab 18.30h, Tanzen ab 19.30h, Live Musik mit Erwin";
		event.price = BigDecimal.valueOf(0);
		event.deeJay = dj;
		event.status = EventStatus.generated;
		event.tags.add(EventTag.LiveBand);

		super.saveImportedEvent(event);
	}
	
	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.region.add(Region.AG);
		location.school = false;
		location.address = "Allmend 79";
		location.city = "5637 Beinwil/Freiamt";
		location.name = "Allmendhof Bloch";
		location.url = "https://www.allmendhof-broch.ch";
		return location;
	}

}
