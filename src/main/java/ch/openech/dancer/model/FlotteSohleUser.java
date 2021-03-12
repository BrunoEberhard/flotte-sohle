package ch.openech.dancer.model;

import java.util.ArrayList;
import java.util.List;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Size;
import org.minimalj.security.model.Password;

public class FlotteSohleUser {
	public static final FlotteSohleUser $ = Keys.of(FlotteSohleUser.class);
	
	public Object id;
	
	@Size(255)
	public String email;
	
	public final Password password = new Password();
	
	@Size(60)
	public String vorname, name;
	
	public List<Location> locations = new ArrayList<>();

	public Boolean multiLocation;
	
}
