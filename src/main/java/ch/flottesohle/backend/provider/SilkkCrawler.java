package ch.flottesohle.backend.provider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.minimalj.repository.query.By;
import org.minimalj.util.StringUtils;

import ch.flottesohle.backend.DanceEventProvider;
import ch.flottesohle.backend.EventUpdateCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;
import ch.flottesohle.model.Region;

public class SilkkCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.silkk.ch/events";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();
		
		for (Element eventRow : doc.select(".eventlist .item .event-table .event-row")) {
			Element dateElement = eventRow.selectFirst(".date");
			if (dateElement == null) {
				continue;
			}
			
			Element dayElement = dateElement.selectFirst(".day");
			Element monthElement = dateElement.selectFirst(".month");
			Element yearElement = dateElement.selectFirst(".year");

			LocalDate date = parseDate(dayElement.text(), monthElement.text(), yearElement.text()); 

			Element eventElement = eventRow.selectFirst("h3");
			Element infoElement = eventRow.selectFirst(".data h4");
			
			Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

			DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
			if (danceEvent.status == EventStatus.edited) {
				result.skippedEditedEvents++;
				continue;
			} else if (danceEvent.status == EventStatus.blocked) {
				result.skippedBlockedEvents++;
				continue;
			}

			String eventString = eventElement.text();
			int indexDj = eventString.indexOf("mit ");
			if (indexDj > 0) {
				eventString = eventString.substring(0, indexDj).trim();
			}
			if ("TANZerei! die Dienstagstanzparty".equals(eventString)) {
				eventString = "TANZerei!";
			}
			
			LocalTime from = null;
			String infoString = infoElement.text();
			int indexAb = infoString.indexOf("ab");
			if (indexAb > 0) {
				infoString = infoString.substring(indexAb + 3).trim();
				try {
					from = LocalTime.parse(infoString);
				} catch (DateTimeParseException x) {
					from = LocalTime.of(20, 30);
				}
			}
			
			if (eventString.contains("Ü40") || eventString.toUpperCase().contains("SALSA")) {
				danceEvent.status = EventStatus.blocked;
			} else {
				danceEvent.status = EventStatus.generated;
			}
			danceEvent.date = date;
			danceEvent.line = eventString;
			danceEvent.from = from;
			danceEvent.location = location;
//			danceEvent.deeJay = getDeeJay(dj);
			save(danceEvent, result);
		}
		return result;
	}
	
	static LocalDate parseDate(String day, String monthMMM, String year) {
		monthMMM = monthMMM.toUpperCase();
		int month = 0;
		if (StringUtils.equals(monthMMM, "JAN")) month = 1;
		else if (StringUtils.equals(monthMMM, "FEB")) month = 2;
		else if (StringUtils.equals(monthMMM, "MRZ", "MÄR", "MAR")) month = 3;
		else if (StringUtils.equals(monthMMM, "APR")) month = 4;
		else if (StringUtils.equals(monthMMM, "MAI")) month = 5;
		else if (StringUtils.equals(monthMMM, "JUN")) month = 6;
		else if (StringUtils.equals(monthMMM, "JUL")) month = 7;
		else if (StringUtils.equals(monthMMM, "AUG")) month = 8;
		else if (StringUtils.equals(monthMMM, "SEP")) month = 9;
		else if (StringUtils.equals(monthMMM, "OKT")) month = 10;
		else if (StringUtils.equals(monthMMM, "NOV")) month = 11;
		else if (StringUtils.equals(monthMMM, "DEZ")) month = 12;

		if (year.length() == 2) {
			year = Year.now().toString().substring(0, 2) + year;
		}
		return LocalDate.of(Integer.parseInt(year), month, Integer.parseInt(day));
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Zürcherstrasse 49";
		location.city = "8620 Wetzikon";
		location.name = "Silkk";
		location.url = "https://www.silkk.ch/";
		location.region.add(Region.ZH);
		return location;
	}

}