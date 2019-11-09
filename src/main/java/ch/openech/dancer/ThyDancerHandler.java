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
import org.minimalj.thymeleaf.ThymeHttpHandler;
import org.minimalj.util.StringUtils;
import org.thymeleaf.context.Context;

import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.SpecialDayGroupViewModel;

public class ThyDancerHandler extends ThymeHttpHandler {

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	@Override
	public void fillContext(String path, Context context) {
		LinkedHashMap<String, String> navigation = new LinkedHashMap<>();
//		for (Action action : Application.getInstance().getNavigation()) {
//			PageAction pageAction = (PageAction) action;
//			navigation.put(pageAction.getName(), Routing.getRouteSafe(pageAction.getPage()));
//		}
		navigation.put("Anlässe", "/events.html");
		navigation.put("Tanzkarte", "/location_map.html");
		navigation.put("Veranstalter", "/locations.html");
		navigation.put("Info", "/infos.html");
		navigation.put("Special", "/specialDays.html");

		context.setVariable("navigation", navigation);

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
			context.setVariable("eventsByDay", eventsByDay);
		}

		if (path.startsWith("/event/")) {
			String id = path.substring("/event/".length());
			DanceEvent event = Backend.read(DanceEvent.class, id);
			context.setVariable("event", event);
		}

		if (path.startsWith("/specialDays/")) {
			// String id = path.substring("/specialDays/".length());
			// Location location = Backend.read(Location.class, id);
			Location location = Backend.find(Location.class, By.ALL).get(0);
			context.setVariable("location", location);
			context.setVariable("specialDayGroups", SpecialDayGroupViewModel.toViewModel(location));
		}

		if (StringUtils.equals(path, "/locations.html")) {
			List<Location> locations = Backend.find(Location.class, By.ALL.order(Location.$.name));
			context.setVariable("locations", locations);
		}
	}

	@Override
	public String handle(String path, Object input) {
		if (path.endsWith(".png")) {
			return null;
		}

		if (path.startsWith("/event/")) {
			return "event.html";
		}
		if (path.startsWith("/specialDays/")) {
			return "specialDays.html";
		}
		if (StringUtils.equals(path, "/events.html", "/infos.html", "/container.html", "/locations.html", "/location_map.html", "/specialDays.html", "/action.html")) {
			return path;
		} else {
			return null;
		}
	}

}
