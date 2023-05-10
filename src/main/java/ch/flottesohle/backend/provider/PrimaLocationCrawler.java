package ch.flottesohle.backend.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
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

public class PrimaLocationCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://prima-location.ch/tanzen/tanzveranstaltungen/";
	
	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		Elements titles = doc.select("h3:contains(Tanzabend)");
		titles.forEach(title -> {
			try {
				Element container = title.parent().parent().parent();

				// Ok, das funktioniert in 9 Jahren nicht mehr
				Element datum = container.selectFirst("p:contains( 202)");
				String datumText = datum.text();
				int posYear = datumText.indexOf(" 202");
				int posDatum = posYear - 1;
				while (posDatum > -1 && !Character.isDigit(datumText.charAt(posDatum))) {
					posDatum -= 1;
				}
				while (posDatum > -1 && Character.isDigit(datumText.charAt(posDatum))) {
					posDatum -= 1;
				}
				String datumString = datum.text().substring(posDatum + 1, posYear + 5);
				LocalDate date = LocalDate.parse(datumString, LONG_DATE_FORMAT);
				
				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
				danceEvent.from = LocalTime.of(20, 0);
				danceEvent.until = LocalTime.of(0, 0);
				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
				danceEvent.location = location;
				
				String text = title.ownText();
				int posTanzabend = text.indexOf("Tanzabend");
				if (posTanzabend > -1) {
					text = text.substring(posTanzabend);
				}
				danceEvent.line = text;
				
				save(danceEvent, result);
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
		location.address = "Industriestrasse 12";
		location.city = "6233 Büron";
		location.region.add(Region.LU);
		location.name = "Prima Location";
		location.url = "https://prima-location.ch/";
		return location;
	}

}