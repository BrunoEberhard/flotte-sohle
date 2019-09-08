package ch.openech.dancer.backend.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;
import org.minimalj.util.DateUtils;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class BadenerTanzCenterCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://badenertanzcentrum.ch/events/tanz-partys/?art=2";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		Element tableElement = doc.selectFirst(".parties");
		Element tableHaderElement = tableElement.selectFirst("thead");
		Element tableHaderRowElement = tableHaderElement.selectFirst("tr");
		List<String> headerNames = getHeaderNames(tableHaderRowElement);

		int dateIndex = headerNames.indexOf("Datum");
		int djIndex = headerNames.indexOf("DJ");
		int timeIndex = headerNames.indexOf("Uhrzeit");
		int eventIndex = headerNames.indexOf("Event");

		for (Element tr : tableElement.select("tr")) {
			Elements td = tr.select("td");
			if (td.size() == 0) {
				continue;
			}
			LocalDate date = DateUtils.parse(td.get(dateIndex).text());
			String dj = td.get(djIndex).text();
			LocalTime time = LocalTime.parse(td.get(timeIndex).text().substring(0, 5));
			String event = td.get(eventIndex).text();

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
			danceEvent.header = "BTC Baden";
			danceEvent.title = event;
			danceEvent.from = time;
			danceEvent.location = location;
			danceEvent.deeJay = getDeeJay(dj);
			save(danceEvent, result);
		}
		return result;
	}

	private List<String> getHeaderNames(Element tableHaderRowElement) {
		List<String> names = new ArrayList<>();
		for (Element th : tableHaderRowElement.select("th")) {
			names.add(th.text());
		}
		return names;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Martinsbergstrasse 38";
		location.city = "5400 Baden";
		location.name = "BTC Badener Tanzcentrum";
		location.url = "https://badenertanzcentrum.ch/";
		location.region.add(Region.AG);
		return location;
	}

}