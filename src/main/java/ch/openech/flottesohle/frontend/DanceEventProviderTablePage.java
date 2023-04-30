package ch.openech.flottesohle.frontend;

import static ch.openech.flottesohle.model.DanceEventProviderData.$;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.page.TablePage;
import org.minimalj.model.Column;
import org.minimalj.model.Keys;
import org.minimalj.repository.query.By;
import org.minimalj.transaction.Role;
import org.minimalj.util.resources.Resources;

import ch.openech.flottesohle.model.DanceEventProviderData;

@Role("admin")
public class DanceEventProviderTablePage extends TablePage<DanceEventProviderData> {

	public static final Object[] COLUMNS = new Object[] { $.name, $.lastRun, new ColumnActive(), $.eventUpdateCounter.newEvents,
			$.eventUpdateCounter.updatedEvents, $.eventUpdateCounter.skippedEditedEvents,
			$.eventUpdateCounter.skippedBlockedEvents, $.eventUpdateCounter.failedEvents };

	public static class ColumnActive extends Column {

		public ColumnActive() {
			super($.active);
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
		public CharSequence render(Object rowObject, Object value) {
			return Boolean.TRUE.equals(value) ? "Aktiv" : null;
		}
	}
	
	@Override
	protected Object[] getColumns() {
		return COLUMNS;
	}

	@Override
	protected List<DanceEventProviderData> load() {
		return Backend.find(DanceEventProviderData.class, By.all().order($.name));
	}

	@Override
	protected boolean allowMultiselect() {
		return true;
	}
	
	@Override
	public List<Action> getTableActions() {
		return List.of(new ActivateProviderAction(), new InactivateProviderAction(), new StartProviderAction());
	}

	private class ActivateProviderAction extends AbstractObjectsAction<DanceEventProviderData> {

		@Override
		public void run() {
			getSelectedObjects().stream().filter(o -> !o.active).forEach(data -> {
				data.active = true;
				Backend.update(data);
			});
			refresh();
		}
		
		@Override
		protected boolean accept(List<DanceEventProviderData> selectedObjects) {
			return selectedObjects.stream().anyMatch(data -> !data.active);
		}
	}

	private class InactivateProviderAction extends AbstractObjectsAction<DanceEventProviderData> {

		@Override
		public void run() {
			getSelectedObjects().stream().filter(o -> o.active).forEach(data -> {
				data.active = false;
				Backend.update(data);
			});
			refresh();
		}
		
		@Override
		protected boolean accept(List<DanceEventProviderData> selectedObjects) {
			return selectedObjects.stream().anyMatch(data -> data.active);
		}
		
	}
	
	private class StartProviderAction extends AbstractObjectsAction<DanceEventProviderData> {

		@Override
		public void run() {
			getSelectedObjects().stream().forEach(data -> data.run());
			refresh();
		}
	}
}
