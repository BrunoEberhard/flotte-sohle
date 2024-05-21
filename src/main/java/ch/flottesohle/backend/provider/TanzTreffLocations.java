package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalTime;

import ch.flottesohle.backend.LocationProvider;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class TanzTreffLocations {

	public static class GasthausHaemikerbergImport extends LocationProvider {
		@Override
		protected void saveImportedEvent(DanceEvent event) {
			event.line = "Tanz-Treff";
			event.from = LocalTime.of(19, 30);
			event.until = LocalTime.of(0, 0);
			event.price = BigDecimal.valueOf(20);
			event.description = "Tanzen unter freiem Himmel - https://www.tanz-treff.ch/";
			super.saveImportedEvent(event);
		}

		@Override
		protected Location createLocation() {
			Location location = new Location();
			location.country = "Schweiz";
			location.region.add(Region.LU);
			location.address = "Hämikon-Berg 5";
			location.city = "6289 Hämikon";
			location.name = "Gasthaus Hämikerberg";
			location.url = "https://www.gasthaus-haemikerberg.ch/";
			return location;
		}
	}

	public static class RestaurantRatenImport extends LocationProvider {
		@Override
		protected void saveImportedEvent(DanceEvent event) {
			event.line = "Tanz-Treff";
			event.from = LocalTime.of(19, 30);
			event.until = LocalTime.of(0, 0);
			event.price = BigDecimal.valueOf(20);
			event.description = "Tanzen unter freiem Himmel - https://www.tanz-treff.ch/";
			super.saveImportedEvent(event);
		}

		@Override
		protected Location createLocation() {
			Location location = new Location();
			location.country = "Schweiz";
			location.region.add(Region.LU);
			location.address = "Ratenstrasse";
			location.city = "6315 Oberägeri ZG";
			location.name = "Restaurant Raten";
			location.url = "https://restaurant-raten.ch/";
			return location;
		}
	}
	
	public static class HeubodenImport extends LocationProvider {
		@Override
		protected void saveImportedEvent(DanceEvent event) {
			event.line = "Tanz-Treff";
			event.from = LocalTime.of(19, 0);
			event.until = LocalTime.of(23, 0);
			event.price = BigDecimal.valueOf(20);
			event.description = "Tanzen unter freiem Himmel - https://www.tanz-treff.ch/";
			super.saveImportedEvent(event);
		}

		@Override
		protected Location createLocation() {
			Location location = new Location();
			location.country = "Schweiz";
			location.region.add(Region.LU);
			location.address = "Heuboden 1";
			location.city = "6343 Risch-Rotkreuz";
			location.name = "Heuboden";
			location.url = "https://heuboden.ch/";
			return location;
		}
	}
	
	public static class SeebadiSeewenImport extends LocationProvider {
		@Override
		protected void saveImportedEvent(DanceEvent event) {
			event.line = "Tanz-Treff";
			event.from = LocalTime.of(19, 30);
			event.until = LocalTime.of(0, 0);
			event.price = BigDecimal.valueOf(20);
			event.description = "Tanzen unter freiem Himmel - https://www.tanz-treff.ch/";
			super.saveImportedEvent(event);
		}

		@Override
		protected Location createLocation() {
			Location location = new Location();
			location.country = "Schweiz";
			location.region.add(Region.LU);
			location.region.add(Region.ZH);
			location.address = "Seemattliweg 25";
			location.city = "6423 Seewen SZ";
			location.name = "Seebadi Seewen";
			location.url = "https://restaurant-raten.ch/";
			return location;
		}
	}
}
