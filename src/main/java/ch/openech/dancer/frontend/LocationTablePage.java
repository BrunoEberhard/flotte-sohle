package ch.openech.dancer.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.page.TablePage;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.Location;

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
	public void action(Location selectedObject) {
		Frontend.getInstance().showBrowser(selectedObject.url);
	}

}
