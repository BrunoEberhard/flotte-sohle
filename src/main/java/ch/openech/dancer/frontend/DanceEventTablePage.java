package ch.openech.dancer.frontend;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.ImageFormElement;
import org.minimalj.frontend.form.element.TextFormElement;
import org.minimalj.frontend.page.TableFormPage;
import org.minimalj.util.DateUtils;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.model.DanceEvent;

public class DanceEventTablePage extends TableFormPage<DanceEvent> {

	private static final Object[] KEYS = new Object[] { DanceEvent.$.getDayOfWeek(), DanceEvent.$.date,
			DanceEvent.$.title, DanceEvent.$.location.name, DanceEvent.$.getFromUntil(),
			DanceEvent.$.deeJay.name };

	private final String query;
	
	public DanceEventTablePage(String query) {
		super(KEYS);
		this.query = query != null ? query.toLowerCase() : null;
	}

	public DanceEventTablePage() {
		this(null);
	}
	
	@Override
	protected List<DanceEvent> load() {
		List<DanceEvent> events = Backend.find(DanceEvent.class, DancerRepository.EventsQuery.instance);

		if (!StringUtils.isEmpty(query)) {
			List<DanceEvent> filteredEvents = events.stream().filter(new DanceEventFilter())
					.collect(Collectors.toList());
			return filteredEvents;
		} else {
			return events;
		}
	}

	private static DateTimeFormatter shortFormat = DateTimeFormatter.ofPattern("d.M.yyyy");

	private class DanceEventFilter implements Predicate<DanceEvent> {

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

	protected Form<DanceEvent> createForm() {
		Form<DanceEvent> form = new Form<>(Form.READ_ONLY, 2);
		form.line(DanceEvent.$.date, DanceEvent.$.getDayOfWeek());
		form.line(DanceEvent.$.from, DanceEvent.$.until);
		form.line(DanceEvent.$.title);
		form.line(DanceEvent.$.description);
		form.line(new ImageFormElement(DanceEvent.$.flyer, Form.READ_ONLY, 3));
		form.line(new TextFormElement(DanceEvent.$.location));
		form.line(DanceEvent.$.tags);
		form.line(DanceEvent.$.status);
		return form;
	}

}