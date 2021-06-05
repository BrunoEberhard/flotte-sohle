package ch.openech.flottesohle.backend.provider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class PilatusKellerRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();

		for (int i = 0; i < 12 * 7; i++) {
			LocalDate date = start.plusDays(i);
			if (date.getDayOfWeek() == DayOfWeek.MONDAY || date.getDayOfWeek() == DayOfWeek.TUESDAY) {
				continue;
			}
			if (!date.isBefore(LocalDate.of(2019, 12, 23)) && !date.isAfter(LocalDate.of(2019, 12, 25))) {
				continue;
			}

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.status == EventStatus.edited) {
				result.skippedEditedEvents++;
				continue;
			} else if (danceEvent.status == EventStatus.blocked) {
				result.skippedBlockedEvents++;
				continue;
			}

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;

			danceEvent.from = LocalTime.of(20, 0);
			if (date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
				danceEvent.until = LocalTime.of(1, 30);
			} else {
				danceEvent.until = LocalTime.of(0, 30);
			}
			switch (date.getDayOfWeek()) {
			case WEDNESDAY:
				danceEvent.line = "Oldies, Schlager";
				danceEvent.description = "Oldies, Schlager, Standards mit angenehm Platz zum \"richtig\" Tanzen.";
				break;
			case THURSDAY:
				danceEvent.line = "Rosenabend";
				danceEvent.description = "Rosenabend, bei jeder Runde eine Rose für die Damen.";
				break;
			case FRIDAY:
				danceEvent.line = "Happy Weekend";
				danceEvent.description = "Happy Weekend";
				break;
			case SATURDAY:
				danceEvent.description = "Tanzparty mit Live Band, angenehme und beschwingte Stimmung.";
				danceEvent.tags.add(EventTag.LiveBand);
				break;
			case SUNDAY:
				danceEvent.line = "Spaghetti-Plausch";
				danceEvent.description = "Der berühmte Spaghetti-Plausch und Tanz zu Live Musik.";
				danceEvent.tags.add(EventTag.LiveBand);
				danceEvent.tags.add(EventTag.Food);
				break;
			default:
				break;
			}
			danceEvent.location = location;

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Seestrasse 34";
		location.city = "6052 Hergiswil NW";
		location.name = "Pilatus Keller";
		location.url = "https://pilatushotel.ch/de/dancing-pilatuskeller/";
		location.region.add(Region.LU);
		return location;
	}

}
