package ch.openech.flottesohle.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

// Läuft momentan über Import
@Deprecated
public class DanceVisionCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private static final String[] AGENDA_URLS = {
			"https://www.dance-vision.ch/events/tanzabend-alle-tanzstile/"
	};
	
	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		for (String url : AGENDA_URLS) {
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
			Element articleElement = doc.selectFirst("article.events");

			Element headerelement = articleElement.selectFirst("h1");
			if (headerelement == null) {
				continue;
			}
			String headline = headerelement.text();
			Element bodyElement = articleElement.selectFirst("section");
			String description = "";
			LocalTime from = LocalTime.of(20, 30);
			LocalTime until = LocalTime.of(0, 30);
			BigDecimal price = BigDecimal.valueOf(12);
			for (Element element : bodyElement.getAllElements()) {
				String content = element.ownText().trim();
				if (content.length() == 0) {
					continue;
				}
				int pos = content.indexOf("Zeit:");
				if (pos >= 0) {
					while (!Character.isDigit(content.charAt(pos))) {
						pos++;
					}
					String fromString = content.substring(pos, pos+5).replace(".", ":");
					from = LocalTime.parse(fromString);
					pos += 5;
					while (!Character.isDigit(content.charAt(pos))) {
						pos++;
					}
					String untilString = content.substring(pos, pos+5).replace(".", ":");
					until = LocalTime.parse(untilString);
					continue;
				}
				pos = content.indexOf("Eintritt:");
				if (pos >= 0) {
					while (!Character.isDigit(content.charAt(pos))) {
						pos++;
					}
					int endPos = pos;
					while (Character.isDigit(content.charAt(endPos)) || content.charAt(endPos) == '.') {
						endPos++;
					}
					price = new BigDecimal(content.substring(pos, endPos));
					continue;
				}
				
				// manchmal kommen daten in einer Zeile:
				// Freitag 08.02.2019 Freitag 10.05.2019 Freitag 13.09.2019 Freitag 13.12.2019
				while (content.length() > 0) {
					pos = content.indexOf(' ');
					if (pos < 0 || pos > content.length() - 11) {
						break;
					}
					String dateString = content.substring(pos + 1, pos + 11);
					try {
						LocalDate date = LocalDate.parse(dateString, formatter);
						content = content.substring(pos + 11).trim();
						if (date.isBefore(LocalDate.now())) {
							continue;
						}
						
						Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));
						
						DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());
						if (danceEvent.status == EventStatus.edited) {
							result.skippedEditedEvents++;
							continue;
						} else if (danceEvent.status == EventStatus.blocked) {
							result.skippedBlockedEvents++;
							continue;
						}
						
						danceEvent.status = EventStatus.generated;
						danceEvent.date = date;
						
						danceEvent.line = headline;
						danceEvent.from = from;
						danceEvent.until = until;
						danceEvent.description = description;
						danceEvent.location = location;
						danceEvent.price = price;
						
						save(danceEvent, result);
					} catch (DateTimeParseException ignored) {
						if (description.length() == 0) {
							description = content;
						}
						content = "";
					}
				}
			}
		}

		return result;
	}


	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bernstrasse 85";
		location.city = "3613 Steffisburg";
		location.name = "Dance Vision";
		location.url = "https://www.dance-vision.ch/";
		location.region.add(Region.BE);
		return location;
	}

}