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
import ch.openech.dancer.model.SpecialDayGroupViewModel;

public class ThyDancerHandler extends ThymeHttpHandler {

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");


	@Override
	public Map<String, Object> createContext(MjHttpExchange exchange) {
		Map<String, Object> variables = super.createContext(exchange);
		String path = exchange.getPath();

		if (StringUtils.equals(path, "/events.html")) {
			List<DanceEvent> events = Backend.find(DanceEvent.class, DancerRepository.EventsQuery.instance);
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
			variables.put("eventsByDay", eventsByDay);
		}

		if (path.startsWith("/event/")) {
			String id = path.substring("/event/".length());
			DanceEvent event = Backend.read(DanceEvent.class, id);
			variables.put("event", event);
		}

		if (path.startsWith("/specialDays/")) {
			// String id = path.substring("/specialDays/".length());
			// Location location = Backend.read(Location.class, id);
			Location location = Backend.find(Location.class, By.ALL).get(0);
			variables.put("location", location);
			variables.put("specialDayGroups", SpecialDayGroupViewModel.toViewModel(location));
		}

		if (StringUtils.equals(path, "/locations.html")) {
			List<Location> locations = Backend.find(Location.class, By.ALL.order(Location.$.name));
			variables.put("locations", locations);
		}

		return variables;
	}

	@Override
	public String resolveTemplate(MjHttpExchange exchange) {
		if (exchange.getPath().startsWith("/event/")) {
			return "event.html";
		} else if (exchange.getPath().startsWith("/specialDays/")) {
			return "specialDays.html";
		}

		return super.resolveTemplate(exchange);
	}

}
