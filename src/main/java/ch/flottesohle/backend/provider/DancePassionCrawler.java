package ch.flottesohle.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

public class DancePassionCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.dance-passion.ch/events-tanzpartys";
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMAN);
	
	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		Element table = doc.selectFirst("table");
		Elements events = table.select("tr");
		for (Element event : events) {
			Elements columns = event.select("td");
			String title = columns.get(1).text().trim();
			if (!title.toLowerCase().contains("party")) {
				continue;
			}
			String dateString = columns.get(0).text().trim();
			LocalDate date = null;
			try {
				date = LocalDate.parse(dateString, DATE_FORMAT);
			} catch (DateTimeParseException x) {
				result.failedEvents++;
				continue;
			}

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;
			
			danceEvent.line = title;
			danceEvent.from = LocalTime.of(19, 30);
			danceEvent.until = LocalTime.of(23, 0);
			danceEvent.description = "Einmal im Monat, meistens an jedem ersten Samstag des Monats, gibt es bei uns eine Dance Party.<p>"
					+ "Unsere Kursteilnehmerinnen und Kursteilnehmer, ihre Freunde und Bekannte sowie alle Tanzbegeisterten können an diesem Abend ihre Tänze üben und Spass an Musik und Tanz mit Gleichgesinnten teilen.<p>"
					+ "Im Preis inbegriffen sind: Softgetränke, sowie ein kleines Imbissbuffet und natürlich etwas Süsses.<p>Zeit:  19:30 Uhr bis 23:00 Uhr. Achtung neue Zeiten!<p>"
					+ "Anmeldung jeweils bis am Donnerstag 079 291 04 01,<br>Email: dance(at)dance-passion.ch oder direkt in der Tanzschule.<br>"
					+ "Die Anmeldung ist verbindlich, kann aber bis 24 Stunden vor dem Anlass storniert werden.";
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(25);

			save(danceEvent, result);
		}
		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Dalmaziquai 69";
		location.city = "3005 Bern";
		location.name = "Dance Passion";
		location.url = "https://www.dance-passion.ch/";
		location.region.add(Region.BE);
		return location;
	}

}