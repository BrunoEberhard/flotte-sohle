package ch.openech.flottesohle.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.minimalj.application.Configuration;
import org.minimalj.backend.Backend;
import org.minimalj.transaction.Transaction;

import ch.openech.flottesohle.model.AdminLog;
import ch.openech.flottesohle.model.AdminLog.AdminLogType;

public class EventsUpdateTransaction implements Transaction<List<EventUpdateCounter>> {
	private static final long serialVersionUID = 1L;

	private final TreeSet<String> providerNames;

	public EventsUpdateTransaction(TreeSet<String> providerNames) {
		this.providerNames = providerNames;
	}

	@Override
	public List<EventUpdateCounter> execute() {
		List<EventUpdateCounter> counters = new ArrayList<EventUpdateCounter>();
		for (String providerName : providerNames) {
			DanceEventProvider provider = DanceEventProviders.PROVIDERS.get(providerName);
			if (providerNames.size() > 2 && Configuration.isDevModeActive() && !provider.getClass().getName().endsWith("Rule") && !provider.getClass().getName().endsWith("Import")) {
				// im Dev - Modus nicht alle Veranstalter Seiten abfragen
				continue;
			}
			EventUpdateCounter counter = Backend.execute(provider);
			counter.provider = provider.getName();
			counters.add(counter);
		}
		Collections.sort(counters, Comparator.comparing(counter -> counter.provider));
		Integer inserted = counters.stream().mapToInt(c -> c.newEvents).sum();
		Integer updated = counters.stream().mapToInt(c -> c.updatedEvents).sum();
		Backend.insert(new AdminLog(AdminLogType.IMPORT, "Import: " + inserted + " neue, " + updated + " aktualisierte Anlässe"));
		return counters;
	}

}
