package ch.openech.flottesohle.backend.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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

public class DanceInnCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.danceinn.ch/events";

	private static enum SAAL {
		MAIN, Schloss
	};

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		Element e = doc.getElementById("events");
		Elements events = e.getElementsByClass("event");
		events.forEach(element -> {
			LocalDate date = null;
			SAAL saal = null;
			String description = null;
			boolean geschlossen = !element.getElementsContainingText("geschlossen").isEmpty();

			if (!element.getElementsContainingText("ü40").isEmpty()) return;
			if (!element.getElementsContainingText("Coast").isEmpty()) return;
			if (!element.getElementsContainingText("Nachmittag").isEmpty()) return;

			Element dayElement = element.selectFirst(".day");
			Element monthElement = element.selectFirst(".month");
			Element yearElement = element.selectFirst(".year");
			if (dayElement == null || monthElement == null || yearElement == null) return;
			
			String day = dayElement.text();
			String monthName = monthElement.text();
			String year = yearElement.text();
			if (year.length() < 4) {
				year = "20" + year;
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.GERMAN);
			date = LocalDate.parse(day + " " + monthName + " " + year, formatter);
			
			saal = element.getElementsContainingText("Schlosshof").isEmpty() ? SAAL.MAIN : SAAL.Schloss;

			LocalTime from = LocalTime.of(20, 0);
			Elements abElements = element.getElementsContainingText("ab");
			for (Element abElement : abElements) {
				String abElementText = abElement.text();
				if (abElementText.length() == 8) {
					from = LocalTime.parse(abElementText.substring(3));
				}
			}
			
			String line = null;
			Element headerElement = element.selectFirst("h2");
			if (headerElement != null) {
				line = headerElement.text();
			}
			
			if (date != null && saal != null && !(line != null && line.toLowerCase().contains("salsa"))) {
				String header = saal == SAAL.MAIN ? "Dance Inn" : "Schlosshof";

				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
						By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				if (danceEvent.status == EventStatus.edited) {
					result.skippedEditedEvents++;
				} else if (danceEvent.status == EventStatus.blocked) {
					result.skippedBlockedEvents++;
				} else {
					danceEvent.description = description;
					danceEvent.line = line;

					danceEvent.from = from;
					danceEvent.until = null;
					danceEvent.status = geschlossen ? EventStatus.blocked : EventStatus.generated;
					danceEvent.date = date;
					danceEvent.location = location;

					save(danceEvent, result);
				}
			}
		});
//		handleWerk1InDanceInn();
		return result;
	}

//	public static void handleWerk1InDanceInn() {
//		Optional<Location> werk1 = findOne(Location.class, new FieldCriteria(Location.$.name, "Werk 1"));
//		Optional<Location> danceInn = findOne(Location.class, new FieldCriteria(Location.$.name, "Dance Inn"));
//		if (!werk1.isPresent() || !danceInn.isPresent()) {
//			return;
//		}
//
//		List<DanceEvent> danceInnEvents = Backend.find(DanceEvent.class, By.field(DanceEvent.$.location, danceInn.get()));
//		for (DanceEvent i : danceInnEvents) {
//			if (i.description.contains("Werk 1")) {
//				Optional<DanceEvent> werkEvent = findOne(DanceEvent.class, By.field(DanceEvent.$.location, werk1.get()).and(By.field(DanceEvent.$.date, i.date)));
//				if (werkEvent.isPresent()) {
//					werkEvent.get().status = EventStatus.blocked;
//					Backend.save(werkEvent.get());
//				}
//			}
//		}
//	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Murgtalstrasse 20";
		location.city = "9542 Münchwilen TG";
		location.region.add(Region.ZH);
		location.region.add(Region.SG);
		location.name = "Dance Inn";
		location.url = "http://www.danceinn.ch/";
		return location;
	}

}