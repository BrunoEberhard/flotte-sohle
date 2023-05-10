package ch.flottesohle.frontend;

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
import org.minimalj.util.CloneHelper;
import org.minimalj.util.resources.Resources;

import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;

public class DanceEventLocationTablePage extends SimpleTableEditorPage<DanceEvent> {

	private final Location location;

	public DanceEventLocationTablePage(Location location) {
		this.location = location;
	}

	@Override
	protected boolean allowMultiselect() {
		return true;
	}

	@Override
	protected Object[] getColumns() {
		return new Object[] { DanceEvent.$.date, DanceEvent.$.line, DanceEvent.$.from, DanceEvent.$.status };
	}

	@Override
	public List<Action> getTableActions() {
		return Arrays.asList(new TableNewObjectEditor(), new DanceEventTableEditor(), new DeleteDetailAction(), new DanceEventCopyEditor());
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

	private class DanceEventCopyEditor extends TableEditor {
		@Override
		protected DanceEvent createObject() {
			DanceEvent event = new DanceEvent();
			CloneHelper.deepCopy(super.createObject(), event);
			event.id = null;
			event.status = EventStatus.edited;
			return event;
		}
	}

	@Override
	protected List<DanceEvent> load() {
		return Backend.find(DanceEvent.class, By.field(DanceEvent.$.location, location).order(DanceEvent.$.date));
	}

	@Override
	protected DanceEvent createObject() {
		DanceEvent event = new DanceEvent();
		event.location = location;
		event.from = LocalTime.of(20, 00);
		event.until = LocalTime.of(23, 00);
		event.status = EventStatus.edited;
		return event;
	}

	@Override
	protected Form<DanceEvent> createForm(boolean editable, boolean newObject) {
		return new DanceEventForm(editable, false);
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