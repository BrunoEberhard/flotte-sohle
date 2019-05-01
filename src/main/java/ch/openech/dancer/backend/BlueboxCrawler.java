package ch.openech.dancer.backend;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonReader;
import org.minimalj.repository.query.By;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class BlueboxCrawler extends DanceEventCrawler {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.blue-box.ch/wp-admin/admin-ajax.php?action=wcs_get_events_json&content%5Bwcs_type%5D%5B%5D=42&";

	@Override
	public int crawlEvents() {
		LocalDate startEvents = LocalDate.now();
		LocalDate endEvents = startEvents.plusMonths(3);
		// start=2019-04-12&end=2019-05-09
		String urlString = AGENDA_URL + "start=" + startEvents.toString() + "&end=" + endEvents.toString();

		try {
			URL url = new URL(urlString);
			HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
			int responseCode = httpConn.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {

				// opens input stream from the HTTP connection
				try (InputStream inputStream = httpConn.getInputStream()) {

					@SuppressWarnings("unchecked")
					List<Map<String, Object>> events = (List<Map<String, Object>>) JsonReader.read(inputStream);
					for (Map<String, Object> event : events) {

						String titleComplete = (String) event.get("title");
						LocalDateTime start = LocalDateTime.parse(((String) event.get("start")).substring(0, 19));
						LocalDateTime end = LocalDateTime.parse(((String) event.get("end")).substring(0, 19));

						int index = titleComplete.indexOf(" mit");
						String title = index > 0 ? titleComplete.substring(0, index) : titleComplete;

						Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class,
								By.field(DanceEvent.$.location, location)
										.and(By.field(DanceEvent.$.date, start.toLocalDate()))
										.and(By.field(DanceEvent.$.title, title)));

						DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
						if (danceEvent.status == EventStatus.edited)
							continue;

						danceEvent.header = "BlueBox";
						danceEvent.title = title;
						if (!StringUtils.equals(title, "Disco Tanznacht", "Party Tanznacht")) {
							danceEvent.line = title;
						}
						String excerpt = (String) event.get("excerpt");
						index = excerpt.indexOf("</p>");
						if (index > 0) {
							excerpt = excerpt.substring(3, index);
							danceEvent.description = excerpt;
						}

						danceEvent.from = start.toLocalTime();
						danceEvent.until = end.toLocalTime();
						danceEvent.status = EventStatus.generated;
						danceEvent.date = start.toLocalDate();
						danceEvent.location = location;

						if (titleComplete.contains("Partydancers")) {
							danceEvent.tags.add(EventTag.Taxidancer);
						}

						Backend.save(danceEvent);
					}
				}
			}
			httpConn.disconnect();

			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Im Fennen 11";
		location.city = "8867 Niederurnen";
		location.region.add(Region.GR);
		location.name = "BlueBox Disco";
		location.url = "https://www.blue-box.ch/";
		return location;
	}

}