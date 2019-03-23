package ch.openech.dancer;

import java.util.List;

import org.minimalj.application.Application;
import org.minimalj.application.Configuration;
import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.action.ActionGroup;
import org.minimalj.frontend.impl.web.WebServer;
import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.PageAction;
import org.minimalj.repository.query.By;
import org.minimalj.security.Subject;
import org.minimalj.security.model.User;

import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.frontend.CheckUnpublishedEventsAction;
import ch.openech.dancer.frontend.DanceEventAdminTablePage;
import ch.openech.dancer.frontend.DanceEventLocationTablePage;
import ch.openech.dancer.frontend.DanceEventPage;
import ch.openech.dancer.frontend.DanceEventTablePage;
import ch.openech.dancer.frontend.DeeJayTablePage;
import ch.openech.dancer.frontend.EventCreationAction;
import ch.openech.dancer.frontend.EventsPage;
import ch.openech.dancer.frontend.LocationTablePage;
import ch.openech.dancer.frontend.UserTablePage;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;

public class DancerApplication extends Application {
	
	@Override
	public Page createDefaultPage() {
		return new EventsPage();
	}

	@Override
	public List<Action> getNavigation() {
		ActionGroup actions = new ActionGroup("");
		actions.add(new PageAction(new EventsPage()));
		
		if (Subject.currentHasRole(DancerRoles.admin.name())) {
			ActionGroup admin = actions.addGroup("Admin");
			admin.add(new PageAction(new DanceEventAdminTablePage()));
			admin.add(new LocationTablePage());
			admin.add(new DeeJayTablePage());
			admin.add(new UserTablePage());
			admin.add(new EventCreationAction());
			admin.add(new CheckUnpublishedEventsAction());
		} else if (Subject.currentHasRole(DancerRoles.location.name())) {
			Location location = Backend.find(Location.class, By.field(Location.$.name, Subject.getCurrent().getName())).get(0);
			actions.add(new PageAction(new DanceEventLocationTablePage(location)));
		}
		return actions.getItems();
	}
	
	@Override
	public Page createSearchPage(String query) {
		return new DanceEventTablePage(query);
	}
	
	@Override
	public Page createPage(String route) {
		if (route.startsWith("event/")) {
			String id = route.substring("event/".length());
			return new DanceEventPage(id);
		} else {
			return null;
		}
	}

	@Override
	public Class<?>[] getEntityClasses() {
		return new Class<?>[] { DanceEvent.class, User.class };
	}

	public static void main(String[] args) {
		Configuration.set("MjRepository", DancerRepository.class.getName());
		Configuration.set("MjAuthentication", DancerAuthentication.class.getName());
		Configuration.set("MjInit", DancerInitTransaction.class.getName());
		Application application = new DancerApplication();
		// Swing.start(application);
		//Lanterna.start(application);
		// RestServer.start(application);
		WebServer.start(application);
	}

}
