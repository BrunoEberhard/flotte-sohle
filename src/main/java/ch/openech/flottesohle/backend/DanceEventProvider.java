package ch.openech.flottesohle.backend;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.backend.repository.ReadCriteriaTransaction;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldCriteria;
import org.minimalj.repository.query.Query;
import org.minimalj.transaction.Transaction;
import org.minimalj.util.CloneHelper;
import org.minimalj.util.EqualsHelper;

import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.DanceEventProviderStatus;
import ch.openech.flottesohle.model.DeeJay;
import ch.openech.flottesohle.model.Location;

public abstract class DanceEventProvider implements Transaction<Void> {
	private static final long serialVersionUID = 1L;

	protected static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";

	// Format für "29. Januar 2022"
	public static final DateTimeFormatter LONG_DATE_FORMAT = DateTimeFormatter.ofPattern("d. LLLL yyyy", Locale.GERMAN);

	protected Location location;

	public static <T> Optional<T> findOne(Class<T> clazz, Query query) {
		return Backend.execute(new ReadCriteriaTransaction<T>(clazz, query)).stream().findAny();
	}

	public String getLocationName() {
		Location location = createLocation();
		return location != null ? location.name : null;
	}

	protected Location save(Location location) {
		Optional<Location> existingLocation = findOne(Location.class,
				new FieldCriteria(Location.$.name, location.name));
		if (existingLocation.isPresent()) {
			location.id = existingLocation.get().id;
		}
		return Backend.save(location);
	}

	@Override
	public Void execute() {
		try {
			// Ohne Reload würden Änderungen vom Admin gleich überschrieben
			location = repository().read(Location.class, location.id);
			location.providerStatus.eventUpdateCounter.clear();
			location.providerStatus.lastRun = LocalDateTime.now();
			CloneHelper.deepCopy(updateEvents(), location.providerStatus.eventUpdateCounter);
			if (location.providerStatus.eventUpdateCounter.newEvents > 0 || location.providerStatus.eventUpdateCounter.updatedEvents > 0) {
				location.providerStatus.lastChange = LocalDateTime.now();
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			location.providerStatus.eventUpdateCounter.exception = sw.toString();
		}
		repository().update(location);
		return null;
	}

	public abstract EventUpdateCounter updateEvents() throws Exception;

	protected abstract Location createLocation();

	public String getName() {
		String className = getClass().getSimpleName();
		if (className.endsWith("Crawler")) {
			return addSpaces(className.substring(0, className.length() - 7)) + " Websuche";
		} else if (className.endsWith("Import")) {
			return addSpaces(className.substring(0, className.length() - 6)) + " Import";
		} else if (className.endsWith("Rule")) {
			return addSpaces(className.substring(0, className.length() - 4)) + " Regel";
		} else if (className.endsWith("Consumer")) {
			return addSpaces(className.substring(0, className.length() - 8)) + " Abfrage";
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Location getLocation() {
		return location;
	}

	public void install(boolean active) {
		location = save(createLocation());
		if (location.providerStatus == null) {
			location.providerStatus = new DanceEventProviderStatus();
			location.providerStatus.active = active;
			location = Backend.save(location);
		}
		DanceEventProviders.PROVIDERS_BY_LOCATION_ID.put(location.id, this);
	}
	
	private String addSpaces(String s) {
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (Character.isUpperCase(c) && sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(c);
		}
		return sb.toString();
	}

	protected void save(DanceEvent event, EventUpdateCounter result) {
		try {
			if (event.id == null) {
				Backend.insert(event);
				result.newEvents++;
			} else {
				DanceEvent existing = Backend.read(DanceEvent.class, event.id);
				if (existing != null) {
					if (!EqualsHelper.equals(existing, event)) {
						Backend.update(event);
						result.updatedEvents++;
					}
				} else {
					Backend.insert(event);
					result.newEvents++;
				}
			}
		} catch (Exception x) {
			result.failedEvents++;
		}
	}

	protected DeeJay getDeeJay(String djText) {
		return getDeeJay(djText, null);
	}

	protected DeeJay getDeeJay(String djText, String url) {
		Optional<DeeJay> deeJay = findOne(DeeJay.class, By.field(DeeJay.$.name, djText));
		if (deeJay.isPresent()) {
			return deeJay.get();
		} else {
			DeeJay newDeeJay = new DeeJay();
			newDeeJay.name = djText;
			newDeeJay.url = url;
			return Backend.save(newDeeJay);
		}
	}
}