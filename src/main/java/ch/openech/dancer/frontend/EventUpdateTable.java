package ch.openech.dancer.frontend;

import static ch.openech.dancer.backend.EventUpdateCounter.$;

import java.util.List;
import java.util.Objects;

import org.minimalj.frontend.page.TablePage;

import ch.openech.dancer.backend.EventUpdateCounter;

public class EventUpdateTable extends TablePage<EventUpdateCounter> {

	private final List<EventUpdateCounter> counters;

	public EventUpdateTable(List<EventUpdateCounter> counters) {
		super(new Object[] { $.provider, $.newEvents, $.updatedEvents, $.skippedEditedEvents, $.skippedBlockedEvents, $.failedEvents });
		this.counters = Objects.requireNonNull(counters);
	}

	@Override
	protected List<EventUpdateCounter> load() {
		return counters;
	}

}