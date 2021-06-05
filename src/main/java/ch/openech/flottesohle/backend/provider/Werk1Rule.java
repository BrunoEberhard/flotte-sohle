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

public class Werk1Rule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.MONDAY) {
			start = start.plusDays(1);
		}

		// DeeJay deeJayJanosch = Backend.find(DeeJay.class, By.field(DeeJay.$.name, "DJ
		// Janosch")).get(0);

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);
			if (date.isBefore(LocalDate.of(2020, 06, 29))) {
				continue;
			}
			
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

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

			
			danceEvent.line = "Tanz mit mir";
			danceEvent.from = LocalTime.of(21, 00);
			danceEvent.until = LocalTime.of(0, 30);
			danceEvent.description = "DiscoSwing, West Coast Swing, Jive, Schlager, Walzer. Findet an Feiertagen im DanceINN (Münchwilen) statt.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(9);
			danceEvent.priceReduced = BigDecimal.valueOf(7);
			// danceEvent.deeJay = deeJayJanosch;
			danceEvent.deeJay = null;

			save(danceEvent, result);
		}
//		DanceInnCrawler.handleWerk1InDanceInn();

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Fabrikstrasse 7";
		location.city = "9200 Gossau SG";
		location.name = "Werk 1";
		location.url = "http://www.werk-1.ch/";
		location.region.add(Region.SG);
		return location;
	}

}