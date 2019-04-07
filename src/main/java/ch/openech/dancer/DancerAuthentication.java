package ch.openech.dancer;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.security.UserPasswordAuthentication;
import org.minimalj.security.model.User;
import org.minimalj.security.model.UserRole;
import org.minimalj.util.CloneHelper;

import ch.openech.dancer.model.Location;

public class DancerAuthentication extends UserPasswordAuthentication {
	private static final long serialVersionUID = 1L;

	private static final User admin = new User();
	static {
		admin.name = "admin";
		admin.roles.add(new UserRole(DancerRoles.admin.name()));
		String password = System.getProperty("ADMIN_PASSWORD", "");
		admin.password.setPassword(password.toCharArray());
	}

	@Override
	protected User retrieveUser(String userName) {
		if ("admin".equals(userName)) {
			return admin;
		}
		List<Location> locations = Backend.find(Location.class, By.field(Location.$.name, userName));
		if (locations.isEmpty()) {
			return null;
		}
		Location location = locations.get(0);
		User user = new User();
		user.name = location.name;
		user.roles.add(new UserRole(DancerRoles.location.name()));
		CloneHelper.deepCopy(location.password, user.password);
		return user;
	}

}
