package ch.openech.dancer.backend.provider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneOffsetTransition;
import java.util.Collection;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonReader;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.EventTag;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class RyvaCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.ecken.ch/_api/wix-one-events-server/html/widget-data?instance=dqvXR_J07Mjjv451Yg_dthBYVPTllpmBBkhlb1-mSYQ.eyJpbnN0YW5jZUlkIjoiYTcxMTE2MDEtZGM2Ni00NTFjLWEzYzItZTRjZmE4YWIzZDdmIiwiYXBwRGVmSWQiOiIxNDA2MDNhZC1hZjhkLTg0YTUtMmM4MC1hMGY2MGNiNDczNTEiLCJtZXRhU2l0ZUlkIjoiYmE4NmUzYjctMjFlYi00ZDQ1LTg0YTctZDYzOWNiODhhM2NkIiwic2lnbkRhdGUiOiIyMDE5LTEwLTI5VDE2OjEyOjQ0LjQzMloiLCJ1aWQiOm51bGwsInBlcm1pc3Npb25zIjpudWxsLCJpcEFuZFBvcnQiOiI5My4yMjUuMjU0LjE3Mi8yNjQzMCIsInZlbmRvclByb2R1Y3RJZCI6bnVsbCwiZGVtb01vZGUiOmZhbHNlLCJhaWQiOiJjYzBiYjdkNi02NTkzLTQ4OWQtODFmZi1lMTY5OGVhYzAzNmMiLCJiaVRva2VuIjoiMWQ5N2Y1YjYtZmQ4ZC0wODU5LTI3NjUtMzJmNjYzMjM5ZWIyIiwic2l0ZU93bmVySWQiOiIwMDRhNWUxOS02ZjM3LTQ0MjgtODg0Zi1kZGM3ZDdhMTI3YTMifQ"
			+ "&compId=comp-jcs3jzsw&locale=de&viewMode=site&members=false";

	@SuppressWarnings("unchecked")
	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).ignoreContentType(true).get();
		String s = doc.toString();

		String startString = "window.appData = JSON.parse(htmlDecode('";
		int index1 = s.indexOf(startString);
		int index2 = s.indexOf("'", index1 + startString.length() + 1);
		s = s.substring(index1 + startString.length(), index2);
		s = Jsoup.parse(s).text();
		s = s.replace("\\\\", "\\"); // warum? Es scheint wirklich, als ob die Daten schon so geliefert werden.

		Map<String, Object> d = (Map<String, Object>) JsonReader.read(s);
		Map<String, Object> component = (Map<String, Object>) d.get("component");
		Collection<Object> events = (Collection<Object>) component.get("events");

		for (Object e : events) {
			Map<String, Object> event = (Map<String, Object>) e;

			String id = (String) event.get("id");

			Map<String, Object> scheduling = (Map<String, Object>) event.get("scheduling");
			Map<String, Object> config = (Map<String, Object>) scheduling.get("config");

			LocalDateTime start = LocalDateTime.parse(((String) config.get("startDate")).substring(0, 19));
			LocalDateTime end = LocalDateTime.parse(((String) config.get("endDate")).substring(0, 19));

			String timeZoneString = (String) config.get("timeZoneId");
			ZoneId zoneId = ZoneId.of(timeZoneString);
			ZoneOffset offset = zoneId.getRules().getOffset(start);
			start = ZoneOffsetTransition.of(start, ZoneOffset.UTC, offset).getDateTimeAfter();
			end = ZoneOffsetTransition.of(end, ZoneOffset.UTC, offset).getDateTimeAfter();

			String title = (String) event.get("title");
			String description = (String) event.get("description");

			if (title.contains("Bachata") || title.contains("Salsa") || title.contains("Latin")) {
				continue;
			}

			boolean insert = true;
			DanceEvent danceEvent = Backend.read(DanceEvent.class, id);
			if (danceEvent != null) {
				if (!danceEvent.location.id.equals(location.id)) {
					System.out.println("Cross id");
				}
				if (danceEvent.status == EventStatus.edited) {
					result.skippedEditedEvents++;
					continue;
				} else if (danceEvent.status == EventStatus.blocked) {
					result.skippedBlockedEvents++;
					continue;
				}
				insert = false;
			} else {
				danceEvent = new DanceEvent();
				danceEvent.id = id;
				danceEvent.location = location;
			}

			danceEvent.header = "Ryva";
			danceEvent.title = title;

			danceEvent.from = start.toLocalTime();
			danceEvent.until = end.toLocalTime();
			danceEvent.status = EventStatus.generated;
			danceEvent.date = start.toLocalDate();
			danceEvent.location = location;
			danceEvent.description = description;
			if (description.contains("Egge Tänzern")) {
				danceEvent.tags.add(EventTag.Taxidancer);
			}
			if (description.contains("live")) {
				danceEvent.tags.add(EventTag.LiveBand);
			}

			if (insert) {
				Backend.insert(danceEvent);
				result.newEvents++;
			} else {
				Backend.update(danceEvent);
				result.updatedEvents++;
			}
		}
		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Rubernstrasse 2";
		location.city = "4852 Rothrist";
		location.region.add(Region.LU);
		location.name = "Ryva";
		location.url = "http://www.ryva.ch/";
		return location;
	}

}