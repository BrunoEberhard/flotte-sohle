package ch.openech.dancer.frontend;

import java.time.format.DateTimeFormatter;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.page.HtmlPage;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.model.DanceEvent;

public class EventPage extends HtmlPage {

	private final Object id;

	public EventPage(Object id) {
		super(createHtml(id));
		this.id = id;
	}

	@Override
	public String getTitle() {
		// TODO DanceEvent nur einmal lesen
		DanceEvent event = Backend.read(DanceEvent.class, id);
		return event.header;
	}

	public Object getId() {
		return id;
	}

	private static String template;
	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	static {
		template = JsonFrontend.readStream(EventPage.class.getResourceAsStream("/ch/openech/dancer/event.html"));
	}

	private static String createHtml(Object id) {
		DanceEvent event = Backend.read(DanceEvent.class, id);
		return fillTemplate(event);
	}

	private static String fillTemplate(DanceEvent event) {
		String result = template.replace("$title", event.title);
		result = result.replace("$description", event.description != null ? event.description : "");
		result = result.replace("$fromUntil", event.getFromUntil());
		result = result.replace("$location", event.location.name);
		result = result.replace("$address", event.location.address);
		result = result.replace("$city", event.location.city);

		String date = event.getDayOfWeek() + ", " + shortFormat.format(event.date);
		result = result.replace("$date", date);

		if (!StringUtils.isEmpty(event.location.url)) {
			result = result.replace("$url", event.location.url);
		}
		return result;
	}

}
