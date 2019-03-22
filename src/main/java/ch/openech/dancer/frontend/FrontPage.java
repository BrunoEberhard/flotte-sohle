package ch.openech.dancer.frontend;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.page.HtmlPage;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.util.DateUtils;

import ch.openech.dancer.model.DanceEvent;

public class FrontPage extends HtmlPage {

	private static String template;
	
	public FrontPage() {
		super(createHtml(), "Flotte Sohle");
	}

	private static String createHtml() {
		template = JsonFrontend.readStream(FrontPage.class.getResourceAsStream("/ch/openech/dancer/frontpage.html"));
		return fillTemplate(template);
	}

	private static String fillTemplate(String template) {
		List<DanceEvent> events = Backend.find(DanceEvent.class, By //
				.field(DanceEvent.$.date, FieldOperator.greaterOrEqual, LocalDate.now()) //
				.and(By.field(DanceEvent.$.date, FieldOperator.less, LocalDate.now().plusDays(7)))
				.order(DanceEvent.$.date));

		String result = template.replace("$DieseWoche", createTable(events));
		result = result.replace("$Flyers", createFlyers(events));
//		events = Backend.find(DanceEvent.class, By //
//				.field(DanceEvent.$.date, FieldOperator.greaterOrEqual, LocalDate.now().plusDays(7)) //
//				.and(By.field(DanceEvent.$.date, FieldOperator.less, LocalDate.now().plusDays(14)))
//				.order(DanceEvent.$.date));
		
		return result;
	}

	private static String createFlyers(List<DanceEvent> events) {
		StringBuilder s = new StringBuilder();
		for (DanceEvent event : events) {
			if (event.flyer != null && event.flyer.image != null) {
				s.append("<td><img class=\"flyer\" src=\"data:image;base64,");
				s.append(Base64.getEncoder().encodeToString(event.flyer.image));
				s.append("\"></td>");
			}
		}
		return s.toString();
	}

	private static String createTable(List<DanceEvent> events) {
		StringBuilder s = new StringBuilder();
		for (DanceEvent event : events) {
			s.append("<tr><td class=\"columnDayOfWeek\">").append(event.getDayOfWeek()).append("</td>");
			s.append("<td>").append(DateUtils.format(event.date)).append("</td>");
			s.append("<td class=\"columnTime\">").append(event.getFromUntil()).append("</td>");
			s.append("<td>").append("<a href=\"event/").append(event.id).append("\">").append(event.location.name)
					.append("</a></td>");
			s.append("<td class=\"columnDeejay\">").append(event.deeJay != null ? event.deeJay.name : "")
					.append("</td>");
			s.append("<td class=\"columnTitle\">").append(event.title).append("</td></tr>");
		}
		return s.toString();
	}

}
