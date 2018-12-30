package ch.openech.dancer.model;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.security.model.User;

public class UserDeeJay {
	public static final UserDeeJay $ = Keys.of(UserDeeJay.class);
	
	public Object id;
	
	@NotEmpty
	public User user;

	@NotEmpty
	public DeeJay deeJay;

}