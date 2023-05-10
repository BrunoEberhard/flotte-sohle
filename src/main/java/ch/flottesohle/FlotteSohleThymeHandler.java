package ch.flottesohle;

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

import ch.flottesohle.backend.FlotteSohleRepository;
import ch.flottesohle.model.AccessCounter;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.Location;

public class FlotteSohleThymeHandler extends ThymeHttpHandler {

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	private static final LocationMapDataProvider locationMapDataProvider = new LocationMapDataProvider();

	@Override
	protected void handle(ThymeRequest request) {
		String path = request.getPath();
		if (StringUtils.equals(path, "/sohle.css")) {
			updateAccessCounter();
		}

		if (StringUtils.equals(path, "/", "/events.html", "/flyer.html")) {
			List<DanceEvent> events = Backend.find(DanceEvent.class, FlotteSohleRepository.EventsQuery.instance);
			request.put("eventsByDay", viewEvents(events));
			request.sendResponse(StringUtils.equals(path, "/flyer.html") ? "flyer.html" : "events.html");

		} else if (path.startsWith("/event/")) {
			String id = path.substring("/event/".length());
			DanceEvent event = Backend.read(DanceEvent.class, id);
			request.put("event", event);
			if (event != null) {
				request.put("title", event.location.name + " " + shortFormat.format(event.date));
			} else {
				request.put("title", "Anlass nicht gefunden");
			}
			request.sendResponse("event.html");

		} else if (path.equals("/query")) {
			List<DanceEvent> events = Backend.find(DanceEvent.class,
					By.search(request.getParameter("query")).and(By.field(DanceEvent.$.date, FieldOperator.less, LocalDate.now().plusMonths(1))).order(DanceEvent.$.date));
			request.put("eventsByDay", viewEvents(events));
			request.sendResponse("events.html");

		} else if (StringUtils.equals(path, "/locations.html")) {
			List<Location> locations = Backend.find(Location.class, By.ALL.order(Location.$.name));
			request.put("locations", locations);
			request.sendResponse(path);

		} else if (StringUtils.equals(path, "/location_map.html")) {
			request.put("locations", locationMapDataProvider.getLocationMapData());
			request.sendResponse(path);
		} else if (StringUtils.equals(path, "/infos.html")) {
			request.sendResponse(path);
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
