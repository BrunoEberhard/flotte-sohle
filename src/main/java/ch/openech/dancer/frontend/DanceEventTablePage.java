package ch.openech.dancer.frontend;

import java.time.LocalDate;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.ReferenceFormElement;
import org.minimalj.frontend.form.element.TextFormElement;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.model.Keys;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Organizer;

public class DanceEventTablePage extends SimpleTableEditorPage<DanceEvent> {

	private static final Object[] keys = new Object[] { DanceEvent.$.start, DanceEvent.$.title, DanceEvent.$.location.name, DanceEvent.$.organizer.name };
	
	public DanceEventTablePage() {
		super(keys);
	}
	
	@Override
	protected List<DanceEvent> load() {
		return Backend.find(DanceEvent.class, By.ALL);
	}

	@Override
	protected Form<DanceEvent> createForm(boolean editable, boolean newObject) {
		Form<DanceEvent> form = new Form<>(editable, 2);
		form.line(DanceEvent.$.start);
		form.line(new DanceEventPeriodFormElement(DanceEvent.$.danceEventPeriod, editable));
		form.line(DanceEvent.$.title);
		form.line(DanceEvent.$.description);
		form.line(editable ? new ReferenceFormElement<>(DanceEvent.$.location, Location.$.name) : new TextFormElement(DanceEvent.$.location));
		form.line(editable ? new ReferenceFormElement<>(DanceEvent.$.organizer, Organizer.$.name) : new TextFormElement(DanceEvent.$.organizer));
		form.line(new RecurFormElement(Keys.getProperty(DanceEvent.$.recur), editable));
		return form;
	}
	
	@Override
	protected DanceEvent createObject() {
		DanceEvent danceEvent = super.createObject();
		return danceEvent;
	}
	
	@Override
	protected void validate(DanceEvent event, boolean newObject, List<ValidationMessage> validationMessages) {
		if (newObject) {
			if (event.start != null && event.start.isBefore(LocalDate.now())) {
				validationMessages.add(new ValidationMessage(DanceEvent.$.start, "Muss in Zukunft liegen"));
			}
		}
	}
	
}
