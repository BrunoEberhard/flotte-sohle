package ch.flottesohle;

import java.time.LocalDateTime;
import java.util.List;

import org.minimalj.application.Configuration;
import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.security.UserPasswordAuthentication;
import org.minimalj.security.model.User;
import org.minimalj.security.model.UserData;
import org.minimalj.security.model.UserRole;
import org.minimalj.util.CloneHelper;

import ch.flottesohle.model.AdminLog;
import ch.flottesohle.model.FlotteSohleUser;
import ch.flottesohle.model.AdminLog.AdminLogType;

public class FlotteSohleAuthentication extends UserPasswordAuthentication {
	private static final long serialVersionUID = 1L;

	private static final User admin = new User();
	static {
		admin.name = "admin";
		admin.roles.add(new UserRole(FlotteSohleRoles.admin.name()));
		String password = Configuration.get("ADMIN_PASSWORD", "");
		if (!Configuration.isDevModeActive()) {
			admin.password.setPassword(password.toCharArray());
		} else {
			// damit funktioniert das token basierte remember me auch
			// noch nach einen Neustart
			admin.password.setPasswordWithoutSalt(password.toCharArray());
		}
	}

	@Override
	protected UserData retrieveUser(String userName, char[] password) {
		UserData user = super.retrieveUser(userName, password);
		if (user != null) {
			Backend.insert(new AdminLog(AdminLogType.LOGIN, user.getName() + " eingeloggt"));
		}
		return user;
	}

	@Override
	protected User retrieveUser(String userName) {
		if ("admin".equals(userName)) {
			return admin;
		}
		List<FlotteSohleUser> users = Backend.find(FlotteSohleUser.class, By.field(FlotteSohleUser.$.email, userName));
		if (users.isEmpty()) {
			return null;
		}
		FlotteSohleUser flotteSohleUser = users.get(0);
		User user = new User();
		user.name = flotteSohleUser.email;
		if (flotteSohleUser.multiLocation) {
			user.roles.add(new UserRole(FlotteSohleRoles.multiLocation.name()));
		}
		CloneHelper.deepCopy(flotteSohleUser.password, user.password);
		flotteSohleUser.lastLogin = LocalDateTime.now();
		Backend.update(flotteSohleUser);
		return user;
	}

}
