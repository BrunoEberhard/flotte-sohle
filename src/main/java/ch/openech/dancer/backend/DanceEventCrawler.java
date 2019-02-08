package ch.openech.dancer.backend;

import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.backend.repository.ReadCriteriaTransaction;
import org.minimalj.repository.query.FieldCriteria;
import org.minimalj.repository.query.Query;
import org.minimalj.transaction.Transaction;

import ch.openech.dancer.model.Location;

public abstract class DanceEventCrawler implements Transaction<Integer> {
	private static final long serialVersionUID = 1L;

	protected static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";

	protected transient Location location;

	public static <T> Optional<T> findOne(Class<T> clazz, Query query) {
		return Backend.execute(new ReadCriteriaTransaction<T>(clazz, query)).stream().findAny();
	}

	protected void initData() {
		location = createLocation();
		Optional<Location> existingLocation = findOne(Location.class, new FieldCriteria(Location.$.name, location.name));
		location = existingLocation.orElseGet(() -> Backend.save(location));

//		organizer = createOrganizer();
//		Optional<Organizer> existingOrganizer = findOne(Organizer.class, new FieldCriteria(Organizer.$.name, organizer.name));
//		organizer = existingOrganizer.orElseGet(() -> Backend.save(organizer));
	}

	@Override
	public Integer execute() {
		initData();
		return crawlEvents();
	}
	
	public abstract int crawlEvents();

	protected abstract Location createLocation();

}