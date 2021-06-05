package ch.openech.flottesohle.backend;

import org.minimalj.model.Keys;

public class EventUpdateCounter {
	public static final EventUpdateCounter $ = Keys.of(EventUpdateCounter.class);

	public String provider;

	public Integer newEvents = 0;

	public Integer updatedEvents = 0;

	public Integer skippedEditedEvents = 0;

	public Integer skippedBlockedEvents = 0;

	public Integer failedEvents = 0;

	public String exception;

}
