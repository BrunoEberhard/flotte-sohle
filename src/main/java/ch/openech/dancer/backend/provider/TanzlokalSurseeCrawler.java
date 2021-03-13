package ch.openech.dancer.backend.provider;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
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
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class TanzlokalSurseeCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.tanzlokal-sursee.ch/";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		Elements events = doc.select(".cc-m-textwithimage-inline-rte");
		events.forEach(element -> {
			Element titleElement = element.getElementsByTag("p").get(0);
			String text = titleElement.text();
			boolean startsWithWeekday = false;
			for (DayOfWeek d : DayOfWeek.values()) {
				String day = d.getDisplayName(TextStyle.FULL, Locale.GERMAN);
				if (text.startsWith(day)) {
					startsWithWeekday = true;
					break;
				}
			}
			if (startsWithWeekday && !text.contains("Ball") && !text.contains("13.30")) {
				int index = text.indexOf(' ');
				LocalDate date = DateUtils.parse(text.substring(index + 1, index + 9));

				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());

				
				danceEvent.from = LocalTime.of(20, 0);
				danceEvent.until = text.contains("23.00") ? LocalTime.of(23, 0) : LocalTime.of(0, 0);
				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
				if (!text.startsWith("Freitag")) {
					danceEvent.tags.add(EventTag.Workshop);
				}
				danceEvent.location = location;
				save(danceEvent, result);
			}
		});
		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Bettenweg 12";
		location.city = "6233 Büron";
		location.region.add(Region.LU);
		location.name = "Tanzlokal Sursee";
		location.url = "https://www.tanzlokal-sursee.ch/";
		return location;
	}

}