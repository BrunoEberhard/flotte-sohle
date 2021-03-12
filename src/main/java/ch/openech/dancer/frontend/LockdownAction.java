package ch.openech.dancer.frontend;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.editor.Editor.SimpleEditor;
import org.minimalj.frontend.form.Form;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Location.Closing;

public class LockdownAction extends SimpleEditor<Closing> {

	@Override
	protected Closing createObject() {
		Closing closing = new Closing();
		closing.reason = "Lockdown";
		return closing;
	}
	
	@Override
	protected Closing save(Closing closing) {
		L: for (Location location : Backend.find(Location.class, By.ALL)) {
			for (Closing c : location.closings) {
				if (c.overlaps(closing)) {
					continue L;
				}
			}
			location.closings.add(closing);
			Backend.update(location);
		}
		return closing;
	}
	
	@Override
	protected Form<Closing> createForm() {
		Form<Closing> form = new Form<>(2);
		form.line(Closing.$.from, Closing.$.until);
		form.line(Closing.$.reason);
		return form;
	}
}
