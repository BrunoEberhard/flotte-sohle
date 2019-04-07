package ch.openech.dancer.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.editor.Editor.SimpleEditor;
import org.minimalj.frontend.form.Form;
import org.minimalj.model.Keys;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;

public class CheckUnpublishedEventsAction extends SimpleEditor<CheckUnpublishedEventsAction.CheckUnpublishedEventsModel> {

	private CheckUnpublishedEventsModel model = new CheckUnpublishedEventsModel();
	private List<DanceEvent> unpublishedEvents;

	public CheckUnpublishedEventsAction() {

	}

	@Override
	protected CheckUnpublishedEventsModel createObject() {
		unpublishedEvents = Backend.find(DanceEvent.class, By.field(DanceEvent.$.status, EventStatus.generated)
				.order(DanceEvent.$.date).order(DanceEvent.$.from));
		model.count = unpublishedEvents.size();
		model.pos = 0;
		model.event = unpublishedEvents.get(model.pos);
		return model;
	}

	@Override
	protected Form<CheckUnpublishedEventsModel> createForm() {
		Form<CheckUnpublishedEventsModel> form = new Form<>(Form.EDITABLE, 2);
		DanceEventForm.fill(Form.EDITABLE, true, form, CheckUnpublishedEventsModel.$.event);
		// Minimal-J 1.17.0.0 form.line(form.readonly(CheckUnpublishedEventsModel.$.pos), form.readonly(CheckUnpublishedEventsModel.$.count));
		return form;
	}

	@Override
	protected CheckUnpublishedEventsModel save(CheckUnpublishedEventsModel model) {
		DanceEvent result = Backend.save(model.event);
		showNextEvent();
		return model;
	}

	private void showNextEvent() {
		model.pos++;
		model.event = unpublishedEvents.get(model.pos);
		objectChanged();
	}

	@Override
	protected boolean closeWith(CheckUnpublishedEventsModel model) {
		return model.pos == model.count;
	}

	public static class CheckUnpublishedEventsModel {
		public static final CheckUnpublishedEventsModel $ = Keys.of(CheckUnpublishedEventsModel.class);
		
		public DanceEvent event;
		public Integer count, pos;
	}
}
