package ch.openech.flottesohle.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class TanzSalonCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.tanzsalon.ch/tanzabende";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		Element row = doc.selectFirst("div[role=\"row\"]");
		Elements titles = row.select("span:containsOwn(anzabend)");
		
		titles.forEach(title -> {
			try {
				Element container = title;
				for (int i = 0; i < 7; i++) {
					container = container.parent();
				}

				// Ok, das funktioniert in 9 Jahren nicht mehr
				Element datum = container.selectFirst("span:contains( 202)");
				String datumText = datum.text().trim();
				LocalDate date = LocalDate.parse(datumText, LONG_DATE_FORMAT);
				
				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
				danceEvent.from = LocalTime.of(20, 0);
				danceEvent.until = LocalTime.of(23, 30);
				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
				danceEvent.location = location;
				danceEvent.price = BigDecimal.valueOf(35);
				danceEvent.description = "Eintritt inkl. Getränke mit/ohne Alkohol & Naschereien.";
				
				danceEvent.line = title.ownText().trim();
				
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
		location.address = "Spinnereistrasse 29";
		location.city = "8640 Rapperswil-Jona";
		location.name = "Tanzsalon";
		location.url = "https://tanzsalon.ch/";
		location.region.add(Region.SG);
		location.region.add(Region.ZH);
		return location;
	}

}