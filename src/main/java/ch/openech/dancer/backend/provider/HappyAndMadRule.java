package ch.openech.dancer.backend.provider;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;

public class HappyAndMadRule extends HappyAndMadCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		for (int i = 0; i<60; i++) {
			LocalDate date = LocalDate.now().plusDays(i);
			DayOfWeek dayOfWeek = date.getDayOfWeek();
			if (dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
				
		
				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				danceEvent.header = "Happy and Mad";
				danceEvent.title = location.name;
				if (dayOfWeek == DayOfWeek.FRIDAY) {
					danceEvent.description = "Paartanz mit Discofox, Schlager, Rock´n´Roll und Standardtänze. Floor 2 Latino Musik.";
				} else {
					danceEvent.description = "Paartanz mit Discofox, Schlager, Rock´n´Roll und Standardtänze";
				}

				danceEvent.from = dayOfWeek == DayOfWeek.THURSDAY ? LocalTime.of(20, 0) : LocalTime.of(19, 30);
				danceEvent.until = LocalTime.of(0, 0);
				if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
					danceEvent.until = LocalTime.of(1, 30);
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
