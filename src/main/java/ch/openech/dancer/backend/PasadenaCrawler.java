package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.util.DateUtils;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;

public class PasadenaCrawler extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "http://www.pasadena.ch/agendanews/";

	@Override
	public int crawlEvents() {
		try {
			Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
			Elements elements = doc.select("p[style]");
			elements.forEach(element -> {
				if (!isSimpleElement(element)) {
					LocalDate date = extractLocalDate(element);
					Pair<LocalTime, LocalTime> period = extractPeriod(element);
					boolean duringTheDay = DanceEvent.isDuringTheDay(period.getValue1());

					Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
							By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date))
									.and(By.field(DanceEvent.$.getDuringTheDay(), duringTheDay)));

					DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

					danceEvent.status = EventStatus.published;
					danceEvent.date = date;
					danceEvent.title = extractDanceEventTitle(element);
					danceEvent.from = period.getValue0();
					danceEvent.until = period.getValue1();
					danceEvent.description = element.nextElementSibling().text();
					// danceEvent.organizer = organizer;
					danceEvent.location = location;

					Backend.save(danceEvent);
				}
			});
			return elements.size();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private boolean isSimpleElement(Element element) {
		return element.nextElementSibling() == null || element.childNodeSize() != 3;
	}

	private Pair<LocalTime, LocalTime> extractPeriod(Element element) {
		if (element.childNodeSize() == 3) {
			String period = element.childNode(2).toString();
			if (period != null) {
				String[] moments = period.split("bis");
				if (moments.length == 2) {
					return new Pair<>(LocalTime.parse(moments[0].trim(), DateUtils.TIME_FORMAT),
							LocalTime.parse(moments[1].trim(), DateUtils.TIME_FORMAT));
				}
			}
		}
		return null;
	}

	private LocalDate extractLocalDate(Element element) {
		Element dateTag = element.child(0);
		if (dateTag != null) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd. MMMM", Locale.GERMAN);
			final int currentYear = LocalDate.now().getYear();
			return MonthDay.parse(dateTag.ownText(), formatter).atYear(currentYear);
		}
		return null;
	}

	private String extractDanceEventTitle(Element paragraphElement) {
		Elements elements = paragraphElement.nextElementSibling().getElementsByTag("h3");
		if (elements != null && !elements.isEmpty()) {
			return elements.get(0).childNode(0).attr("title");
		}
		return null;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Chriesbaumstrasse 2";
		location.city = "8604 Volketswil";
		location.name = "Pasadena";
		location.url = "http://www.pasadena.ch";
		return location;
	}

}