package ch.openech.flottesohle.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Enabled;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Size;
import org.minimalj.security.model.Password;

public class FlotteSohleUser {
	public static final FlotteSohleUser $ = Keys.of(FlotteSohleUser.class);
	
	public Object id;
	
	@Size(255) @NotEmpty
	public String email;
	
	@NotEmpty
	public final Password password = new Password();
	
	@Size(60) 
	public String vorname, name;
	
	@Enabled("enableLocations")
	public List<Location> locations = new ArrayList<>();

	public Boolean multiLocation;
	
	public LocalDateTime lastLogin;
	
	public boolean enableLocations() {
		return Boolean.TRUE.equals(multiLocation);
	}
}
