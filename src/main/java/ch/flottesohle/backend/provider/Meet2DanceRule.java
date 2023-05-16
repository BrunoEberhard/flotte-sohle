package ch.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.EventTag;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class Meet2DanceRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();
		
		lastSaturdayEvents(result);
		firstFridayEvents(result, true);
		firstFridayEvents(result, false);

		return result;
	}

	protected void lastSaturdayEvents(EventUpdateCounter result) {
		LocalDate start = LocalDate.now();
		LocalDate lastSaturday = start.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));
		if (lastSaturday.isBefore(start)) {
			start = start.plusMonths(1);
		}

		for (int i = 0; i < 4; i++) {
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));
			if (date.isBefore(LocalDate.now())) {
				continue;
			}
			
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.isImportable(result)) {
				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
				
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
		}
	}

	protected void firstFridayEvents(EventUpdateCounter result, boolean first) {
		LocalDate start = LocalDate.now();

		for (int i = 0; i < 4; i++) {
			LocalDate date = start.plusMonths(i).with(TemporalAdjusters.firstInMonth(DayOfWeek.FRIDAY));
			if (!first) {
				date = date.plusDays(7);
			}
			if (date.isBefore(LocalDate.now())) {
				continue;
			}
			
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.isImportable(result)) {
				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
	
				
				danceEvent.line = "Friday Dancing";
				danceEvent.from = LocalTime.of(21, 00);
				danceEvent.until = LocalTime.of(23, 30);
				danceEvent.description = "Tanzmusik ab CD in allen Tanzrichtungen, von Disco Fox, West coast Swing, ChaChaCha bis Tango Argentino.\r\n"
						+ "Natürlich können auch Wünsche angebracht werden und wer es gerne möchte kann bei Figurenproblemen auch die anwesenden Tanzlehrer um Rat fragen.";
				danceEvent.location = location;
				danceEvent.price = BigDecimal.valueOf(10);
	
				save(danceEvent, result);
			}
		}
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