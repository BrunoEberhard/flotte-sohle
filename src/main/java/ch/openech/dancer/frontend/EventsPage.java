package ch.openech.dancer.frontend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.page.HtmlPage;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.util.DateUtils;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.model.DanceEvent;

public class EventsPage extends HtmlPage {

	private static String template;
	
	public EventsPage() {
		super(createHtml(null), "Anlässe");
	}

	public EventsPage(String query) {
		super(createHtml(query), "Suche: " + query);
	}

	private static String createHtml(String query) {
		template = JsonFrontend
				.readStream(EventsPage.class.getResourceAsStream("/ch/openech/dancer/events.html"));
		List<DanceEvent> events = load(query);
		return fillTemplate(template, events);
	}

	protected static List<DanceEvent> load(String query) {
		List<DanceEvent> events = Backend.find(DanceEvent.class, By //
				.field(DanceEvent.$.date, FieldOperator.greaterOrEqual, LocalDate.now()) //
				.and(By.field(DanceEvent.$.date, FieldOperator.less, LocalDate.now().plusMonths(1)))
				.order(DanceEvent.$.date));

		// TODO fix this in minimal-j
		// wenn eine geordnete Liste Stückweise zurückgegeben wird ändert sich manchmal
		// die Reihenfolge und dann erscheinen Elemente doppelt
		events = events.subList(0, events.size());

		if (!StringUtils.isEmpty(query)) {
			List<DanceEvent> filteredEvents = events.stream().filter(new DanceEventFilter(query))
					.collect(Collectors.toList());
			return filteredEvents;
		} else {
			return events;
		}
	}

	private static String fillTemplate(String template, List<DanceEvent> events) {
		StringBuilder s = new StringBuilder();
		createBlocks(events, s);
		
		String result = template.replace("$Blocks", s.toString());

		return result;
	}

	private static void createBlocks(List<DanceEvent> events, StringBuilder s) {
		LocalDate lastDate = null;
		for (DanceEvent event : events) {
			if (!event.date.equals(lastDate)) {
				endDay(s, lastDate);
				lastDate = event.date;
				createDay(event, s);
			}
			createBlock(event, s);
		}
		endDay(s, lastDate);
	}

	private static void createDay(DanceEvent event, StringBuilder s) {
		s.append("<h1 class=\"Day\">").append(event.getDayOfWeek()).append(", ").append(shortFormat.format(event.date))
				.append("</h1>");
		s.append("<div class=\"DayEvents\">");
	}

	private static void endDay(StringBuilder s, LocalDate lastDate) {
		if (lastDate != null) {
			s.append("</div>");
		}
	}

	private static void createBlock(DanceEvent event, StringBuilder s) {
		s.append("<a href=\"event/").append(event.id).append("\">");
		s.append("<div class=\"DanceEvent\">");
		s.append("<h2 class=\"LocationName\">").append(event.location.name).append("</h2>");
		/*
		 * if (event.flyer != null && event.flyer.image != null) {
		 * s.append("<img class=\"EventPic\" src=\"data:image;base64,");
		 * s.append(Base64.getEncoder().encodeToString(event.flyer.image));
		 * s.append("\">"); }
		 */
		if (!StringUtils.isEmpty(event.displayTitle)) {
			s.append("<div class=\"Title\">").append(event.displayTitle).append("</div>");
		}
		if (event.deeJay != null) {
			s.append("<div class=\"DeeJay\">").append(event.deeJay.name).append("</div>");
		}
		s.append("<div class=\"FromUntil\">").append(event.getFromUntil()).append("</div>");
		if (event.location != null && !StringUtils.isEmpty(event.location.city)) {
			s.append("<div class=\"Location\">").append(event.location.city).append("</div>");
		}
		s.append("</div></a>");
	}

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	private static class DanceEventFilter implements Predicate<DanceEvent> {

		private final String query;

		public DanceEventFilter(String query) {
			this.query = query.toLowerCase();
		}

		@Override
		public boolean test(DanceEvent event) {
			if (containsQuery(event.title))
				return true;
			if (containsQuery(event.description))
				return true;
			if (event.location != null && containsQuery(event.location.name))
				return true;
			if (event.deeJay != null && containsQuery(event.deeJay.name))
				return true;
			if (event.deeJay2 != null && containsQuery(event.deeJay2.name))
				return true;
			String date = DateUtils.format(event.date);
			if (date.contains(query))
				return true;
			date = shortFormat.format(event.date);
			if (date.contains(query))
				return true;
			if (containsQuery(event.getDayOfWeek()))
				return true;
			return false;
		}

		private boolean containsQuery(String value) {
			return value != null && value.toLowerCase().contains(query);
		}
	}

}
