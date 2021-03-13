package ch.openech.dancer.backend.provider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class VerschiedeneImport {

	public static class DanceAndDineImport extends DanceEventProvider {
		private static final long serialVersionUID = 1L;

		@Override
		public EventUpdateCounter updateEvents() throws Exception {
			EventUpdateCounter result = new EventUpdateCounter();

			LocalDate date = LocalDate.of(2019, 10, 26);
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			if (!danceEventOptional.isPresent() && date.isAfter(LocalDate.now())) {
				DanceEvent event = new DanceEvent();
				event.date = date;
				event.location = this.location;
				event.line = "Dance and Dine";

				event.line = "Dance and Dine";
				event.description = "Die Türen werden für Sie um 18:30 geöffnet. Am Dance & Dine 2019 erwartet sie:" + "<ul>" + "<li>Ein gediegenes 4-Gänge-Menu (1. Gang um 19:30Uhr)</li>"
						+ "<li>Live vorgetragene Piano-Musik während des Apéro's und während des Essens</li>"
						+ "<li>Ein Tanzmusik DJ welcher Sie bis in die Morgenstunden zum Tanzen motivieren wird</li>" + "<li>Eine Mitternachtsshow zum Thema Tanzen</li>" + "</ul>"
						+ "und natürlich viele motivierte und gut gelaunte Helfer des Turnverein Wohlen, welche Ihnen den ganzen Abend zur Seite stehen.";
				event.status = EventStatus.generated;
				event.from = LocalTime.of(18, 30);
				event.tags.add(EventTag.Food);
				event.tags.add(EventTag.Show);
				event.tags.add(EventTag.LiveBand);


				save(event, result);
			}

			return result;
		}

		@Override
		protected Location createLocation() {
			Location location = new Location();
			location.country = "Schweiz";
			location.address = "Zentralstrasse 30A";
			location.city = "5610 Wohlen";
			location.region.add(Region.AG);
			location.name = "Casino Wohlen";
			location.url = "https://www.dance-and-dine.ch/";
			return location;
		}

	}
}
