package ch.openech.dancer.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Searched;
import org.minimalj.model.annotation.Size;

public class DanceEvent {
	public static final DanceEvent $ = Keys.of(DanceEvent.class);
	
	public Object id;
	
	@NotEmpty
	public EventStatus status;

	@NotEmpty
	public LocalDate date;
	
	@NotEmpty
	public LocalTime from, until;
	
	@Size(100)
	@NotEmpty
	@Searched
	public String title;
	@Size(1000)
	@Searched
	public String description;
	
	public Organizer organizer;
	
	public Location location;
	
	public final Set<EventTag> tags = new TreeSet<>();
	public Recur recur;
	
	public final List<DanceFloor> floors = new ArrayList<>();
}
