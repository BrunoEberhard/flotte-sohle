package ch.openech.flottesohle.frontend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.frontend.page.TablePage;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.Order;
import org.minimalj.transaction.Role;
import org.minimalj.util.CloneHelper;
import org.minimalj.util.resources.Resources;

import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;

@Role("admin")
public class DanceEventAdminTablePage extends SimpleTableEditorPage<DanceEvent> {

	private Location location;
	private final TablePage<?> parentPage;
	
	public DanceEventAdminTablePage() {
		location = null;
		parentPage = null;
	}

	public DanceEventAdminTablePage(TablePage<?> parentPage, Location location) {
		this.location = location;
		this.parentPage = parentPage;
	}
	
	public void setLocation(Location location) {
		this.location = location;
		refresh();
	}

	@Override
	protected Object[] getColumns() {
		if (location == null) {
			return new Object[] { DanceEvent.$.date, DanceEvent.$.location.name, DanceEvent.$.line, DanceEvent.$.from, DanceEvent.$.status };
		} else {
			return new Object[] { DanceEvent.$.date, DanceEvent.$.line, DanceEvent.$.from, DanceEvent.$.status };
		}
	}

	@Override
	protected boolean allowMultiselect() {
		return true;
	}
	
	@Override
	public List<Action> getTableActions() {
		return Arrays.asList(new TableNewObjectEditor(), new DanceEventTableEditor(), new DanceEventDeleteAction(), new DanceEventCopyEditor());
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
		
		@Override
		protected DanceEvent save(DanceEvent object) {
			return DanceEventAdminTablePage.this.save(object);
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
		
		@Override
		protected DanceEvent save(DanceEvent object) {
			return DanceEventAdminTablePage.this.save(object);
		}
	}
	
	private class DanceEventDeleteAction extends DeleteDetailAction {
		@Override
		public void run() {
			super.run();
			if (parentPage != null) {
				parentPage.refresh();
			}
		}
	}
	
	@Override
	protected List<DanceEvent> load() {
		Order order;
		if (location == null) {
			order = By.ALL.order(DanceEvent.$.date).order(DanceEvent.$.location);
		} else {
			order = By.field(DanceEvent.$.location, location).order(DanceEvent.$.date);
		}
		return Backend.find(DanceEvent.class, order);
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
	protected DanceEvent save(DanceEvent event) {
		DanceEvent danceEvent = super.save(event);
		if (parentPage != null) {
			parentPage.refresh();
		}
		return danceEvent;
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