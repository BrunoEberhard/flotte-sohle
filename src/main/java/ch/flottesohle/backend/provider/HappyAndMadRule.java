package ch.flottesohle.backend.provider;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;

public class HappyAndMadRule extends HappyAndMadCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		for (int i = 0; i<60; i++) {
			LocalDate date = LocalDate.now().plusDays(i);
			DayOfWeek dayOfWeek = date.getDayOfWeek();
			if (dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
				
		
				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				danceEvent.description = "Paartanz mit Discofox, Schlager, Rock´n´Roll und Standardtänze";
				if (dayOfWeek == DayOfWeek.TUESDAY) {
					danceEvent.line = "Rosentanznacht";
					danceEvent.description = "Mit Happy Tänzer, Tanzgarantie für alle Damen. Zur Begrüssung ein Glas Prosecco und eine Rose für die Damen";
				}
				
				danceEvent.from = dayOfWeek == DayOfWeek.TUESDAY ? LocalTime.of(19, 30) : LocalTime.of(20, 0);
				danceEvent.until = LocalTime.of(0, 0);
				if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
					danceEvent.until = LocalTime.of(2, 0);
				}

				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
				danceEvent.location = location;

				save(danceEvent, result);
			}

		}
		return result;
	}

}
