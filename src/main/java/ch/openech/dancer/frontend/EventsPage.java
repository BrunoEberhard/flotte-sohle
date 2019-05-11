package ch.openech.dancer.frontend;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.Frontend.IContent;
import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.page.Page;
import org.minimalj.util.DateUtils;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Region;

public class EventsPage extends Page {

	private static String template;
	private final String title;
	private final Predicate<DanceEvent> filter;
	static {
		template = JsonFrontend.readStream(EventsPage.class.getResourceAsStream("/ch/openech/dancer/events.html"));
	}

	public EventsPage() {
		this.title = "Anlässe";
		filter = event -> true;
	}

	public EventsPage(String query) {
		this.title = "Suche: " + query;
		filter = new DanceEventFilter(query);
	}

	public EventsPage(Region region) {
		this.title = "Region: " + region;
		filter = new DanceRegionEventFilter(region);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public IContent getContent() {
		List<DanceEvent> events = Backend.find(DanceEvent.class, DancerRepository.EventsQuery.instance);
		return Frontend.getInstance().createHtmlContent(fillTemplate(events.stream().filter(filter)));
	}

	private static String fillTemplate(Stream<DanceEvent> events) {
		StringBuilder s = new StringBuilder();
		createBlocks(events, s);
		
		String result = template.replace("$Blocks", s.toString());

		return result;
	}

	private static void createBlocks(Stream<DanceEvent> events, StringBuilder s) {
		LocalDate lastDate = null;
		Iterator<DanceEvent> i = events.iterator();
		while (i.hasNext()) {
			DanceEvent event = i.next();
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
		s.append("<div class=\"DanceEvent\">");
		s.append("<div class=\"Header\">");
		appendLink(event, s);
		s.append(event.header).append("</a></div>");
		/*
		 * if (event.flyer != null && event.flyer.image != null) {
		 * s.append("<img class=\"EventPic\" src=\"data:image;base64,");
		 * s.append(Base64.getEncoder().encodeToString(event.flyer.image));
		 * s.append("\">"); }
		 */
		if (!StringUtils.isEmpty(event.line)) {
			s.append("<div class=\"EventTitle\">").append(event.line).append("</div>");
		}
		if (event.deeJay != null) {
			s.append("<div class=\"DeeJay\">").append(event.deeJay.name).append("</div>");
		}
		s.append("<div class=\"FromUntil\">").append(event.getFromUntil()).append("</div>");
		if (event.location != null && !StringUtils.isEmpty(event.location.city)) {
			s.append("<div class=\"Location\">").append(event.location.city).append("</div>");
		}
		s.append("</div>");
	}

	private static void appendLink(DanceEvent event, StringBuilder s) {
		s.append("<a href=\"event/").append(event.id).append("\" rel=\"nofollow\">");
	}

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	private static class DanceRegionEventFilter implements Predicate<DanceEvent> {

		private final Region region;

		public DanceRegionEventFilter(Region region) {
			this.region = region;
		}

		@Override
		public boolean test(DanceEvent event) {
			return event.location.region.contains(region);
		}
	}

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
