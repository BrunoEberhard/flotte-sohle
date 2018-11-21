package ch.openech.dancer.model;

import java.time.LocalDate;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Size;

public class DanceEvent {
	public static final DanceEvent $ = Keys.of(DanceEvent.class);
	
	public Object id;
	
	@NotEmpty
	public LocalDate start;
	@Size(100) @NotEmpty
	public String title;
	@Size(1000)
	public String description;
	
	public Organizer organizer;
	
	public Location location;
	
	public Recur recur;
	
	@NotEmpty
	public final DanceEventPeriod danceEventPeriod = new DanceEventPeriod();

}
