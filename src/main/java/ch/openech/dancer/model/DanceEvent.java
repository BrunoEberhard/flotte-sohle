package ch.openech.dancer.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Searched;
import org.minimalj.model.annotation.Size;
import org.minimalj.util.LocaleContext;

public class DanceEvent {
	public static final DanceEvent $ = Keys.of(DanceEvent.class);
	
	public Object id;
	
	@NotEmpty
	public EventStatus status;

	@NotEmpty
	public LocalDate date;
	
	@NotEmpty
	@Size(Size.TIME_HH_MM)
	public LocalTime from, until;
	
	public String getFromUntil() {
		if (Keys.isKeyObject(this))
			return Keys.methodOf(this, "fromUntil");

		return Rendering.render(from) + " - " + Rendering.render(until);
	}

	public String getDayOfWeek() {
		if (Keys.isKeyObject(this))
			return Keys.methodOf(this, "dayOfWeek", $.date);

		if (date != null) {
			DayOfWeek dayOfWeek = date.getDayOfWeek();
			return dayOfWeek.getDisplayName(TextStyle.FULL, LocaleContext.getCurrent());
		} else {
			return null;
		}
	}

	@Size(100)
	@NotEmpty
	@Searched
	public String title;
	@Size(1000)
	@Searched
	public String description;
	
	public byte[] flyer;

	public Location location;
	
	public final Set<EventTag> tags = new TreeSet<>();
	
	public final List<DanceFloor> floors = new ArrayList<>();
}
