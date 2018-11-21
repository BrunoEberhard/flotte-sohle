package ch.openech.dancer;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.security.model.User;
import org.minimalj.security.model.UserRole;
import org.minimalj.transaction.Transaction;

import ch.openech.dancer.crawler.PasadenaCrawler;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Organizer;

public class DancerInitTransaction implements Transaction<Void> {

	private static final long serialVersionUID = 1L;
	
	private final PasadenaCrawler pasadenaCrawler = new PasadenaCrawler();

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
		
		Organizer organizer =  PasadenaCrawler.createOrganizer();
		Location location =  PasadenaCrawler.createLocation();
		organizer.id = Backend.insert(organizer);
		location.id = Backend.insert(location);
		
		pasadenaCrawler.crawlEvents().forEach(danceEvent -> {
			
			danceEvent.location = location;
			danceEvent.organizer = organizer;
			Backend.insert(danceEvent);
			
		});
		
		return null;
	}

}
