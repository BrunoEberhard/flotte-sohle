package ch.openech.dancer.backend;

import java.time.LocalDate;
import java.util.List;

import org.minimalj.model.Model;
import org.minimalj.repository.Repository;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.repository.query.Query;
import org.minimalj.repository.sql.SqlRepository;

import ch.openech.dancer.model.DanceEvent;

/**
 * Hält einen kleinen Cache für die DanceEvent, damit die nicht für jeden
 * Benutzer oder für sogar für jedes Filtern neu geladen werden.
 *
 */
public class DancerRepository implements Repository {

	private final Repository repository;

	private List<DanceEvent> events;
	private long lastLoad = Long.MIN_VALUE;

	public DancerRepository(Model model) {
		this.repository = new SqlRepository(model);
	}

	public <T> T read(Class<T> clazz, Object id) {
		return repository.read(clazz, id);
	}

	public <T> List<T> find(Class<T> clazz, Query query) {
		if (query == EventsQuery.instance) {
			if (events == null || lastLoad < System.currentTimeMillis() - 60 * 1000) {
				events = repository.find(DanceEvent.class, By //
						.field(DanceEvent.$.date, FieldOperator.greaterOrEqual, LocalDate.now()) //
						.order(DanceEvent.$.date));
				// load completely in one transaction
				events = events.subList(0, events.size());
				lastLoad = System.currentTimeMillis();
			}
			return (List<T>) events;
		} else {
			return repository.find(clazz, query);
		}
	}

	public <T> long count(Class<T> clazz, Query query) {
		return repository.count(clazz, query);
	}

	public <T> Object insert(T object) {
		events = null;
		return repository.insert(object);
	}

	public <T> void update(T object) {
		events = null;
		repository.update(object);
	}

	public <T> void delete(Class<T> clazz, Object id) {
		events = null;
		repository.delete(clazz, id);
	}
	
	public static class EventsQuery implements Query {
		private static final long serialVersionUID = 1L;
		
		public static final EventsQuery instance = new EventsQuery();
		
	}
}
