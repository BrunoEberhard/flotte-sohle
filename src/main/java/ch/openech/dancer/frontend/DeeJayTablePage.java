package ch.openech.dancer.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.repository.query.By;
import org.minimalj.transaction.Role;

import ch.openech.dancer.model.DeeJay;

@Role("admin")
public class DeeJayTablePage extends SimpleTableEditorPage<DeeJay> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { DeeJay.$.name, DeeJay.$.url };
	}
	
	@Override
	protected List<DeeJay> load() {
		return Backend.find(DeeJay.class, By.ALL);
	}

	@Override
	protected Form<DeeJay> createForm(boolean editable, boolean newObject) {
        Form<DeeJay> form = new Form<>(editable);
		form.line(DeeJay.$.name);
		form.line(DeeJay.$.url);
		return form;
	}
	
}
