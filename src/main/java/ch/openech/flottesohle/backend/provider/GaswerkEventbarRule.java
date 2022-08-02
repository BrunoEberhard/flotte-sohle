package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class GaswerkEventbarRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String DESCRIPTION = "<p>Innert kürzester Zeit hat sich die Paartanzreihe «Tanzwerk» im Gaswerk als fester Bestandteil im überregionalen Angebot für Paartanzliebhaber etabliert. Von Discofox, Walzer, Rock`n`Roll und Jive über Salsa, Bachata bis hin zu Chachacha und Swing wird alles geboten was das Tänzerherz höherschlagen lässt.</p>"
			+ "<p>Strukturiert in Tanzstil-Blöcke wird durch den Abend geführt. Dabei garantieren wir rhythmisches Vergnügen durch sämtliche Tanzstile.</p>"
			+ "<p>Geboten wird ein bunter Mix von aktuellen Hits, bis hin zu Klassikern aus vergangenen Jahren in den Bereichen: Pop, Classics, Rock, EDM, Swing, R&B bis hin zu Chanson und Schlager. Das stetig wachsende und altersübergreifende Publikum bestätigt: Paartanz ist angesagter den je!</p>"
			+ "<p>Kein Tanzpartner? kein Problem. Gerne bittet unser Gaswerk-Tänzer unsere weiblichen Gäste ohne Begleitung aufs Parkett. Lasst euch von der Musik mitreissen und tretet ein ins «Tanzwerk». Dem Paartanztreffpunkt eurer Region.</p>"
			+ "<p>Gerne erfüllen wir auch Musik- und Tanzstilwünsche direkt ab dem Tanzparkett um den Abend für euch unvergesslich zu machen. Der Eintritt zum Vergnügen ist kostenlos!</p>";

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.SUNDAY) {
			start = start.plusDays(1);
		}

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);
			if (date.isBefore(LocalDate.of(2019, 8, 18))) {
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
			danceEvent.until = LocalTime.of(0, 0);
			danceEvent.price = BigDecimal.valueOf(10);
			danceEvent.priceReduced = BigDecimal.valueOf(5);
			danceEvent.description = DESCRIPTION;
			danceEvent.location = location;

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bahnhofstrasse 180B";
		location.city = "6423 Seewen";
		location.name = "Gaswerk Eventbar";
		location.url = "https://www.gaswerk-eventbar.ch/";
		location.region.add(Region.LU);
		return location;
	}

}
