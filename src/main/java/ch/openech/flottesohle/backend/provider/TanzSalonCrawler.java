package ch.openech.flottesohle.backend.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;
import org.minimalj.util.DateUtils;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class TanzSalonCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://tanzsalon.ch/events/";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		Element tableDivElement = doc.selectFirst(".css-events-list");
		Element tableElement = doc.selectFirst(".css-events-list > table");

		for (Element tr : tableElement.select("tr")) {
			Elements td = tr.select("td");
			if (td.size() < 4) {
				continue;
			}

			String title = td.get(0).text();
			String dateString = td.get(2).text();
			String timeString = td.get(3).text();

			if (title.toLowerCase().contains("behinderten") || dateString.contains("-")) {
				continue;
			}
			if (!(title.toLowerCase().contains("tanzabend") || title.contains("tanzfest"))) {
				continue;
			}

			if (!dateString.endsWith(".")) {
				dateString += ".";
			}
			dateString += Year.now().getValue();
			LocalDate date = DateUtils.parse(dateString);
			// Es wird angenommen, dass im Dezember schon die Daten vom nächsten Jahr
			// online sind. Das sollte aber kontrolliert werden!
			if (date.compareTo(LocalDate.now().minusMonths(11)) < 0) {
				date = date.plusYears(1);
			}

			LocalTime time = LocalTime.parse(timeString.substring(0, 5));
			LocalTime until = null;
			if (timeString.length() >= 13) {
				until = LocalTime.parse(timeString.substring(8, 13));
			}

			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.status == EventStatus.edited) {
				result.skippedEditedEvents++;
				continue;
			} else if (danceEvent.status == EventStatus.blocked) {
				result.skippedBlockedEvents++;
				continue;
			}

			danceEvent.status = EventStatus.generated;
			danceEvent.date = date;
			
			danceEvent.line = "Tanzabend";
			if (title.contains("Sommertanzfest")) {
				danceEvent.line = "Sommertanzfest";
			}
			if (title.contains("Adventstanzabend")) {
				danceEvent.line = "Adventstanzabend";
			}
			danceEvent.from = time;
			danceEvent.until = until;
			danceEvent.location = location;
			save(danceEvent, result);
		}
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