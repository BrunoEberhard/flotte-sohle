package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
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
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class GalacticCrawler extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.galactic-dance.ch/events/";
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d", Locale.GERMAN);
	
	@Override
	public int crawlEvents() {
		try {
			Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

			Elements events = doc.select(".type-tribe_events");
			for (Element element : events) {
				Element start = element.selectFirst(".tribe-event-date-start");
				LocalDate date = extractLocalDate(start);
				LocalTime from = extractTime(start);

				LocalTime until = null;
				Element end = element.selectFirst(".tribe-event-date-end");
				if (end != null) {
					until = extractTime(end);
				} else {
					end = element.selectFirst(".tribe-event-time");
					if (end != null) {
						until = LocalTime.parse(end.text(), TIME_FORMATTER);
					}
				}

				if (DanceEvent.isDuringTheDay(from))
					continue;

				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
						By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				String subtitle = element.selectFirst(".tribe-events-list-event-title").text();
				if (subtitle.contains("Flamenco")) {
					continue;
				} else if ("Ritmo Dell’Amicizia con Filippe & Enza".equals(subtitle)) {
					subtitle = "Ritmo Dell’Amicizia";
				} else if ("Musigstubete mit Viva Varia mit Fründe".equals(subtitle)) {
					subtitle = "Musigstubete";
				}

				Element description = element.selectFirst(".tribe-events-list-photo-description");

				danceEvent.status = EventStatus.published;
				danceEvent.date = date;
				danceEvent.title = location.name;
				danceEvent.subTitle = subtitle;
				danceEvent.from = from;
				danceEvent.until = until;
				danceEvent.description = description.text();
				danceEvent.location = location;

				Backend.save(danceEvent);
			}
			return events.size();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private LocalDate extractLocalDate(Element element) {
		String text = element.ownText();
		int colonIndex = text.indexOf(':');
		MonthDay monthDay = MonthDay.parse(text.substring(0, colonIndex), DATE_FORMATTER);
		LocalDate date = monthDay.atYear(Year.now().getValue());
		if (date.isBefore(LocalDate.now())) {
			date = date.plusYears(1);
		}
		return date;
	}

	private LocalTime extractTime(Element element) {
		String text = element.ownText();
		int colonIndex = text.indexOf(':');
		String timeString = text.substring(colonIndex + 1).trim();
		LocalTime time = LocalTime.parse(timeString, TIME_FORMATTER);
		return time;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Albulastrasse 47";
		location.city = "8048 Zürich";
		location.name = "Galactic Dance";
		location.url = "https://www.galactic-dance.ch/";
		location.region.add(Region.ZH);
		return location;
	}

}