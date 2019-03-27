package ch.openech.dancer.backend;

import java.io.IOException;
import java.io.InputStream;
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

	private static final String AGENDA_URL = "http://www.pasadena.ch/agendanews/";

	@Override
	public int crawlEvents() {
		try {
			InputStream in = getClass().getResourceAsStream("/ch/openech/dancer/data/galactic.html");
			Document doc = Jsoup.parse(in, null, ".");

			Elements events = doc.select(".type-tribe_events");
			for (Element element : events) {
				Element start = element.selectFirst(".tribe-event-date-start");
				LocalDate date = extractLocalDate(start);
				LocalTime from = extractTime(start);

				Element end = element.selectFirst(".tribe-event-date-end");
				LocalTime until = extractTime(end);

				if (DanceEvent.isDuringTheDay(from))
					continue;

				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
						By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				Element title = element.selectFirst(".tribe-events-list-event-title");
				Element description = element.selectFirst(".tribe-events-list-photo-description");

				danceEvent.status = EventStatus.published;
				danceEvent.date = date;
				danceEvent.title = title.text();
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
		int spaceIndex = text.indexOf(' ');
		int colonIndex = text.indexOf(':');
		String month = text.substring(0, spaceIndex);
		String day = text.substring(spaceIndex + 1, colonIndex);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d", Locale.GERMAN);
		MonthDay monthDay = MonthDay.parse(text.substring(0, colonIndex), formatter);
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
		LocalTime time = LocalTime.parse(timeString, formatter);
		return time;
	}


	private String extractDanceEventTitle(Element paragraphElement) {
		Elements elements = paragraphElement.nextElementSibling().getElementsByTag("h3");
		if (elements != null && !elements.isEmpty()) {
			return elements.get(0).childNode(0).attr("title");
		}
		return null;
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