package ch.openech.dancer.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class PasadenaRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate date = LocalDate.now();
		LocalDate endDate = date.plusMonths(3);

		while (date.isBefore(endDate)) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());

			// Mo/Di wieder geschlossen. Falls schon erzeugt blockieren, sonst mit nächstem
			// Tag weiterfahren. Kann Mai 2020 wieder entfernt werden.
			if (date.getDayOfWeek() == DayOfWeek.MONDAY || date.getDayOfWeek() == DayOfWeek.TUESDAY) {
				if (danceEvent.id != null) {
					Backend.delete(danceEvent);
				}
				date = date.plusDays(1);
				continue;
			}

			if (danceEvent.status == EventStatus.edited) {
				result.skippedEditedEvents++;
				date = date.plusDays(1);
				continue;
			} else if (danceEvent.status == EventStatus.blocked) {
				result.skippedBlockedEvents++;
				date = date.plusDays(1);
				continue;
			}

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;
			
			danceEvent.location = location;
			danceEvent.priceReduced = BigDecimal.ZERO;

			int week = (date.getDayOfMonth() - 1) / 7 + 1;
			switch (date.getDayOfWeek()) {
			case MONDAY:
			case TUESDAY:
				date = date.plusDays(1);
				continue;
			case WEDNESDAY:
				danceEvent.from = LocalTime.of(19, 30);
				danceEvent.until = LocalTime.of(0, 0);
				danceEvent.price = BigDecimal.valueOf(10);
				if (week == 5) {
					danceEvent.line = "Swing Night";
				} else {
					danceEvent.line = "Ballroom dance";
				}
				break;
			case THURSDAY:
				danceEvent.from = LocalTime.of(20, 30);
				danceEvent.until = LocalTime.of(1, 0);
				danceEvent.price = BigDecimal.valueOf(10);
				danceEvent.line = "Music Timeline";
				break;
			case FRIDAY:
				danceEvent.from = LocalTime.of(20, 30);
				danceEvent.until = LocalTime.of(3, 0);
				danceEvent.price = BigDecimal.valueOf(17);
				danceEvent.line = "Hit Dance Night";
				danceEvent.description = "ab 23.30 Uhr: Mixed Dancing PPP Pasadena Power Partysound";
				if (date.equals(LocalDate.of(2020, 01, 17))) {
					danceEvent.line = "Januarloch Party";
					danceEvent.price = BigDecimal.ZERO;
				}
				break;
			case SATURDAY:
				danceEvent.from = LocalTime.of(20, 30);
				danceEvent.until = LocalTime.of(3, 0);
				danceEvent.price = BigDecimal.valueOf(17);
				if (week == 1 || week == 3) {
					danceEvent.line = "Schlagerparty";
				} else if (week == 4) {
					danceEvent.line = "La Notte Italiana";
					danceEvent.description = "Gratis die besten Pizzas für alle!";
				}
				if (date.equals(LocalDate.of(2020, 3, 7)) || date.equals(LocalDate.of(2020, 10, 31))) {
					danceEvent.line = "Ländlerabend";
					danceEvent.price = BigDecimal.valueOf(25);
					danceEvent.priceReduced = null;
					danceEvent.description = "7. März und 31. Oktober 2020 mol lüpfig em Samschtig im Pasadena… Ländlertanzobig feat. dä Nötzli mit dä Chlötzli Eintrittspreise: Sitzplätze CHF 30 Bar- und Stehplätze CHF 25 (Reservationen unter 044 908 30 00)";
				}
				break;
			case SUNDAY:
				danceEvent.from = LocalTime.of(19, 30);
				danceEvent.until = LocalTime.of(0, 0);
				danceEvent.price = BigDecimal.valueOf(10);
				if (week == 2 || date.plusWeeks(1).getMonth() != date.getMonth()) {
					danceEvent.line = "Facebookparty";
					danceEvent.description = "Wer sich auf der Facebookseite \"Dancing Sunday\" auf der Gästeliste einträgt, (Wichtig! Bei der Veranstaltung anmelden!) kommt gratis ins PASADENA. Anmeldeschluss ist immer der Samstag um Mitternacht vor der Facebookparty";
				} else {
					danceEvent.line = "Dancing Sunday";
				}
				break;
			}

			save(danceEvent, result);
			date = date.plusDays(1);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Chriesbaumstrasse 2";
		location.latitude = BigDecimal.valueOf(47.3814611);
		location.longitude = BigDecimal.valueOf(8.6801448);
		// location.osm = 1682227;
		location.city = "8604 Volketswil";
		location.name = "Pasadena";
		location.url = "http://www.pasadena.ch";
		location.region.add(Region.ZH);
		return location;
	}

}
