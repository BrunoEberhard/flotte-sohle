package ch.flottesohle.backend;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.minimalj.backend.Backend;
import org.minimalj.model.Keys;
import org.minimalj.model.annotation.AnnotationUtil;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldCriteria;
import org.minimalj.util.CsvReader;
import org.minimalj.util.EqualsHelper;

import ch.flottesohle.model.AdminLog;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.DeeJay;
import ch.flottesohle.model.EventStatus;
import ch.flottesohle.model.Location;

public abstract class LocationProvider {
	protected static final int ADMIN_LOG_MSG_SIZE = AnnotationUtil.getSize(Keys.getProperty(AdminLog.$.msg));

	private static final long serialVersionUID = 1L;

	// Format für "29. Januar 2022"
	public static final DateTimeFormatter LONG_DATE_FORMAT = DateTimeFormatter.ofPattern("d. LLLL yyyy", Locale.GERMAN);

	protected Location location;

	public String getLocationName() {
		Location location = createLocation();
		return location != null ? location.name : null;
	}

	protected Location save(Location location) {
		Location existingLocation = Backend.findOne(Location.class,
				new FieldCriteria(Location.$.name, location.name));
		if (existingLocation != null) {
			location.id = existingLocation.id;
		}
		return Backend.save(location);
	}

	protected abstract Location createLocation();

	public String getCsvName() {
		String className = getClass().getSimpleName();
		if (className.endsWith("Crawler")) {
			return className.substring(0, className.length() - 7) + ".csv";
		} else if (className.endsWith("Import")) {
			return className.substring(0, className.length() - 6) + ".csv";
		} else if (className.endsWith("Rule")) {
			return className.substring(0, className.length() - 4) + ".csv";
		} else if (className.endsWith("Consumer")) {
			return className.substring(0, className.length() - 8) + ".csv";
		} else {
			throw new IllegalArgumentException();
		}
	}

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
		importCsv();
	}
	
	private void importCsv() {
		InputStream inputStream = getClass().getResourceAsStream("/data/" + getCsvName());
		if (inputStream != null) {
			importCsv(inputStream);
		}
	}

	private void importCsv(InputStream inputStream) {
		CsvReader reader = new CsvReader(inputStream);
		for (DanceEvent event : reader.readValues(DanceEvent.class)) {
			DanceEvent existingEvent = Backend.findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, event.date)));
			if (existingEvent == null && event.date.isAfter(LocalDate.now())) {
				saveImportedEvent(event);
			}
		}
	}

	protected void saveImportedEvent(DanceEvent event) {
		event.status = EventStatus.generated;
		event.location = location;
		Backend.save(event);
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
		DeeJay deeJay = Backend.findOne(DeeJay.class, By.field(DeeJay.$.name, djText));
		if (deeJay != null) {
			return deeJay;
		} else {
			DeeJay newDeeJay = new DeeJay();
			newDeeJay.name = djText;
			newDeeJay.url = url;
			return Backend.save(newDeeJay);
		}
	}
}