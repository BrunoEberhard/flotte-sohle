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

// Nach zu vielen Änderungen auf der Pasi Seite habe ich den Crawler
// vorläufig aufgegeben und einen Rule - Provider erstellt.
@Deprecated
public class PasadenaCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.pasadena.ch/agenda-news-club-dancing/";

	private static final String[] TITLES = { "Schlagerparty", "Schlagernacht", "Facebookparty" };

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		Elements elements = doc.select(".post-item");
		for (Element element : elements) {
			try {

				Element elementPost = element.selectFirst(".post-excerpt");
				String text = elementPost.text();

				LocalDate date = extractLocalDate(text);
				LocalTime[] period = extractPeriod(text);
				String djText = extractDj(text);
				BigDecimal price = extractPrice(text);

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

				Element elementTitle = element.selectFirst(".entry-title");

				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
				danceEvent.header = location.name;
				danceEvent.from = period[0];
				danceEvent.until = period[1];
				danceEvent.description = text;
				danceEvent.location = location;
				danceEvent.price = price;

				if (!StringUtils.isEmpty(djText)) {
					danceEvent.deeJay = getDeeJay(djText);
				} else {
					danceEvent.deeJay = null;
				}

				// Normalerweise den Titel verwenden
				danceEvent.line = elementTitle.text();
				if (StringUtils.equals(danceEvent.line, "Dancing Night", "Tanzabig", "Hit Dance Night")) {
					// Diese Titel sind nichtssagend, da muss nichts angezeigt werden
					danceEvent.line = null;
				} else if (!StringUtils.isEmpty(danceEvent.line)) {
					// bei diesen Titeln nur das Stichwort anzeigen
					for (String title : TITLES) {
						if (danceEvent.line.contains(title)) {
							danceEvent.line = title;
						}
					}
				}

				save(danceEvent, result);
			} catch (Exception x) {
				result.failedEvents++;
				result.exception = x.getMessage();
			}
		}
		return result;

	}

	// Dienstag 12. November 19:30 bis 24:00 mit DJ Rolf Eintritt (CHF): 10.00
	// (Member gratis)

	private LocalTime[] extractPeriod(String text) {
		int index = text.indexOf(" bis ");
		if (index > 5 && index < text.length() - 10) {
			return new LocalTime[] { LocalTime.parse(text.substring(index - 5, index), DateUtils.TIME_FORMAT), LocalTime.parse(text.substring(index + 5, index + 10), DateUtils.TIME_FORMAT) };
		}
		return null;
	}

	private LocalDate extractLocalDate(String text) {
		int index = text.indexOf(' ');
		index = text.indexOf(' ', index + 1);
		index = text.indexOf(' ', index + 1);
		text = text.substring(0, index);
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd. MMMM", Locale.GERMAN);
		MonthDay monthDay = MonthDay.parse(text, formatter);
		int year = LocalDate.now().getYear();
		if (monthDay.getMonth().getValue() < LocalDate.now().getMonth().getValue()) {
			year++;
		}
		return monthDay.atYear(year);
	}

	private String extractDj(String text) {
		int index = text.toUpperCase().indexOf("MIT ");
		int endIndex = text.toUpperCase().indexOf("EINTRITT");
		if (index > 0 && endIndex > 0) {
			return text.substring(index + 4, endIndex);
		}
		return null;
	}

	private BigDecimal extractPrice(String text) {
		int index = text.toUpperCase().indexOf("EINTRITT (CHF): ") + 16;
		int endIndex = text.indexOf(' ', index);
		if (index > 0 && endIndex > 0) {
			return new BigDecimal(text.substring(index, endIndex));
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