package ch.openech.flottesohle.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.page.TablePage;
import org.minimalj.repository.query.By;

import ch.openech.flottesohle.model.Location;

public class LocationTablePage extends TablePage<Location> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { Location.$.name, Location.$.city, Location.$.url };
	}
	
	@Override
	protected List<Location> load() {
		return Backend.find(Location.class, By.ALL.order(Location.$.name));
	}

	@Override
	public void selectionChanged(List<Location> selectedObjects) {
		if (selectedObjects.size() > 0) {
			Frontend.getInstance().showBrowser(selectedObjects.get(0).url);
		}
	}


}
