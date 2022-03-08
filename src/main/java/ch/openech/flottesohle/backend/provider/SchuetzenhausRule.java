package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class SchuetzenhausRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter counter = new EventUpdateCounter();

		// stubete(counter);
		tanzhalle(counter);
		
		return counter;
	}

	private void tanzhalle(EventUpdateCounter result) {
		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.SATURDAY) {
			start = start.plusDays(1);
		}

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);
			if (date.isBefore(LocalDate.of(2022, 3, 12))) {
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
			danceEvent.tags.add(EventTag.LiveBand);

			danceEvent.from = LocalTime.of(20, 15);
			danceEvent.until = LocalTime.of(1, 15);

			danceEvent.description = "Eintritt inkl ein Getränk. "
					+ "Wir haben Tische mit gelben und roten Tischtücher: "
					+ "an den gelben Tischen sitzen Singles und Tanzfreudige, die zum Tanzen aufgefordert werden wollen. "
					+ "An den roten Tischen sitzen Päarchen oder die Personen die ungestört bleiben wollen. Wir haben ca.100 eigene Parkplätze. "
					+ "Bei Tanzanlässen erhalten Sie  Fr. 5.--  Parkhausentschädigung beim vorzeigen des Parkscheines an der Abendkasse.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(23);

			save(danceEvent, result);
		}
	}
	
	private void stubete(EventUpdateCounter result) {
		LocalDate start = LocalDate.now();
		LocalDate firstTuesday = start.with(TemporalAdjusters.firstInMonth(DayOfWeek.TUESDAY));
		LocalDate thirdTuesDay = firstTuesday.plusWeeks(2);
		if (thirdTuesDay.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 3; i++) {
			// jeweils am 3. Dienstag des Monats
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.firstInMonth(DayOfWeek.TUESDAY));
			date = date.plusWeeks(2);

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

			danceEvent.line = "Stubete";
			danceEvent.from = LocalTime.of(19, 0);
			danceEvent.until = LocalTime.of(23, 0);

			danceEvent.description = "Genießen Sie mit uns die Abende in geselliger Runde an der Stubete.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(0);

			save(danceEvent, result);
		}
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Schaffhauserstrasse 201";
		location.city = "8400 Winterthur";
		location.name = "Schützenhaus";
		location.url = "https://www.tanzhalle-schuetzenhaus.ch/stubete";
		location.region.add(Region.ZH);
		return location;
	}

}
