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
import ch.flottesohle.model.AdminLog.AdminLogType;
import ch.flottesohle.model.FlotteSohleUser;

public class FlotteSohleAuthentication extends UserPasswordAuthentication {
	private static final long serialVersionUID = 1L;

	private static final User admin = createAdminUser("admin", "ADMIN_PASSWORD");
	private static final User deputy1 = createAdminUser("alois", "DEPUTY1_PASSWORD");

	private static User createAdminUser(String name, String passwordConfigurationName) {
		User admin = new User();
		admin.name = name;
		admin.roles.add(new UserRole(FlotteSohleRoles.admin.name()));
		String password = Configuration.get(passwordConfigurationName, "");
		if (!Configuration.isDevModeActive()) {
			admin.password.setPassword(password.toCharArray());
		} else {
			// damit funktioniert das token basierte remember me auch
			// noch nach einen Neustart
			admin.password.setPasswordWithoutSalt(password.toCharArray());
		}
		return admin;
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
		if (admin.name.equals(userName)) {
			return admin;
		} else if (deputy1.name.equals(userName)) {
			return deputy1;
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
