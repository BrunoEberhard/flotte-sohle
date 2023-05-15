package ch.flottesohle.backend;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.backend.repository.ReadCriteriaTransaction;
import org.minimalj.repository.query.Query;
import org.minimalj.transaction.Transaction;

import ch.flottesohle.model.AdminLog;
import ch.flottesohle.model.AdminLog.AdminLogType;
import ch.flottesohle.model.ImportStatus;
import ch.flottesohle.model.Location;

public abstract class DanceEventProvider extends LocationProvider implements Transaction<Void> {
	private static final long serialVersionUID = 1L;

	protected static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";

	public void install(boolean active) {
		super.install(active);
		if (location.importStatus == null) {
			location.importStatus = new ImportStatus();
			location.importStatus.active = active;
			location = Backend.save(location);
		}
		DanceEventProviders.PROVIDERS_BY_LOCATION_ID.put(location.id, this);
	}
	
	@Override
	public Void execute() {
		AdminLog adminLog = new AdminLog();
		adminLog.logType = AdminLogType.IMPORT;
		try {
			// Ohne Reload würden Änderungen vom Admin gleich überschrieben
			location = repository().read(Location.class, location.id);
			EventUpdateCounter eventUpdateCounter = updateEvents();
			if (eventUpdateCounter.newEvents > 0 || eventUpdateCounter.updatedEvents > 0) {
				location.importStatus.lastChange = LocalDateTime.now();
			}
			adminLog.msg = eventUpdateCounter.newEvents + " neue Anlässe" + " " + eventUpdateCounter.updatedEvents + " aktualisiert Anlässe";
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			adminLog.msg = sw.toString();
			if (adminLog.msg.length() > ADMIN_LOG_MSG_SIZE) {
				adminLog.msg = adminLog.msg.substring(0, ADMIN_LOG_MSG_SIZE);
			}
		}
		Backend.insert(adminLog);
		location.importStatus.lastRun = LocalDateTime.now();
		repository().update(location);
		return null;
	}
	
	public static <T> Optional<T> findOne(Class<T> clazz, Query query) {
		return Backend.execute(new ReadCriteriaTransaction<T>(clazz, query)).stream().findAny();
	}

	public abstract EventUpdateCounter updateEvents() throws Exception;

	protected abstract Location createLocation();

}