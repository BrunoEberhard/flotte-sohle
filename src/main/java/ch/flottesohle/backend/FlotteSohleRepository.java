package ch.flottesohle.backend;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.minimalj.model.Model;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.Criteria;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.repository.query.Query;
import org.minimalj.repository.sql.SqlRepository;

import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.EventStatus;

/**
 * Hält einen kleinen Cache für die DanceEvent, damit die nicht für jeden
 * Benutzer oder für sogar für jedes Filtern neu geladen werden.
 *
 */
public class FlotteSohleRepository extends SqlRepository {

	private final Map<Object, DanceEvent> eventCache = new HashMap<>(1000);
	private List<DanceEvent> events;
	private long lastLoad = Long.MIN_VALUE;

	public FlotteSohleRepository(Model model) {
		super(model);
	}

	@SuppressWarnings("unchecked")
	public <T> T read(Class<T> clazz, Object id) {
		if (eventCache.containsKey(id)) {
			return (T) eventCache.get(id);
		}
		T result = super.read(clazz, id);
		if (result instanceof DanceEvent) {
			eventCache.put(id, (DanceEvent) result);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> clazz, Query query) {
		if (query == EventsQuery.instance) {
			LocalDate now = LocalDate.now();
			if (events == null || lastLoad < System.currentTimeMillis() - 2 * 60 * 60 * 1000) {
				events = super.find(DanceEvent.class, By //
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
			return super.find(clazz, query);
		}
	}

	public <T> long count(Class<T> clazz, Criteria criteria) {
		return super.count(clazz, criteria);
	}

	private void clearCache() {
		events = null;
		eventCache.clear();
	}

	public <T> Object insert(T object) {
		events = null;
		return super.insert(object);
	}

	public <T> void update(T object) {
		clearCache();
		super.update(object);
	}

	@Override
	public <T> void delete(T object) {
		clearCache();
		super.delete(object);
	}
	
	@Override
	public <T> int delete(Class<T> clazz, Criteria criteria) {
		clearCache();
		return super.delete(clazz, criteria);
	}

	public static class EventsQuery extends Query {
		private static final long serialVersionUID = 1L;
		
		public static final EventsQuery instance = new EventsQuery();
		
	}
}
