package ch.openech.dancer.frontend;

import java.time.LocalDate;
import java.util.List;

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
		super(createHtml(), "Anlässe");
	}

	private static String createHtml() {
		template = JsonFrontend
				.readStream(EventsPage.class.getResourceAsStream("/ch/openech/dancer/events.html"));
		return fillTemplate(template);
	}

	private static String fillTemplate(String template) {
		List<DanceEvent> events = Backend.find(DanceEvent.class, By //
				.field(DanceEvent.$.date, FieldOperator.greaterOrEqual, LocalDate.now()) //
				.and(By.field(DanceEvent.$.date, FieldOperator.less, LocalDate.now().plusMonths(1)))
				.order(DanceEvent.$.date));

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
		s.append("<h1 class=\"Day\">").append(event.getDayOfWeek()).append(", ").append(DateUtils.format(event.date))
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

}
