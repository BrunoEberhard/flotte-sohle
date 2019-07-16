package ch.openech.dancer.backend;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.DeeJay;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class DanceoramaCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("d. MMMM yyyy").toFormatter(Locale.GERMAN);

	static final String AGENDA_URL = "https://www.danceorama.ch/friday-night.html";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		return updateEvents(doc);
	}

	EventUpdateCounter updateEvents(Document doc) {
		EventUpdateCounter result = new EventUpdateCounter();
		Element col2 = doc.selectFirst(".col_2");

		Elements elements = col2.select("strong");
		for (Element element : elements) {
			String text = element.text();
			int pos = text.indexOf(" 20");
			while (pos > -1) {
				createEvent(result, text.substring(0, pos + 5));

				text = text.substring(pos + 5).trim();
				pos = text.indexOf(" 20");
			}
		}
		return result;
	}

	private void createEvent(EventUpdateCounter result, String substring) {
		LocalDate date = LocalDate.parse(substring, FORMATTER);
		if (!date.isBefore(LocalDate.now())) {
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());
			DeeJay deeJay = getDeeJay("DJ Jüre Rüegsegger");

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;
			danceEvent.header = location.name;
			danceEvent.title = "Friday-Night";
			danceEvent.from = LocalTime.of(21, 0);
			danceEvent.description = "Die Tanzparty fuer alle die gerne tanzen. Auch wenn Du nicht bei uns in einem Kurs tanzt, bist Du herzlich willkommen! "
					+ "Die Musik ist aktuell und ideal auf Gesellschafts-Tanzpaare abgestimmt. Alle gängigen Tänze werden berücksichtigt. Zum Tanzen steht viel Platz zur Verfügung. "
					+ "Für das Wohl der Gäste sorgt das Bar-Team. "
					+ "Wir empfehlen Euch vorgänig einen Platz zu reservieren, damit Ihr während Euren Tanzpausen, sicher eine Sitzgelegenheit habt. Schreibt uns ein Email mit Name, Anzahl Personen, gewünschter Anlass und Datum an info@danceorama.ch.";

			danceEvent.deeJay = deeJay;
			danceEvent.price = new BigDecimal(15);
			danceEvent.location = location;

			save(danceEvent, result);
		}

	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Zentweg 26";
		location.city = "3006 Bern";
		location.name = "Danceorama";
		location.url = "https://www.danceorama.ch/";
		location.region.add(Region.BE);
		return location;
	}

}
