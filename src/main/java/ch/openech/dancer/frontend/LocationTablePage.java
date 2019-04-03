package ch.openech.dancer.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.Location;

public class LocationTablePage extends SimpleTableEditorPage<Location> {

	private static final Object[] keys = new Object[] { Location.$.name, Location.$.city };
	
	public LocationTablePage() {
		super(keys);
	}
	
	@Override
	protected List<Location> load() {
		return Backend.find(Location.class, By.ALL);
	}

	@Override
	protected Form<Location> createForm(boolean editable, boolean newObject) {
		Form<Location> form = new Form<>(editable, 2);
		form.line(Location.$.name);
		form.line(Location.$.address);
		form.line(Location.$.city);
		form.line(Location.$.region);
		form.line(Location.$.country);
		form.line(Location.$.url);
		return form;
	}
	
	@Override
	protected Location createObject() {
		Location organizer = super.createObject();
		organizer.country = "Schweiz";
		return organizer;
	}
	
}
