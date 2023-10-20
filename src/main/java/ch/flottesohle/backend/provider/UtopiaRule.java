package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class UtopiaRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate date = LocalDate.of(2023, 10, 23);
		while (date.isBefore(LocalDate.now())) {
			date = date.plusDays(7);
		}
		
		for (int i = 0; i <= 12; i++) {
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
			
			danceEvent.location = location;
			danceEvent.line = "Tanzen in Aarau";
			danceEvent.price = BigDecimal.valueOf(15);
			danceEvent.from = LocalTime.of(19, 30);

			danceEvent.description = "Ob Senior oder Student, blutiger Anfänger oder Profi – im «Utopia» steht die Freude am Tanz im Vordergrund. Von Discofox, Walzer, Rock`n`Roll und Jive über Salsa, Bachata bis hin zu Chachacha und Swing wird alles geboten was das Tänzerherz höherschlagen lässt. Strukturiert in Tanzstil-Blöcke wird ein bunter Mix von aktuellen Hits bis hin zu Klassikern aus vergangenen Jahren geliefert.\r\n"
					+ "\r\n"
					+ "Das stetig wachsende und altersübergreifende Publikum bestätigt: Paartanz ist angesagter den je! Lasst euch von der Musik mitreissen und tretet ein ins «Utopia Tanzlokal». Dem Tanztreffpunkt eurer Region. Gerne erfüllen wir auch Musik- und Tanzstilwünsche direkt ab dem Tanzparkett um den Abend für euch unvergesslich zu machen.";
			save(danceEvent, result);

			date = date.plusDays(7);
		}

		return result;
	}

	@Override
	protected Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Frey-Herosé-Strasse 20";
		location.city = "5000 Aarau";
		location.region.add(Region.AG);
		location.name = "Utopia Club";
		location.url = "https://www.utopia-club.ch";
		return location;
	}

}
