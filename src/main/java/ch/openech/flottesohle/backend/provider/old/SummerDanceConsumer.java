package ch.openech.flottesohle.backend.provider.old;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonReader;
import org.minimalj.util.StringUtils;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;

public class SummerDanceConsumer extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL_1 = "https://inffuse.eventscalendar.co/js/v0.1/calendar/data?cacheKiller=";
	private static final String AGENDA_URL_2 = "&compId=comp-jrz3l3rd&currency=CHF&deviceType=desktop&instance=GTH_JMuDfD4CCWrU1B9AMwssyw0ZN-r9WtscF84yGoY.eyJpbnN0YW5jZUlkIjoiMTNjNjIyZmYtNWZjMS00ZjgwLTgxYzMtNTRhYjYwNGYzM2UwIiwiYXBwRGVmSWQiOiIxMzNiYjExZS1iM2RiLTdlM2ItNDliYy04YWExNmFmNzJjYWMiLCJzaWduRGF0ZSI6IjIwMTktMDYtMTRUMDU6MTY6MzEuNzM3WiIsInVpZCI6bnVsbCwiaXBBbmRQb3J0IjoiODAuMjE4LjIyMi45Ni81ODQwNiIsInZlbmRvclByb2R1Y3RJZCI6InByZW1pdW0iLCJkZW1vTW9kZSI6ZmFsc2UsImFpZCI6IjYzY2M1Y2I3LTJiMjEtNDkyNS1hZDA3LTE5YWI1ZDA5NDNkNyIsInNpdGVPd25lcklkIjoiMThhNTA3ZTQtYWNkNS00MWY3LTg4N2YtYjVkOGIyNjE3YTBjIn0&locale=de&pageId=cee5&siteRevision=56&tz=Europe%2FZurich&viewMode=site&_referrer=";

	@Override
	public EventUpdateCounter updateEvents() throws Exception {
		EventUpdateCounter result = new EventUpdateCounter();

		String urlString = AGENDA_URL_1 + (System.currentTimeMillis() / 1000) + AGENDA_URL_2;


		URL url = new URL(urlString);
		HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		if (responseCode == HttpsURLConnection.HTTP_OK) {

			// opens input stream from the HTTP connection
			try (InputStream inputStream = httpConn.getInputStream()) {

				@SuppressWarnings("unchecked")
				Map<String, Object> struct0 = (Map<String, Object>) JsonReader.read(inputStream);
				Map<String, Object> projectNode = (Map<String, Object>) struct0.get("project");
				Map<String, Object> dataNode = (Map<String, Object>) projectNode.get("data");
				List<Map<String, Object>> events = (List<Map<String, Object>>) dataNode.get("events");

				Map<String, Object> settingsNode = (Map<String, Object>) dataNode.get("settings");
				String timezoneString = (String) settingsNode.get("timezone");

				ZoneId zoneId = ZoneId.of(timezoneString);

				for (Map<String, Object> event : events) {
					long startLong = Long.valueOf((String) event.get("start"));
					LocalDate date = Instant.ofEpochMilli(startLong).atZone(zoneId).toLocalDate();

					if (date.isBefore(LocalDate.now())) {
						continue;
					}
					
					String idString = (String) event.get("id");
					if (idString == null) {
						continue;
					}
					idString = idString.substring("event_".length());
					byte[] idBytes = Base64.getDecoder().decode(idString);
					String id = UUID.nameUUIDFromBytes(idBytes).toString();

					DanceEvent danceEvent = Backend.read(DanceEvent.class, id);
					boolean newEvent = danceEvent == null;
					if (newEvent) {
						danceEvent = new DanceEvent();
						danceEvent.id = id;
						danceEvent.status = EventStatus.generated;
					} else if (danceEvent.status == EventStatus.blocked || danceEvent.status == EventStatus.edited) {
						continue;
					}

					String locationName = (String) event.get("location");

					danceEvent.date = date;
					// hier ist anders als bei anderen Locations, da Summerdance an verschiedenen Orten stattfindet
					danceEvent.line = this.location.name;
					danceEvent.description = (String) event.get("description");
					danceEvent.price = BigDecimal.valueOf(10);
					danceEvent.tags.add(EventTag.Outdoor);

					danceEvent.from = getTime(event, "start");
					danceEvent.until = getTime(event, "end");

					danceEvent.location = this.location;

					String locationUrl = null;
					if (event.containsKey("links")) {
						Map<String, Object> links = (Map<String, Object>) event.get("links");
						if (links != null) {
							Map<String, Object> link = (Map<String, Object>) links.get("0");
							if (link != null) {
								locationUrl = (String) link.get("url");
							}
						}
					}
					if (locationUrl != null) {
						danceEvent.description = danceEvent.description + "<br><a target=\"_top\" href=\"" + locationUrl + "\">" + locationUrl + "</a>";
					}

					danceEvent.line = !StringUtils.isBlank(locationName) ? locationName : "Tanzen unter freiem Himmel";

					try {
						if (newEvent) {
							Backend.insert(danceEvent);
							result.newEvents++;
						} else {
//							DanceEvent existing = Backend.read(DanceEvent.class, danceEvent.id);
//							if (!EqualsHelper.equals(existing, danceEvent)) {
								Backend.save(danceEvent);
								result.updatedEvents++;
//							}
						}
					} catch (Exception x) {
						result.failedEvents++;
					}
				}
			}
		}
		httpConn.disconnect();

		return result;
	}

	private LocalTime getTime(Map<String, Object> data, String prefix) {
		Long hour = (Long) data.get(prefix + "Hour");
		Long minute = (Long) data.get(prefix + "Minutes");
		return LocalTime.of(hour.intValue(), minute.intValue());
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.name = "Summer Dance";
		location.url = "https://www.summerdance.ch/";
		return location;
	}

}