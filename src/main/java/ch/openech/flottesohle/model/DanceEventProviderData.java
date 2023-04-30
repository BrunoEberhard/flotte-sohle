package ch.openech.flottesohle.model;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.minimalj.backend.Backend;
import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Size;
import org.minimalj.util.CloneHelper;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.DanceEventProviders;
import ch.openech.flottesohle.backend.EventUpdateCounter;

public class DanceEventProviderData {

	public static final DanceEventProviderData $ = Keys.of(DanceEventProviderData.class);

	private static final Logger LOG = Logger.getLogger(DanceEventProviderData.class.getName());

	public Object id;

	@Size(64)
	public String name;

	public Boolean active = false;

	@Size(Size.TIME_WITH_SECONDS)
	public LocalDateTime lastRun;

	public final EventUpdateCounter eventUpdateCounter = new EventUpdateCounter();

	public void run() {
		DanceEventProvider provider = DanceEventProviders.PROVIDERS.get(name);
		if (provider != null) {
			lastRun = LocalDateTime.now();
			try {
				var counter = provider.execute();
				CloneHelper.deepCopy(counter, eventUpdateCounter);
			} catch (Exception x) {
				CloneHelper.deepCopy(new EventUpdateCounter(), eventUpdateCounter);
				eventUpdateCounter.exception = x.getMessage();
			}
			Backend.update(this);
		} else {
			LOG.warning("Provider not fount: " + name);
		}
	}

}
