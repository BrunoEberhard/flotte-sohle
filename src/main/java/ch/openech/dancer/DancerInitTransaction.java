package ch.openech.dancer;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.security.model.User;
import org.minimalj.security.model.UserRole;
import org.minimalj.transaction.Transaction;

public class DancerInitTransaction implements Transaction<Void> {

	@Override
	public Void execute() {
		long adminCount = Backend.count(User.class, By.field(User.$.name, "admin"));
		if (adminCount == 0) {
			User admin = new User();
			admin.name = "admin";
			admin.password.setPassword("123456".toCharArray());
			UserRole role = new UserRole("admin");
			admin.roles.add(role);
			Backend.insert(admin);
		}
		
		return null;
	}

}
