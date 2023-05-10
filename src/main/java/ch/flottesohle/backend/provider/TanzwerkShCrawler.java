package ch.flottesohle.backend.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.DeeJay;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;

public class TanzwerkShCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL_1 = "https://www.tanzzentrum-sh.ch/index.php?option=com_icagenda&view=list&Itemid=1932";
	private static final String AGENDA_URL_2 = "https://www.tanzzentrum-sh.ch/index.php?option=com_icagenda&view=list&Itemid=1933";
	private static final String AGENDA_URL_3 = "https://www.tanzzentrum-sh.ch/index.php?option=com_icagenda&view=list&Itemid=553";
	private static final String AGENDA_URL_4 = "https://www.tanzzentrum-sh.ch/index.php?option=com_icagenda&view=list&Itemid=1801";
	private static final String AGENDA_URL_5 = "https://www.tanzzentrum-sh.ch/index.php?option=com_icagenda&view=list&Itemid=552";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		updateEvents(AGENDA_URL_1, result);
		updateEvents(AGENDA_URL_2, result);
		updateEvents(AGENDA_URL_3, result);
		updateEvents(AGENDA_URL_4, result);
		updateEvents(AGENDA_URL_5, result);
		return result;
	}

	private void updateEvents(String url, EventUpdateCounter result) throws IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();

		DateTimeFormatter germanFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.GERMAN);
		DeeJay deeJayMany = Backend.find(DeeJay.class, By.field(DeeJay.$.name, "DJ Many")).get(0);

		for (Element event : doc.select(".event")) {

			Element nextdate = event.selectFirst(".nextdate");
			String datestring = nextdate.select(".ic-single-next").text();
			LocalDate date = LocalDate.parse(datestring, germanFormatter);

			Element starttime = nextdate.selectFirst(".ic-single-starttime");
			LocalTime time = LocalTime.parse(starttime.text());
			if (time.isBefore(LocalTime.of(17, 0))) {
				continue;
			}

			String description = event.select(".descshort").text();

			String title = event.selectFirst("h2").text();

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
			
			danceEvent.line = title;
			danceEvent.description = description;
			danceEvent.from = time;
			danceEvent.location = location;
			danceEvent.price = BigDecimal.valueOf(8);
			if (description.contains("DJ Many")) {
				danceEvent.deeJay = deeJayMany;
			}
			save(danceEvent, result);
		}
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Rheinweg 6";
		location.city = "8200 Schaffhausen";
		location.name = "Tanzzentrum SH";
		location.url = "https://www.tanzzentrum-sh.ch/";
		return location;
	}

}
