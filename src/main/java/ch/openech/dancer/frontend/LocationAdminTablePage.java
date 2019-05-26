package ch.openech.dancer.frontend;

import java.util.ArrayList;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.repository.query.By;
import org.minimalj.util.CloneHelper;

import ch.openech.dancer.model.Location;

public class LocationAdminTablePage extends SimpleTableEditorPage<Location> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { Location.$.name, Location.$.city };
	}
	
	@Override
	protected List<Location> load() {
		return Backend.find(Location.class, By.ALL.order(Location.$.name));
	}

	@Override
	public List<Action> getTableActions() {
		List<Action> actions = new ArrayList<>(super.getTableActions());
		actions.add(new LocationPasswordEditor());
		return actions;
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
	
	private class LocationPasswordEditor extends AbstractTableEditor implements TableSelectionAction<Location> {
		private transient Location selection;

		public LocationPasswordEditor() {
			selectionChanged(null);
		}

		@Override
		protected Location createObject() {
			return CloneHelper.clone(selection);
		}

		@Override
		protected Location save(Location object) {
			return LocationAdminTablePage.this.save(object, selection);
		}

		@Override
		public void selectionChanged(List<Location> selectedObjects) {
			this.selection = selectedObjects != null && !selectedObjects.isEmpty() ? selectedObjects.get(0) : null;
			setEnabled(selection != null);
		}

		@Override
		protected Form<Location> createForm() {
			Form<Location> form = new Form<>();
			form.line(Location.$.password);
			return form;
		}
	}
}
