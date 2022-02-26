package ch.openech.flottesohle.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class ChesselhuusCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.chesselhuus.ch/ticket-bestellen/";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		Element listEvents = doc.selectFirst("ul.ee-list-events");
		Elements events = listEvents.select("li");
		events.forEach(event -> {
			try {
				Element title = event.selectFirst("h3");
				if (title.text().contains("Paar-Tanz")) {
					Element time = event.selectFirst("time");
					long seconds = Long.parseLong(time.attr("datetime"));
					LocalDate date = Instant.ofEpochMilli(seconds * 1000).atZone(ZoneId.systemDefault()).toLocalDate();

					Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

					DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
					danceEvent.line = "Dance Night";
					danceEvent.from = LocalTime.of(20, 0);
					danceEvent.until = LocalTime.of(23, 55);
					danceEvent.status = EventStatus.generated;
					danceEvent.date = date;
					danceEvent.location = location;
					danceEvent.price = BigDecimal.valueOf(12);
					danceEvent.tags.add(EventTag.Taxidancer);
					danceEvent.description = "DANCE NIGHT mit Dicoswing sowie Salsa, Bachata & vielem mehr. "
							+ "Für alle Single Ladys haben wir ebenfalls auch die Taxi-Dancers mit von der Partie im Chesselhuus. "
							+ "Falls Du für diesen Dance Event noch Tanzschuhe brauchst.... Kein Problem. Direkt daneben ist Dancingqueens.ch.";

					save(danceEvent, result);
				}
			} catch (Exception x) {
				result.failedEvents++;
			}
		});
		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Tumbelenstrasse 12";
		location.city = "8330 Pfäffikon ZH";
		location.region.add(Region.ZH);
		location.name = "Chesselhuus";
		location.url = "https://www.chesselhuus.ch/";
		return location;
	}

}