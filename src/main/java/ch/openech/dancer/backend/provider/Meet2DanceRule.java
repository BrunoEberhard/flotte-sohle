package ch.openech.dancer.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class Meet2DanceRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();
		
		LocalDate start = LocalDate.now();
		LocalDate lastSaturday = start.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));
		if (lastSaturday.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 4; i++) {
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));
			
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

			danceEvent.header = location.name;
			danceEvent.line = "Tanzparty mit RoMa";
			danceEvent.from = LocalTime.of(20, 30);
			danceEvent.until = LocalTime.of(0, 0);
			danceEvent.description = "Mit den unvergänglichen Dance Hits \n" + 
					"von den 80er Jahren bis Heute! \n" + 
					"Und das alles im Paartanz-Rhythmus \n" + 
					"von Walzer über Tango, Rumba, ChaChaCha, \n" + 
					"Jive bis zum Disco-Fox!";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(10);
			danceEvent.tags.add(EventTag.Workshop);

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Wehntalerstrasse 6";
		location.city = "8154 Oberglatt";
		location.name = "Meet2Dance";
		location.url = "https://www.meet2dance.ch/";
		location.region.add(Region.ZH);
		return location;
	}

}