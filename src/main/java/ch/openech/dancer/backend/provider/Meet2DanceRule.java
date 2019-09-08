package ch.openech.dancer.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class Meet2DanceRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.FRIDAY) {
			start = start.plusDays(1);
		}

		List<LocalDate> exceptions = new ArrayList<>();
		exceptions.add(LocalDate.of(2019, 8, 30));
		exceptions.add(LocalDate.of(2019, 9, 20));
		exceptions.add(LocalDate.of(2019, 9, 27));
		exceptions.add(LocalDate.of(2019, 10, 25));
		exceptions.add(LocalDate.of(2019, 11, 22));
		exceptions.add(LocalDate.of(2019, 12, 6));
		exceptions.add(LocalDate.of(2019, 12, 13));

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);
			if (exceptions.contains(date)) {
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

			danceEvent.header = location.name;
			danceEvent.title = "Friday Dancing";
			danceEvent.from = LocalTime.of(21, 0);
			danceEvent.until = LocalTime.of(0, 0);
			danceEvent.description = "Tanzmusik ab CD in allen Tanzrichtungen, von Disco Fox, West coast Swing, ChaChaCha bis Tango Argentino. "
					+ "Natürlich können auch Wünsche angebracht werden und wer es gerne möchte kann bei Figurenproblemen auch die anwesenden Tanzlehrer um Rat fragen.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(8);

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