package ch.openech.dancer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.thymeleaf.ThymeHttpHandler;
import org.minimalj.thymeleaf.ThymeRequest;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.model.AccessCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;

public class ThymeDancerHandler extends ThymeHttpHandler {

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	private static final LocationMapDataProvider locationMapDataProvider = new LocationMapDataProvider();

	@Override
	protected void handle(ThymeRequest request) {
		if (StringUtils.equals(request.getPath(), "/sohle.css")) {
			updateAccessCounter();
		}
		
		if (StringUtils.equals(request.getPath(), "/events.html", "/")) {
			List<DanceEvent> events = Backend.find(DanceEvent.class, DancerRepository.EventsQuery.instance);
			request.put("eventsByDay", viewEvents(events));
			request.sendResponse("events.html");

		} else if (request.getPath().startsWith("/event/")) {
			String id = request.getPath().substring("/event/".length());
			DanceEvent event = Backend.read(DanceEvent.class, id);
			request.put("event", event);
			request.sendResponse("event.html");

		} else if (request.getPath().equals("/query")) {
			List<DanceEvent> events = Backend.find(DanceEvent.class,
					By.search(request.getParameters().get("query").get(0)).and(By.field(DanceEvent.$.date, FieldOperator.less, LocalDate.now().plusMonths(1))).order(DanceEvent.$.date));
			request.put("eventsByDay", viewEvents(events));
			request.sendResponse("events.html");

		} else if (StringUtils.equals(request.getPath(), "/locations.html")) {
			List<Location> locations = Backend.find(Location.class, By.ALL.order(Location.$.name));
			request.put("locations", locations);
			request.sendResponse();

		} else if (StringUtils.equals(request.getPath(), "/location_map.html")) {
			request.put("locations", locationMapDataProvider.getLocationMapData());
			request.sendResponse();

		} else if (StringUtils.equals(request.getPath(), "/infos.html")) {
			request.sendResponse();

//		} else if (request.getPath().startsWith("/specialDays/")) {
//			Map<String, List<String>> parameters = request.getParameters();
//
//			if (parameters.containsKey("locationId")) {
//				Object locationId = parameters.get("locationId").get(0);
//				Location location = Backend.read(Location.class, locationId);
//				for (String p : parameters.keySet()) {
//					if (p.startsWith("day")) {
//						Integer specialDayId = Integer.parseInt(p.substring(3));
//						boolean found = false;
//						for (SpecialDayInfo s : location.specialDayInfos) {
//							if (s.specialDay.id.equals(specialDayId)) {
//								s.closed = Integer.parseInt(parameters.get(p).get(0)) == 1;
//								found = true;
//							}
//						}
//						if (!found) {
//							SpecialDayInfo s = new SpecialDayInfo();
//							s.specialDay = Codes.findCode(SpecialDay.class, specialDayId);
//							s.closed = Integer.parseInt(parameters.get(p).get(0)) == 1;
//							location.specialDayInfos.add(s);
//						}
//					}
//				}
//				Backend.update(location);
//			}
//			// String id = path.substring("/specialDays/".length());
//			// Location location = Backend.read(Location.class, id);
//			Location location = Backend.find(Location.class, By.ALL).get(0);
//			request.put("location", location);
//			request.put("specialDayGroups", SpecialDayGroupViewModel.toViewModel(location));
//			request.sendResponse("specialDays.html");
		}
	}

	public Map<String, List<DanceEvent>> viewEvents(List<DanceEvent> events) {
		Map<String, List<DanceEvent>> eventsByDay = new LinkedHashMap<>();
		List<DanceEvent> currentDay = null;
		LocalDate lastDate = null;
		Iterator<DanceEvent> i = events.iterator();
		while (i.hasNext()) {
			DanceEvent event = i.next();
			if (!event.date.equals(lastDate)) {
				currentDay = new ArrayList<>();
				String day = event.getDayOfWeek() + ", " + shortFormat.format(event.date);
				eventsByDay.put(day, currentDay);
				lastDate = event.date;
			}
			currentDay.add(event);
		}
		return eventsByDay;
	}

	private static void updateAccessCounter() {
		LocalDate now = LocalDate.now();
		List<AccessCounter> counterList = Backend.find(AccessCounter.class, By.field(AccessCounter.$.date, now));
		AccessCounter counter;
		if (counterList.isEmpty()) {
			counter = new AccessCounter();
			counter.date = now;
			counter.count = 1;
			Backend.insert(counter);
		} else {
			counter = counterList.get(0);
			counter.count += 1;
			Backend.update(counter);
		}
	}
}
