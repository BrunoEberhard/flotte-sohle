package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;

public class DanceInnCrawler extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "http://www.danceinn.ch/programm-2/";

	@Override
	public int crawlEvents() {
		try {
			Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

			Elements events = doc.select(".event");
			events.forEach(element -> {
				Element dateElement = element.selectFirst("h4");

				String dateText = dateElement.ownText();
				int index = dateText.indexOf(", ");
				if (index > 0) {
					dateText = dateText.substring(index + 2);
					index = dateText.indexOf(" 20");
					if (index > 0) {
						dateText = dateText.substring(0, index + 5).trim();

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMAN);

						LocalDate date = LocalDate.parse(dateText, formatter);

						Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
								By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

						DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

						Element text = element.select("h4").get(1);
						danceEvent.title = "Dance Inn";
						danceEvent.description = text.ownText().trim();

						danceEvent.from = LocalTime.of(21, 0);
						danceEvent.until = LocalTime.of(1, 0);
						danceEvent.status = EventStatus.generated;
						danceEvent.date = date;
						// danceEvent.organizer = organizer;
						danceEvent.location = location;
						danceEvent.tags.add(EventTag.Workshop);

						Backend.save(danceEvent);

					}
				}
			});
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Murgtalstrasse 20";
		location.city = "9542 Münchwilen TG";
		location.name = "Dance Inn";
		location.url = "http://www.danceinn.ch/";
		return location;
	}

}