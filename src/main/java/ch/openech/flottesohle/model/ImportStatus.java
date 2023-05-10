package ch.openech.flottesohle.model;

import java.time.LocalDateTime;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Size;
import org.minimalj.model.annotation.Width;

public class ImportStatus {

	public static final ImportStatus $ = Keys.of(ImportStatus.class);

	@Width(Width.SMALLER)
	public Boolean active = false;

	@Size(Size.TIME_WITH_SECONDS)
	@Width(Width.LARGER)
	public LocalDateTime lastRun, lastChange;

}
