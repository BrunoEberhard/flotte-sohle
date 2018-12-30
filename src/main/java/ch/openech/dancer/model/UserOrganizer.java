package ch.openech.dancer.model;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.security.model.User;

public class UserOrganizer {
	public static final UserOrganizer $ = Keys.of(UserOrganizer.class);
	
	public Object id;
	
	@NotEmpty
	public User user;

	@NotEmpty
	public Organizer organizer;

}