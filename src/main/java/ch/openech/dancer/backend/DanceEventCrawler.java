package ch.openech.dancer.backend;

import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.backend.repository.ReadCriteriaTransaction;
import org.minimalj.repository.query.FieldCriteria;
import org.minimalj.repository.query.Query;
import org.minimalj.transaction.Transaction;

import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Organizer;

abstract class DanceEventCrawler implements Transaction<Integer> {
	private static final long serialVersionUID = 1L;

	protected static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";

	protected transient Organizer organizer;
	protected transient Location location;

	public static <T> Optional<T> findOne(Class<T> clazz, Query query) {
		return Backend.execute(new ReadCriteriaTransaction<T>(clazz, query)).stream().findAny();
	}

	protected void initData() {
		location = createLocation();
		Optional<Location> locations = findOne(Location.class, new FieldCriteria(Location.$.name, location.name));
		if (!locations.isPresent()) {
			location = Backend.save(createLocation());
		}

		organizer = createOrganizer();
		Optional<Organizer> organizers = findOne(Organizer.class, new FieldCriteria(Organizer.$.name, organizer.name));
		if (!organizers.isPresent()) {
			organizer = Backend.save(createOrganizer());
		}
	}

	@Override
	public Integer execute() {
		initData();
		return crawlEvents();
	}
	
	public abstract int crawlEvents();

	protected abstract Organizer createOrganizer();

	protected abstract Location createLocation();

}