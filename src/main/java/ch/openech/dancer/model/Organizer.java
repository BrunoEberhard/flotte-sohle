package ch.openech.dancer.model;

import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Size;

public class Organizer implements Rendering {
	public static final Organizer $ = Keys.of(Organizer.class);
	
	public Object id;
	
	@Size(255) @NotEmpty
	public String name;
	
	@Size(60)
	public String address, city, country;
	
	public Location homeLocation;
	
	@Size(255)
	public String url;
	
	@Override
	public String render() {
		return name;
	}
}
