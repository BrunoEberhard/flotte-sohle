package ch.openech.dancer.frontend;

import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.ReferenceFormElement;
import org.minimalj.frontend.form.element.TextFormElement;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;

public class DanceEventForm extends Form<DanceEvent> {

	public DanceEventForm(boolean editable) {
		super(editable, 2);
		
		fill(editable, this, DanceEvent.$);
	}
	
	public static void fill(boolean editable, Form<?> form, DanceEvent $) {
		form.line($.date, new TextFormElement($.getDayOfWeek()));
		form.line($.from, $.until);
		form.line($.title);
		form.line($.description);
		// form.line(new FlyerFormElement($.flyer, editable));
		form.line(editable ? new ReferenceFormElement<>($.location, Location.$.name) : new TextFormElement($.location));
		form.line($.tags);
		form.line($.status);
	}
}
