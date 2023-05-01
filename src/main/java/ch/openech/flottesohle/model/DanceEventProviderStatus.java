package ch.openech.flottesohle.model;

import java.time.LocalDateTime;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Size;
import org.minimalj.model.annotation.Width;

import ch.openech.flottesohle.backend.EventUpdateCounter;

public class DanceEventProviderStatus {

	public static final DanceEventProviderStatus $ = Keys.of(DanceEventProviderStatus.class);

	@Width(Width.SMALLER)
	public Boolean active = false;

	@Size(Size.TIME_WITH_SECONDS)
	@Width(Width.LARGER)
	public LocalDateTime lastRun, lastChange;

	public final EventUpdateCounter eventUpdateCounter = new EventUpdateCounter();

}
