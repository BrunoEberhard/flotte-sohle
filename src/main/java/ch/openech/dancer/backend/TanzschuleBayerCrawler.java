package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.minimalj.repository.query.By;
import org.minimalj.util.DateUtils;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class TanzschuleBayerCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "http://www.tanzschule-bayer.at/tanzabende.html";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		for (Element td : doc.getElementsContainingOwnText("Allgemeiner Tanzabend")) {
			Element tr = td.parent();

			String dateString = tr.child(1).text();
			dateString = dateString.substring(dateString.indexOf(' ') + 1);
			LocalDate date = DateUtils.parse(dateString);

			String timeString = tr.child(2).text();
			String[] fromUntil = timeString.split(" - ");

			LocalTime from = LocalTime.parse(fromUntil[0]);
			LocalTime until = StringUtils.equals(fromUntil[1], "24:00") ? LocalTime.of(0, 0) : LocalTime.parse(fromUntil[1]);

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
			danceEvent.header = location.name;
			danceEvent.title = tr.child(0).text();
			danceEvent.from = from;
			danceEvent.until = until;
			danceEvent.location = location;
			save(danceEvent, result);
		}
		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Österreich";
		location.address = "Untere Bahnhofstrasse 10";
		location.city = "6830 Rankweil";
		location.name = "Tanzschule Bayer";
		location.url = "http://www.tanzschule-bayer.at/";
		location.region.add(Region.SG);
		return location;
	}

}