package ch.flottesohle.frontend;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.editor.Editor.SimpleEditor;
import org.minimalj.frontend.form.Form;

import ch.flottesohle.model.Location;

public class LocationEditor extends SimpleEditor<Location> {
	public final Location location;
	
	public LocationEditor(Location location) {
		this.location = location;
	}
	
	@Override
	protected Location createObject() {
		return location;
	}

	@Override
	protected Form<Location> createForm() {
		return new LocationForm(Form.EDITABLE);
	}

	@Override
	protected Location save(Location location) {
		return Backend.save(location);
	}
	
	public static class LocationForm extends Form<Location> {

		public LocationForm(boolean editable) {
			super(editable, 1, Form.DEFAULT_COLUMN_WIDTH * 5 / 3);
			line(Location.$.name);
			line(Location.$.address);
			line(Location.$.city);
			line(Location.$.region);
			line(Location.$.country);
			line(Location.$.url);
			line(Location.$.comment);
		}
	}

}
