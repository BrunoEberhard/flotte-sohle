package ch.openech.dancer.frontend;

import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.ObjectPage;

import ch.openech.dancer.model.DanceEvent;

public class DanceEventPage extends ObjectPage<DanceEvent> {

	public DanceEventPage(Object id) {
		super(DanceEvent.class, id);
	}

	@Override
	protected Form<DanceEvent> createForm() {
		return new DanceEventForm(false);
	}

}
