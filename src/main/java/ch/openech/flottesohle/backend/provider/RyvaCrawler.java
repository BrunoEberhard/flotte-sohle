package ch.openech.flottesohle.backend.provider;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonReader;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.EventTag;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Region;

public class RyvaCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

//	curl "https://www.ecken.ch/_api/wix-one-events-server/html/v2/widget-data?compId=comp-jcs3jzsw&locale=de&regional=de&viewMode=site&members=false&paidPlans=false&responsive=false&widgetType=2&listLayout=1&showcase=false&tz=Europe"%"2FZurich" -H "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:98.0) Gecko/20100101 Firefox/98.0" -H "Accept: application/json, text/plain, */*" -H "Accept-Language: de,en;q=0.8,en-US;q=0.5,pl;q=0.3" -H "Accept-Encoding: gzip, deflate, br" -H "x-wix-brand: wix" -H "authorization: l-qoxP7m7aSm4kH8dQDtnLLjcV4LkhJ2TKukvVGBug4.eyJpbnN0YW5jZUlkIjoiYTcxMTE2MDEtZGM2Ni00NTFjLWEzYzItZTRjZmE4YWIzZDdmIiwiYXBwRGVmSWQiOiIxNDA2MDNhZC1hZjhkLTg0YTUtMmM4MC1hMGY2MGNiNDczNTEiLCJtZXRhU2l0ZUlkIjoiYmE4NmUzYjctMjFlYi00ZDQ1LTg0YTctZDYzOWNiODhhM2NkIiwic2lnbkRhdGUiOiIyMDIyLTAzLTIxVDA2OjI2OjA3LjY3M1oiLCJkZW1vTW9kZSI6ZmFsc2UsImFpZCI6ImU3MWZmMzE5LTIzOTAtNDM1YS05NTMzLTJlOGE3MGZmY2Q3MSIsImJpVG9rZW4iOiIxZDk3ZjViNi1mZDhkLTA4NTktMjc2NS0zMmY2NjMyMzllYjIiLCJzaXRlT3duZXJJZCI6IjAwNGE1ZTE5LTZmMzctNDQyOC04ODRmLWRkYzdkN2ExMjdhMyJ9" -H "X-Wix-Client-Artifact-Id: wix-thunderbolt" -H "commonConfig: "%"7B"%"22brand"%"22"%"3A"%"22wix"%"22"%"2C"%"22BSI"%"22"%"3A"%"22971fbd97-40dd-423f-97d0-a38c627549e3"%"7C1"%"22"%"7D" -H "x-wix-linguist: undefined" -H "Content-Type: application/json" -H "DNT: 1" -H "Connection: keep-alive" -H "Cookie: XSRF-TOKEN=1647763378|pZ_6BM_gaEqr" -H "Sec-Fetch-Dest: empty" -H "Sec-Fetch-Mode: no-cors" -H "Sec-Fetch-Site: cross-site" -H "If-None-Match: W/""e0f9-1357zNBoHSwfcNttaEKTWed4l70""" -H "Cache-Control: max-age=0, no-cache" -H "TE: trailers" -H "Referer: https://www.ecken.ch/" -H "Pragma: no-cache"
	
