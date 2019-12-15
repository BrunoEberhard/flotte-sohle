package ch.openech.dancer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.web.MjHttpExchange;
import org.minimalj.repository.query.By;
import org.minimalj.thymeleaf.ThymeHttpHandler;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;

public class ThymeDancerHandler extends ThymeHttpHandler {

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	private static final LocationMapDataProvider locationMapDataProvider = new LocationMapDataProvider();

	@Override
	public Map<String, Object> createContext(MjHttpExchange exchange) {
		Map<String, Object> variables = super.createContext(exchange);
		String path = exchange.getPath();

		if (StringUtils.equals(path, "/", "/events.html")) {
			List<DanceEvent> events = Backend.find(DanceEvent.class, DancerRepository.EventsQuery.instance);
			variables.put("eventsByDay", viewEvents(events));
		}

		if (StringUtils.equals(path, "/query")) {
			List<DanceEvent> events = Backend.find(DanceEvent.class, By.search(exchange.getParameters().get("query").get(0)).order(DanceEvent.$.date));
			variables.put("eventsByDay", viewEvents(events));
		}

		if (path.startsWith("/event/")) {
			String id = path.substring("/event/".length());
			DanceEvent event = Backend.read(DanceEvent.class, id);
			variables.put("event", event);
		}

//		if (path.startsWith("/specialDays/")) {
//			Map<String, List<String>> parameters = (Map<String, List<String>>) variables.get("parameters");
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
//			variables.put("location", location);
//			variables.put("specialDayGroups", SpecialDayGroupViewModel.toViewModel(location));
//		}

		if (StringUtils.equals(path, "/locations.html")) {
			List<Location> locations = Backend.find(Location.class, By.ALL.order(Location.$.name));
			variables.put("locations", locations);
		}

		if (StringUtils.equals(path, "/location_map.html")) {
			variables.put("locations", locationMapDataProvider.getLocationMapData());
		}

		return variables;
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


	@Override
	public String getTemplateName(MjHttpExchange exchange) {
		if (exchange.getPath().equals("/") || exchange.getPath().equals("/query")) {
			return "events.html";
		} else if (exchange.getPath().startsWith("/event/")) {
			return "event.html";
		} else if (exchange.getPath().startsWith("/specialDays/")) {
			return "specialDays.html";
		}

		return super.getTemplateName(exchange);
	}

}
