package ch.openech.flottesohle.frontend;

import java.util.Map;
import java.util.TreeSet;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.action.Action;
import org.minimalj.util.StringUtils;
import org.minimalj.util.resources.Resources;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.DanceEventProviders;
import ch.openech.flottesohle.backend.EventsUpdateTransaction;
import ch.openech.flottesohle.model.Location;

public class EventLocationUpdateAction extends Action {

	private final Location location;

	public EventLocationUpdateAction(Location location) {
		this.location = location;
	}
	
	@Override
	protected String getResourceName() {
		return Resources.getResourceName(EventUpdateAction.class);
	}
	
	@Override
	public void run() {
		TreeSet<String> providerNames = new TreeSet<>();
		for (Map.Entry<String, DanceEventProvider> entry : DanceEventProviders.PROVIDERS.entrySet()) {
			if (StringUtils.equals(entry.getValue().getLocationName(), location.name)) {
				providerNames.add(entry.getKey());
			}
		}
		if (!providerNames.isEmpty()) {
			Backend.execute(new EventsUpdateTransaction(providerNames));
			Frontend.show(new DanceEventLocationTablePage(location));
		} else {
			Frontend.showMessage("Keinen Generator gefunden");
		}
	}
}
