package ch.openech.dancer.frontend;

import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.FormLookupFormElement;
import org.minimalj.model.properties.PropertyInterface;

import ch.openech.dancer.model.Recur;

public class RecurFormElement extends FormLookupFormElement<Recur> {

	public RecurFormElement(PropertyInterface property, boolean editable) {
		super(property, editable);
	}

	@Override
	protected Form<Recur> createForm() {
		Form<Recur> form = new Form<>();
		form.line(Recur.$.interval);
		form.line(Recur.$.recurFrequency);
		form.line(Recur.$.until);
		form.line(Recur.$.count);
		return form;
	}

}
