package ch.openech.dancer.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;
import org.minimalj.util.DateUtils;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class PasadenaCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "http://www.pasadena.ch/agendanews/";

	private static final String[] TITLES = { "Schlagerparty", "Schlagernacht", "Facebookparty" };

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		Element c31 = doc.selectFirst("#c31");
		Elements elements = c31.select("p[style]");
		for (Element element : elements) {
			if (!isSimpleElement(element)) {
				LocalDate date = extractLocalDate(element);
				LocalTime[] period = extractPeriod(element);
				if (DanceEvent.isDuringTheDay(period[0]))
					continue;

				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

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
				danceEvent.title = extractDanceEventTitle(element);
				danceEvent.from = period[0];
				danceEvent.until = period[1];
				danceEvent.description = element.nextElementSibling().text();
				danceEvent.location = location;

				Elements mitDj = element.nextElementSibling().getElementsContainingOwnText("Mit DJ");
				if (!mitDj.isEmpty()) {
					String djText = mitDj.get(0).ownText().substring(4);
					danceEvent.deeJay = getDeeJay(djText);
				} else {
					danceEvent.deeJay = null;
				}

				// Normalerweise den Titel verwenden
				danceEvent.line = danceEvent.title;
				if (StringUtils.equals(danceEvent.title, "Dancing Night", "Tanzabig", "Hit Dance Night")) {
					// Diese Titel sind nichtssagend, da muss nichts angezeigt werden
					danceEvent.line = null;
				} else if (!StringUtils.isEmpty(danceEvent.title)) {
					// bei diesen Titeln nur das Stichwort anzeigen
					for (String title : TITLES) {
						if (danceEvent.title.contains(title)) {
							danceEvent.line = title;
						}
					}
				}

				save(danceEvent, result);
			}
		}
		return result;
	}

	private boolean isSimpleElement(Element element) {
		return element.nextElementSibling() == null || element.childNodeSize() != 3;
	}

	private LocalTime[] extractPeriod(Element element) {
		if (element.childNodeSize() == 3) {
			String period = element.childNode(2).toString();
			if (period != null) {
				String[] moments = period.split("bis");
				if (moments.length == 2) {
					return new LocalTime[] { LocalTime.parse(moments[0].trim(), DateUtils.TIME_FORMAT), LocalTime.parse(moments[1].trim(), DateUtils.TIME_FORMAT) };
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
		location.latitude = BigDecimal.valueOf(47.3814611);
		location.longitude = BigDecimal.valueOf(8.6801448);
		// location.osm = 1682227;
		location.city = "8604 Volketswil";
		location.name = "Pasadena";
		location.url = "http://www.pasadena.ch";
		location.region.add(Region.ZH);
		return location;
	}

}