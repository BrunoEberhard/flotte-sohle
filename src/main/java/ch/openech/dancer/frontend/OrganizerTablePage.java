package ch.openech.dancer.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.Organizer;

public class OrganizerTablePage extends SimpleTableEditorPage<Organizer> {

	private static final Object[] keys = new Object[] { Organizer.$.name, Organizer.$.city };
	
	public OrganizerTablePage() {
		super(keys);
	}
	
	@Override
	protected List<Organizer> load() {
		return Backend.find(Organizer.class, By.ALL);
	}

	@Override
	protected Form<Organizer> createForm(boolean editable, boolean newObject) {
		Form<Organizer> form = new Form<>(editable, 2);
		form.line(Organizer.$.name);
		form.line(Organizer.$.address);
		form.line(Organizer.$.city);
		form.line(Organizer.$.country);
		form.line(Organizer.$.url);
		return form;
	}
	
	@Override
	protected Organizer createObject() {
		Organizer organizer = super.createObject();
		organizer.country = "Schweiz";
		return organizer;
	}
	
}
