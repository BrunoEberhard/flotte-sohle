package ch.openech.dancer.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.PasswordFormElement;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.repository.query.By;
import org.minimalj.security.Subject;
import org.minimalj.transaction.Role;

import ch.openech.dancer.FlotteSohleRoles;
import ch.openech.dancer.model.FlotteSohleUser;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Location.Closing;

@Role({"admin", "multiLocation"})
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
		if (Subject.currentHasRole(FlotteSohleRoles.admin.name())) {
			actions.add(new LocationUserTableAction());
		}
		actions.add(new LocationClosingTableAction());
		return actions;
	}

	@Override
	protected Form<Location> createForm(boolean editable, boolean newObject) {
		return new LocationEditor.LocationForm(editable);
	}
	
	@Override
	protected Location createObject() {
		Location organizer = super.createObject();
		organizer.country = "Schweiz";
		return organizer;
	}
	
	private class LocationUserTableAction extends Action implements TableSelectionAction<Location> {
		private List<Location> selectedObjects;
		private LocationUserTablePage table;
		
		public LocationUserTableAction() {
			setEnabled(false);
		}
		
		@Override
		public void run() {
			if (table == null) {
				table = new LocationUserTablePage();
			}
			table.setObjects(selectedObjects);
			Frontend.showDetail(LocationAdminTablePage.this, table);
			
		}

		@Override
		public void selectionChanged(List<Location> selectedObjects) {
			this.selectedObjects = selectedObjects;
			if (table != null && Frontend.isDetailShown(table)) {
				table.setObjects(selectedObjects);
			}
			setEnabled(!selectedObjects.isEmpty());
		}
		
	}
	
	private static class LocationUserTablePage extends SimpleTableEditorPage<FlotteSohleUser> implements ChangeableDetailPage<Location> {
		private List<Location> locations;
		private List<Object> locationIds;
		
		@Override
		protected Object[] getColumns() {
			return new Object[] { FlotteSohleUser.$.name };
		}

		@Override
		protected List<FlotteSohleUser> load() {
			List<FlotteSohleUser> users = Backend.find(FlotteSohleUser.class, By.ALL);
			return users.stream().filter(u -> u.locations.stream().anyMatch(l -> locationIds.contains(l.id))).collect(Collectors.toList());
		}

		@Override
		protected FlotteSohleUser createObject() {
			FlotteSohleUser user = new FlotteSohleUser();
			user.multiLocation = false;
			user.locations.addAll(locations);
			return user;
		}
		
		@Override
		protected Form<FlotteSohleUser> createForm(boolean editable, boolean newObject) {
			Form<FlotteSohleUser> form = new Form<>(editable, 2);
			form.line(FlotteSohleUser.$.email);
			form.line(FlotteSohleUser.$.vorname, FlotteSohleUser.$.name);
			form.line(Form.readonly(FlotteSohleUser.$.multiLocation));
			form.line(new PasswordFormElement(FlotteSohleUser.$.password));
			return form;
		}

		@Override
		public void setObjects(List<Location> objects) {
			this.locations = objects;
			this.locationIds = objects.stream().map(l -> l.id).collect(Collectors.toList());
			refresh();
		}
		
	}
	
//	private class LocationUserEditor extends AbstractTableEditor implements TableSelectionAction<FlotteSohleUser> {
//		private transient FlotteSohleUser selection;
//
//		public LocationUserEditor() {
//			selectionChanged(null);
//		}
//
//		@Override
//		protected Location createObject() {
//			FlotteSohleUser user = Backend.find(FlotteSohleUser.class, By.field(FlotteSohleUser.$.location, this.selection));
//			
//
//			
//			return CloneHelper.clone(selection);
//		}
//
//		@Override
//		protected FlotteSohleUser save(FlotteSohleUser object) {
//			return LocationAdminTablePage.this.save(object, selection);
//		}
//
//		@Override
//		public void selectionChanged(List<FlotteSohleUser> selectedObjects) {
//			this.selection = selectedObjects != null && !selectedObjects.isEmpty() ? selectedObjects.get(0) : null;
//			setEnabled(selection != null);
//		}
//
//		@Override
//		protected Form<Location> createForm() {
//			Form<Location> form = new Form<>();
//			form.line(Location.$.password);
//			return form;
//		}
//	}
	
	private class LocationClosingTableAction extends Action implements TableSelectionAction<Location> {
		private List<Location> selectedObjects;
		private LocationClosingTablePage table;
		
		public LocationClosingTableAction() {
			setEnabled(false);
		}
		
		@Override
		public void run() {
			if (table == null) {
				table = new LocationClosingTablePage();
			}
			table.setObjects(selectedObjects);
			Frontend.showDetail(LocationAdminTablePage.this, table);
			
		}

		@Override
		public void selectionChanged(List<Location> selectedObjects) {
			this.selectedObjects = selectedObjects;
			if (table != null && Frontend.isDetailShown(table)) {
				table.setObjects(selectedObjects);
			}
			setEnabled(!selectedObjects.isEmpty());
		}
		
	}
	
	public static class LocationClosingTablePage extends SimpleTableEditorPage<Closing> implements ChangeableDetailPage<Location> {
		private List<Location> locations;
		private List<Closing> closings;
		
		public LocationClosingTablePage() {
			// nothing
		}

		public LocationClosingTablePage(Location location) {
			locations = List.of(location);
			closings = location.closings;
		}

		@Override
		protected Object[] getColumns() {
			return new Object[] { Closing.$.from, Closing.$.until, Closing.$.reason };
		}

		@Override
		protected List<Closing> load() {
			return closings;
		}

		@Override
		protected Form<Closing> createForm(boolean editable, boolean newObject) {
			Form<Closing> form = new Form<>(editable, 2);
			form.line(Closing.$.from, Closing.$.until);
			form.line(Closing.$.reason);
			return form;
		}

		@Override
		protected Closing save(Closing closing) {
			for (Location l : locations) {
				int index = l.closings.indexOf(closing);
				if (index >= 0) {
					l.closings.set(index, closing);
				} else {
					l.closings.add(closing);
				}
				Backend.update(l);
			}
			setObjects(locations);
			return closing;
		}
		
		@Override
		protected void delete(List<Closing> selectedObjects) {
			for (Location l : locations) {
				if (l.closings.removeAll(selectedObjects)) {
					Backend.update(l);
				}
			}
			// refresh
			setObjects(locations);
		}
		
		@Override
		public void setObjects(List<Location> locations) {
			this.locations = locations;
			closings = locations.stream().flatMap(l -> l.closings.stream()).collect(Collectors.toList());
			refresh();
		}
		
	}
}