//	GET /_api/wix-one-events-server/html/v2/widget-data?compId=comp-jcs3jzsw&locale=de&regional=de&viewMode=site&members=false&paidPlans=false&responsive=false&widgetType=2&listLayout=1&showcase=false&tz=Europe%2FZurich HTTP/1.1
//			Host: www.ecken.ch
//			User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:98.0) Gecko/20100101 Firefox/98.0
//			Accept: application/json, text/plain, */*
//			Accept-Language: de,en;q=0.8,en-US;q=0.5,pl;q=0.3
//			Accept-Encoding: gzip, deflate, br
//			x-wix-brand: wix
//			authorization: l-qoxP7m7aSm4kH8dQDtnLLjcV4LkhJ2TKukvVGBug4.eyJpbnN0YW5jZUlkIjoiYTcxMTE2MDEtZGM2Ni00NTFjLWEzYzItZTRjZmE4YWIzZDdmIiwiYXBwRGVmSWQiOiIxNDA2MDNhZC1hZjhkLTg0YTUtMmM4MC1hMGY2MGNiNDczNTEiLCJtZXRhU2l0ZUlkIjoiYmE4NmUzYjctMjFlYi00ZDQ1LTg0YTctZDYzOWNiODhhM2NkIiwic2lnbkRhdGUiOiIyMDIyLTAzLTIxVDA2OjI2OjA3LjY3M1oiLCJkZW1vTW9kZSI6ZmFsc2UsImFpZCI6ImU3MWZmMzE5LTIzOTAtNDM1YS05NTMzLTJlOGE3MGZmY2Q3MSIsImJpVG9rZW4iOiIxZDk3ZjViNi1mZDhkLTA4NTktMjc2NS0zMmY2NjMyMzllYjIiLCJzaXRlT3duZXJJZCI6IjAwNGE1ZTE5LTZmMzctNDQyOC04ODRmLWRkYzdkN2ExMjdhMyJ9
//			X-Wix-Client-Artifact-Id: wix-thunderbolt
//			commonConfig: %7B%22brand%22%3A%22wix%22%2C%22BSI%22%3A%22971fbd97-40dd-423f-97d0-a38c627549e3%7C1%22%7D
//			x-wix-linguist: undefined
//			Content-Type: application/json
//			DNT: 1
//			Connection: keep-alive
//			Referer: https://www.ecken.ch/_partials/wix-thunderbolt/dist/clientWorker.3a668bd7.bundle.min.js
//			Cookie: XSRF-TOKEN=1647763378|pZ_6BM_gaEqr
//			Sec-Fetch-Dest: empty
//			Sec-Fetch-Mode: cors
//			Sec-Fetch-Site: same-origin
//			If-None-Match: W/"e0f9-1357zNBoHSwfcNttaEKTWed4l70"
//			Cache-Control: max-age=0
	
	private static final String AGENDA_URL = "https://www.ecken.ch/_api/wix-one-events-server/html/v2/widget-data?compId=comp-jcs3jzsw&locale=de&regional=de&viewMode=site&members=false&paidPlans=false&responsive=false&widgetType=2&listLayout=1&showcase=false&tz=Europe%2FZurich";

	@SuppressWarnings("unchecked")
	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		try {
			Map<String, Object> d = (Map<String, Object>) JsonReader.read(new URL(AGENDA_URL).openStream());
			Map<String, Object> component = (Map<String, Object>) d.get("component");
			Collection<Object> events = (Collection<Object>) component.get("events");

			for (Object e : events) {
				Map<String, Object> event = (Map<String, Object>) e;

				String id = (String) event.get("id");

				Map<String, Object> scheduling = (Map<String, Object>) event.get("scheduling");
				Map<String, Object> config = (Map<String, Object>) scheduling.get("config");

				LocalDateTime start = LocalDateTime.parse(((String) config.get("startDate")).substring(0, 19));
//				LocalDateTime end = LocalDateTime.parse(((String) config.get("endDate")).substring(0, 19));
	//
//				String timeZoneString = (String) config.get("timeZoneId");
//				ZoneId zoneId = ZoneId.of(timeZoneString);
//				ZoneOffset offset = zoneId.getRules().getOffset(start);
//				start = ZoneOffsetTransition.of(start, ZoneOffset.UTC, offset).getDateTimeAfter();
//				end = ZoneOffsetTransition.of(end, ZoneOffset.UTC, offset).getDateTimeAfter();

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

				danceEvent.line = title;

//				danceEvent.from = start.toLocalTime();
//				danceEvent.until = end.toLocalTime();
				danceEvent.from = LocalTime.of(20, 30);
				danceEvent.until = LocalTime.of(2, 0);

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
		} catch (IOException x) {
			result.failedEvents++;
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