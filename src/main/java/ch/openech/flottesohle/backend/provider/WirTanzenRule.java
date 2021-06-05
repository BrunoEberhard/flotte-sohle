package ch.openech.flottesohle.backend.provider;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.DeeJay;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class WirTanzenRule extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	@Override
	public EventUpdateCounter updateEvents() {
		EventUpdateCounter result = new EventUpdateCounter();

		LocalDate start = LocalDate.now();
		while (start.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
			start = start.plusDays(1);
		}

		DeeJay deeJay= Backend.find(DeeJay.class, By.field(DeeJay.$.name, "DJ Menzi")).get(0);

		for (int i = 0; i < 12; i++) {
			LocalDate date = start.plusWeeks(i);
			if (date.isAfter(LocalDate.of(2020, 8, 12))) {
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

			
			danceEvent.line = "Wir Tanzen";
			danceEvent.from = LocalTime.of(19, 30);
			danceEvent.description = "Tanzabend mit interessanter, stimmungsvoller Tanzmusik individuell auf die anwesenden Gäste abgestimmt, mit viel DiscoFox/Discoswing. Im Eintritt 6.- Getränkegutschein enthalten.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(15);
			danceEvent.deeJay = deeJay;

			save(danceEvent, result);
		}

		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Zürcherstrasse 27";
		location.city = "8620 Wetzikon";
		location.name = "Evita Club";
		location.url = "https://web.facebook.com/wirtanzenzho/";
		location.region.add(Region.ZH);
		return location;
	}

}