package ch.openech.flottesohle.frontend;

import static ch.openech.flottesohle.model.DanceEventProviderStatus.$;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.PasswordFormElement;
import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.model.Column;
import org.minimalj.model.Keys;
import org.minimalj.repository.query.By;
import org.minimalj.security.Subject;
import org.minimalj.transaction.Role;
import org.minimalj.transaction.Transaction;
import org.minimalj.util.resources.Resources;

import ch.openech.flottesohle.FlotteSohleRoles;
import ch.openech.flottesohle.backend.DanceEventProviders;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.FlotteSohleUser;
import ch.openech.flottesohle.model.Location;
import ch.openech.flottesohle.model.Location.Closing;

@Role({"admin", "multiLocation"})
public class LocationAdminTablePage extends SimpleTableEditorPage<Location> {

	private DanceEventAdminTablePage eventTablePage;
	
	@Override
	protected Object[] getColumns() {
		return new Object[] { Location.$.name, Location.$.city, new ColumnActive(), Location.$.events, Location.$.maxDate, Location.$.providerStatus.lastRun, Location.$.providerStatus.lastChange, Location.$.comment, Location.$.getClosings() };
	}
	
	public static class ColumnActive extends Column<Location, Boolean> {

		public ColumnActive() {
			super(Location.$.providerStatus.active);
		}
		
		@Override
		public String getHeader() {
			return Resources.getPropertyName(Keys.getProperty($.active));
		}
		
		@Override
		public ColumnAlignment getAlignment() {
			return ColumnAlignment.center;
		}
		
		@Override
		public CharSequence render(Location rowObject, Boolean value) {
			if (Boolean.TRUE.equals(value)) {
				return "Aktiv";
			} else if (Boolean.FALSE.equals(value)) {
				return "Inaktiv";
			} else {
				return "Manuell";
			}
		}
	}

	@Override
	protected List<Location> load() {
		return Backend.execute(new LocationLoader());
	}
	
	public static class LocationLoader implements Transaction<List<Location>> {
		private static final long serialVersionUID = 1L;

		@Override
		public List<Location> execute() {
			String query = "SELECT l.*, " + //
					"(SELECT COUNT(*) FROM " + $(DanceEvent.class) + " e WHERE e." + $(DanceEvent.$.location) + " = l.id AND " + $(DanceEvent.$.date) + " >= sysdate) AS " + $(Location.$.events) + ", " +
					"(SELECT MAX(" + $(DanceEvent.$.date) + ") FROM " + $(DanceEvent.class) + " e WHERE e." + $(DanceEvent.$.location) + " = l.id) AS " + $(Location.$.maxDate) + " " +
						"FROM " + $(Location.class) + " l ORDER BY l." + $(Location.$.name);
			return sqlRepository().find(Location.class, query, 10000);
		}
	}

	@Override
	public List<Action> getTableActions() {
		List<Action> actions = new ArrayList<>(super.getTableActions());
		if (Subject.currentHasRole(FlotteSohleRoles.admin.name())) {
			actions.add(new LocationUserTableAction());
			actions.add(new LocationClosingTableAction());
			actions.add(new StartProviderAction());
			actions.add(new ActivateProviderAction());
			actions.add(new DeactivateProviderAction());
		}
		return actions;
	}

	@Override
	public void action(Location location) {
		showDetail(location);
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
	
	@Override
	protected Page getDetailPage(Location location) {
		if (eventTablePage == null) {
			return eventTablePage = new DanceEventAdminTablePage(this, location);
		} else {
			eventTablePage.setLocation(location);
			return eventTablePage;
		}
	}
	
	private class LocationUserTableAction extends Action implements ObjectsAction<Location> {
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
			return UserTablePage.COLUMNS;
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
	
	private class LocationClosingTableAction extends Action implements ObjectsAction<Location> {
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
	

	private class ActivateProviderAction extends AbstractObjectsAction<Location> {

		@Override
		public void run() {
			getSelectedObjects().stream() //
					.filter(o -> o.providerStatus != null).filter(o -> !o.providerStatus.active) //
					.forEach(o -> {
						o.providerStatus.active = true;
						Backend.update(o);
					});
			refresh();
		}

		@Override
		protected boolean accept(List<Location> selectedObjects) {
			return selectedObjects.stream().map(o -> o.providerStatus).filter(o -> o != null)
					.anyMatch(data -> !data.active);
		}
	}

	private class DeactivateProviderAction extends AbstractObjectsAction<Location> {

		@Override
		public void run() {
			getSelectedObjects().stream() //
					.filter(o -> o.providerStatus != null).filter(o -> o.providerStatus.active) //
					.forEach(o -> {
						o.providerStatus.active = false;
						Backend.update(o);
					});
			refresh();
		}

		@Override
		protected boolean accept(List<Location> selectedObjects) {
			return selectedObjects.stream().map(o -> o.providerStatus).filter(o -> o != null)
					.anyMatch(data -> data.active);
		}

	}

	private class StartProviderAction extends AbstractObjectsAction<Location> {

		@Override
		public void run() {
			getSelectedObjects().stream().map(location -> DanceEventProviders.PROVIDERS_BY_LOCATION_ID.get(location.id))
					.filter(p -> p != null).forEach(p -> Backend.execute(p));
			refresh();
		}

		@Override
		protected boolean accept(List<Location> selectedObjects) {
			return selectedObjects.stream().map(o -> o.providerStatus).filter(o -> o != null)
					.anyMatch(data -> data.active);
		}
	}

}
