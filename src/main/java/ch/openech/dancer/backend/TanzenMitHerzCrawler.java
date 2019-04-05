package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.DayOfWeek;
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
import ch.openech.dancer.model.Region;

public class TanzenMitHerzCrawler extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.tanzenmitherz.ch/index.php?id=19";

	@Override
	public int crawlEvents() {
		try {
			Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
			Element content = doc.selectFirst("#content");

			Elements bodytexts = content.select(".bodytext");
			bodytexts.forEach(element -> {
				Element firstSpan = element.selectFirst("span");
				if (firstSpan != null) {
					String text = firstSpan.ownText();
					int index = text.indexOf(", ");
					if (index > 0) {
						text = text.substring(index + 2);
						index = text.indexOf(" 20");
						if (index > 0) {
							text = text.substring(0, index + 5);

							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMAN);

							LocalDate date = LocalDate.parse(text, formatter);
							Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
									By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

							DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

							danceEvent.status = EventStatus.published;
							danceEvent.date = date;
							danceEvent.title = location.name;
							danceEvent.subTitle = "Tanzabend";
							if (date.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
								danceEvent.from = LocalTime.of(20, 30);
								danceEvent.until = LocalTime.of(23, 00);
							} else {
								danceEvent.from = LocalTime.of(20, 0);
								danceEvent.until = LocalTime.of(23, 59);
							}
							danceEvent.description = "Unsere Tanzabende sind offen für alle Tanzbegeisterten, ob Kursteilnehmer oder nicht, alle sind herzlich willkommen!! Für jeden Tanzabend wird die Musik individuell zusammen gestellt, Klänge, die sich den Gästen anpassen. Es kann auch mal ein Linedance sein!";
							// danceEvent.organizer = organizer;
							danceEvent.location = location;
							danceEvent.tags.add(EventTag.Workshop);

							Backend.save(danceEvent);

						}
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
		location.address = "Zürichstrasse 38";
		location.city = "8306 Brüttisellen";
		location.name = "Tanzen mit Herz";
		location.url = "https://www.tanzenmitherz.ch";
		location.region.add(Region.ZH);
		return location;
	}

}