package ch.flottesohle.backend.provider;

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
import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class Time2DanceCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://time2dance.ch/tanzabend.html";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		for (Month month : Month.values()) {
			String monthText = month.getDisplayName(TextStyle.FULL, Locale.GERMAN) + ":";
			Elements elements = doc.getElementsContainingOwnText(monthText);
			if (elements.size() > 0) {
				Element element = elements.get(0);
				LocalDate date = extractLocalDate(element, monthText);
				if (date != null) {
					Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

					DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

					danceEvent.status = EventStatus.generated;
					danceEvent.date = date;
					
					danceEvent.line = "Saturday Dance Night";
					danceEvent.from = LocalTime.of(20, 0);
					danceEvent.until = LocalTime.of(23, 59);
					danceEvent.description = "Einmal im Monat laden wir dich ein zu unserem Tanzabend.";
					// danceEvent.organizer = organizer;
					danceEvent.location = location;

					save(danceEvent, result);
				}
			}
		}
		return result;
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
		location.address = "Räffelstrasse 25";
		location.city = "8045 Zürich";
		location.name = "time2dance";
		location.url = "http://www.time2dance.ch";
		location.region.add(Region.ZH);
		return location;
	}

}