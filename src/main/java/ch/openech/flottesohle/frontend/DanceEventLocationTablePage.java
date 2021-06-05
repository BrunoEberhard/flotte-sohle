package ch.openech.flottesohle.frontend;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.repository.query.By;

import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;

public class DanceEventLocationTablePage extends SimpleTableEditorPage<DanceEvent> {

	private final Location location;

	public DanceEventLocationTablePage(Location location) {
		this.location = location;
	}

	@Override
	protected Object[] getColumns() {
		return new Object[] { DanceEvent.$.date, DanceEvent.$.line, DanceEvent.$.status };
	}

	@Override
	public List<Action> getTableActions() {
		return Arrays.asList(new TableNewObjectEditor(), new TableEditor(), new DeleteDetailAction());
	}

	@Override
	protected List<DanceEvent> load() {
		return Backend.find(DanceEvent.class, By.field(DanceEvent.$.location, location).order(DanceEvent.$.date));
	}

	@Override
	protected DanceEvent createObject() {
		DanceEvent event = new DanceEvent();
		event.location = location;
		event.status = EventStatus.edited;
		return event;
	}

	@Override
	protected Form<DanceEvent> createForm(boolean editable, boolean newObject) {
		return new DanceEventForm(editable, false);
	}

	@Override
	protected DanceEvent save(DanceEvent event) {
		event.status = EventStatus.edited;
		return super.save(event);
	}

	@Override
	protected void validate(DanceEvent event, boolean newObject, List<ValidationMessage> validationMessages) {
		if (newObject) {
			if (event.date != null && event.date.isBefore(LocalDate.now())) {
				validationMessages.add(new ValidationMessage(DanceEvent.$.date, "Muss in Zukunft liegen"));
			}
		}
	}

}