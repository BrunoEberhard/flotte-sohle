package ch.openech.dancer.frontend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.repository.query.By;
import org.minimalj.util.resources.Resources;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;

public class DanceEventAdminTablePage extends SimpleTableEditorPage<DanceEvent> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { DanceEvent.$.date, DanceEvent.$.title, DanceEvent.$.line, DanceEvent.$.from, DanceEvent.$.location.name, DanceEvent.$.status };
	}

	@Override
	public List<Action> getTableActions() {
		return Arrays.asList(new TableNewObjectEditor(), new DanceEventTableEditor(), new DeleteDetailAction());
	}

	private class DanceEventTableEditor extends TableEditor {
		@Override
		protected DanceEvent createObject() {
			DanceEvent event = super.createObject();
			if (event.status == EventStatus.generated) {
				event.status = EventStatus.edited;
			}
			return event;
		}
	}

	@Override
	protected List<DanceEvent> load() {
		return Backend.find(DanceEvent.class, By.all().order(DanceEvent.$.location).order(DanceEvent.$.date));
	}

	@Override
	protected DanceEvent createObject() {
		DanceEvent event = new DanceEvent();
		event.from = LocalTime.of(20, 00);
		event.until = LocalTime.of(23, 00);
		event.status = EventStatus.edited;
		return event;
	}

	@Override
	protected Form<DanceEvent> createForm(boolean editable, boolean newObject) {
		return new DanceEventForm(editable, true);
	}


	@Override
	protected void validate(DanceEvent event, boolean newObject, List<ValidationMessage> validationMessages) {
		if (newObject) {
			if (event.date != null && event.date.isBefore(LocalDate.now())) {
				validationMessages.add(new ValidationMessage(DanceEvent.$.date, Resources.getString("DanceEvent.validation.past")));
			}
		}
	}

}