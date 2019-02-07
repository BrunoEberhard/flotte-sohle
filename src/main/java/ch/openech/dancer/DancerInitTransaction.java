package ch.openech.dancer;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.security.model.User;
import org.minimalj.security.model.UserRole;
import org.minimalj.transaction.Transaction;
import org.minimalj.util.CsvReader;

import ch.openech.dancer.model.DeeJay;

public class DancerInitTransaction implements Transaction<Void> {

	private static final long serialVersionUID = 1L;

	@Override
	public Void execute() {
		
		if (Backend.count(User.class, By.all()) == 0) {
			User user = new User();
			user.name = "admin";
			user.roles.add(new UserRole("admin"));
			user.password.setPassword("admin".toCharArray());
			Backend.insert(user);
		}

		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/deejays.csv"));
		for (DeeJay deeJay : reader.readValues(DeeJay.class)) {
			Backend.insert(deeJay);
		}

		return null;
	}

}
