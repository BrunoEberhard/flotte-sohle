package ch.openech.dancer.model;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.security.model.User;

public class UserLocation {
	public static final UserLocation $ = Keys.of(UserLocation.class);
	
	public Object id;
	
	@NotEmpty
	public User user;

	@NotEmpty
	public Location organizer;

}