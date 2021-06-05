package ch.openech.flottesohle.frontend;

import java.time.LocalDate;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.editor.Editor.NewObjectEditor;
import org.minimalj.frontend.form.Form;
import org.minimalj.model.Keys;
import org.minimalj.model.validation.InvalidValues;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.transaction.Role;

import ch.openech.flottesohle.frontend.EventHousekeepingAction.EventHousekeepingViewModel;
import ch.openech.flottesohle.model.DanceEvent;

@Role("admin")
public class EventHousekeepingAction extends NewObjectEditor<EventHousekeepingViewModel> {

	@Override
	public Form<EventHousekeepingViewModel> createForm() {
        Form<EventHousekeepingViewModel> form = new Form<>(Form.EDITABLE);
		form.line(EventHousekeepingViewModel.$.date);
		form.line(EventHousekeepingViewModel.$.getCount());
		return form;
	}

	public static class EventHousekeepingViewModel {
		public static final EventHousekeepingViewModel $ = Keys.of(EventHousekeepingViewModel.class);

		public LocalDate date = LocalDate.now().minusDays(1);

		public Long getCount() {
			if (Keys.isKeyObject(this)) {
				return Keys.methodOf(this, "count", $.date);
			}

			if (date != null && !InvalidValues.isInvalid(date)) {
				return Backend.count(DanceEvent.class, By.field(DanceEvent.$.date, FieldOperator.lessOrEqual, date));
			} else {
				return null;
			}
		}
	}

	@Override
	protected EventHousekeepingViewModel save(EventHousekeepingViewModel selected) {
		Backend.delete(DanceEvent.class, By.field(DanceEvent.$.date, FieldOperator.lessOrEqual, selected.date));
		return null;
	}

}
