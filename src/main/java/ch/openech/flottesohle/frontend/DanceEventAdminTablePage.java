package ch.openech.flottesohle.frontend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend.FormContent;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.ComboBoxFormElement;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.model.Keys;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.Criteria;
import org.minimalj.repository.query.Order;
import org.minimalj.transaction.Role;
import org.minimalj.util.CloneHelper;
import org.minimalj.util.resources.Resources;

import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;

@Role("admin")
public class DanceEventAdminTablePage extends SimpleTableEditorPage<DanceEvent> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { DanceEvent.$.date, DanceEvent.$.location.name, DanceEvent.$.line, DanceEvent.$.from, DanceEvent.$.location.name, DanceEvent.$.status };
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
	
	private final EventFilter filter = new EventFilter();

	@Override
	protected FormContent getOverview() {
		Form<EventFilter> form = new Form<>(2);
		List<Location> locations = Backend.find(Location.class, By.all().order(Location.$.name));
		form.line(new ComboBoxFormElement<Location>(Keys.getProperty(EventFilter.$.location), locations));
		form.setChangeListener(source -> refresh());
		form.setObject(filter);
		return form.getContent();
	}

	public static class EventFilter {
		public static final EventFilter $ = Keys.of(EventFilter.class);

		public Location location;

		public LocalDate date;
	}

	@Override
	protected List<DanceEvent> load() {
		Criteria criteria = By.ALL;
		if (filter.date != null) {
			criteria = criteria.and(By.field(EventFilter.$.date, filter.date));
		}
		if (filter.location != null) {
			criteria = criteria.and(By.field(EventFilter.$.location, filter.location));
		}
		Order order = criteria.order(DanceEvent.$.location).order(DanceEvent.$.date);
		return Backend.find(DanceEvent.class, order);
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
	public void action(DanceEvent event) {
		// detail nervt nur
		openEditor(event);
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