package ch.openech.flottesohle.backend;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.minimalj.model.Model;
import org.minimalj.repository.Repository;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.Criteria;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.repository.query.Query;
import org.minimalj.repository.sql.SqlRepository;

import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;

/**
 * Hält einen kleinen Cache für die DanceEvent, damit die nicht für jeden
 * Benutzer oder für sogar für jedes Filtern neu geladen werden.
 *
 */
public class FlotteSohleRepository implements Repository {

	private final Repository repository;

	private final Map<Object, DanceEvent> eventCache = new HashMap<>(1000);
	private List<DanceEvent> events;
	private long lastLoad = Long.MIN_VALUE;

	public FlotteSohleRepository(Model model) {
		this.repository = new SqlRepository(model);
	}

	public <T> T read(Class<T> clazz, Object id) {
		if (eventCache.containsKey(id)) {
			return (T) eventCache.get(id);
		}
		T result = repository.read(clazz, id);
		if (result instanceof DanceEvent) {
			eventCache.put(id, (DanceEvent) result);
		}
		return result;
	}

	public <T> List<T> find(Class<T> clazz, Query query) {
		if (query == EventsQuery.instance) {
			LocalDate now = LocalDate.now();
			if (events == null || lastLoad < System.currentTimeMillis() - 2 * 60 * 60 * 1000) {
				events = repository.find(DanceEvent.class, By //
						.field(DanceEvent.$.status, FieldOperator.notEqual, EventStatus.blocked) //
						.and(By.field(DanceEvent.$.date, FieldOperator.greaterOrEqual, now)) //
						.and(By.field(DanceEvent.$.date, FieldOperator.less, now.plusMonths(1)))
						.order(DanceEvent.$.date));
				// load completely in one transaction
				events = events.subList(0, events.size());
				lastLoad = System.currentTimeMillis();
			}
			events.removeIf(event -> event.location != null && event.location.isClosed(event.date));
			return (List<T>) events;
		} else {
			return repository.find(clazz, query);
		}
	}

	public <T> long count(Class<T> clazz, Criteria criteria) {
		return repository.count(clazz, criteria);
	}

	private void clearCache() {
		events = null;
		eventCache.clear();
	}

	public <T> Object insert(T object) {
		events = null;
		return repository.insert(object);
	}

	public <T> void update(T object) {
		clearCache();
		repository.update(object);
	}

	@Override
	public <T> void delete(T object) {
		clearCache();
		repository.delete(object);
	}
	
	@Override
	public <T> int delete(Class<T> clazz, Criteria criteria) {
		clearCache();
		return repository.delete(clazz, criteria);
	}

	public static class EventsQuery extends Query {
		private static final long serialVersionUID = 1L;
		
		public static final EventsQuery instance = new EventsQuery();
		
	}
}
