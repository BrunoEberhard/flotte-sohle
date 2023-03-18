package ch.openech.flottesohle.frontend;

import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.ReferenceFormElement;
import org.minimalj.frontend.form.element.TextFormElement;

import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.Location;

public class DanceEventForm extends Form<DanceEvent> {

	public DanceEventForm(boolean editable, boolean locationEditable) {
		super(editable, 2, Form.DEFAULT_COLUMN_WIDTH * 3 / 2);
		
		fill(editable, locationEditable && editable, this, DanceEvent.$);
	}
	
	public static void fill(boolean editable, boolean locationEditable, Form<?> form, DanceEvent $) {
		form.line($.date, new TextFormElement($.getDayOfWeek()));
		form.line($.from, $.until);
		// form.line($.header);
		form.line($.line);
		form.line($.description);
		// form.line(new FlyerFormElement($.flyer, editable));
		form.line(locationEditable ? new ReferenceFormElement<>($.location, Location.$.name) : new TextFormElement($.location));
		form.line(new ReferenceFormElement<>($.deeJay));
		form.line($.price, $.priceReduced);
		form.line($.tags);
		form.line($.status);
	}
}
