package ch.openech.flottesohle.backend;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Size;
import org.minimalj.model.annotation.Width;

public class EventUpdateCounter {
	public static final EventUpdateCounter $ = Keys.of(EventUpdateCounter.class);

	@Width(Width.SMALLEST)
	public Integer newEvents = 0;

	@Width(Width.SMALLEST)
	public Integer updatedEvents = 0;

	@Width(Width.SMALLEST)
	public Integer skippedEditedEvents = 0;

	@Width(Width.SMALLEST)
	public Integer skippedBlockedEvents = 0;

	@Width(Width.SMALLEST)
	public Integer failedEvents = 0;

	@Size(10240)
	public String exception;
}
