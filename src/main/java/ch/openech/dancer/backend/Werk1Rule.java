package ch.openech.dancer.backend;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.DeeJay;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class Werk1Rule extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	@Override
	public int crawlEvents() {
		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.MONDAY) {
			start = start.plusDays(1);
		}

		DeeJay deeJayJanosch = Backend.find(DeeJay.class, By.field(DeeJay.$.name, "DJ Janosch")).get(0);

		int generated = 0;
		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
					By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			if (!danceEventOptional.isPresent()) {
				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;

				danceEvent.header = location.name;
				danceEvent.title = "Tanz mit mir";
				danceEvent.from = LocalTime.of(21, 00);
				danceEvent.until = LocalTime.of(0, 30);
				danceEvent.description = "DiscoSwing, West Coast Swing, Jive, Schlager, Walzer. Findet an Feiertagen im DanceINN (Münchwilen) statt.";
				danceEvent.location = location;
				danceEvent.price = BigDecimal.valueOf(9);
				danceEvent.priceReduced = BigDecimal.valueOf(7);
				danceEvent.deeJay = deeJayJanosch;

				Backend.save(danceEvent);
				generated++;
			}
		}
		DanceInnCrawler.handleWerk1InDanceInn();

		return generated;
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