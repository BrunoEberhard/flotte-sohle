package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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

public class Time2DanceCrawler extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://time2dance.ch/tanzpartys.html";

	@Override
	public int crawlEvents() {
		int count = 0;
		try {
			Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
			for (Month month : Month.values()) {
				String monthText = month.getDisplayName(TextStyle.FULL, Locale.GERMAN) + ":";
				Elements elements = doc.getElementsContainingOwnText(monthText);
				if (elements.size() > 0) {
					Element element = elements.get(0);
					LocalDate date = extractLocalDate(element, monthText);
					if (date != null) {
						Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
								By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

						DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

						danceEvent.status = EventStatus.published;
						danceEvent.date = date;
						danceEvent.header = location.name;
						danceEvent.title = "Saturday Dance Night";
						danceEvent.from = LocalTime.of(20, 0);
						danceEvent.until = LocalTime.of(23, 59);
						danceEvent.description = "Einmal im Monat laden wir dich ein zu unserem Tanzabend.";
						// danceEvent.organizer = organizer;
						danceEvent.location = location;

						Backend.save(danceEvent);
						count++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	private LocalDate extractLocalDate(Element dateTag, String monthText) {
		String text = dateTag.outerHtml();
		int pos = text.indexOf(monthText);
		if (pos > -1) {
			text = text.substring(pos + monthText.length());
			int endIndex = text.indexOf('<');
			text = text.substring(0, endIndex).trim();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			return LocalDate.parse(text, formatter);
		}
		return null;
	}

	
	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Binzstrasse 9";
		location.city = "8045 Zürich";
		location.name = "time2dance";
		location.url = "http://www.time2dance.ch";
		location.region.add(Region.ZH);
		return location;
	}
	
}