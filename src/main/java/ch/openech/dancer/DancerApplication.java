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
import org.minimalj.frontend.page.Routing;
import org.minimalj.repository.query.By;
import org.minimalj.security.Subject;

import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.frontend.DanceEventAdminTablePage;
import ch.openech.dancer.frontend.DanceEventLocationTablePage;
import ch.openech.dancer.frontend.DancerRouting;
import ch.openech.dancer.frontend.DeeJayTablePage;
import ch.openech.dancer.frontend.EventHousekeepingAction;
import ch.openech.dancer.frontend.EventUpdateAction;
import ch.openech.dancer.frontend.EventsPage;
import ch.openech.dancer.frontend.InfoPage;
import ch.openech.dancer.frontend.LocationMapPage;
import ch.openech.dancer.frontend.LocationTablePage;
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

		// actions.add(new EntityTablePage());


		if (Subject.currentHasRole(DancerRoles.admin.name())) {
			ActionGroup pub = actions.addGroup("Öffentlich");
			pub.add(new EventsPage());
			pub.add(new LocationMapPage());
			pub.add(new InfoPage());
			ActionGroup events = actions.addGroup("Anlässe");
			events.add(new DanceEventAdminTablePage());
			events.add(new EventUpdateAction());
			events.add(new EventHousekeepingAction());
			ActionGroup admin = actions.addGroup("Stammdaten");
			admin.add(new LocationTablePage());
			admin.add(new DeeJayTablePage());
			// admin.add(new UserTablePage());
		} else if (Subject.currentHasRole(DancerRoles.location.name())) {
			Location location = Backend.find(Location.class, By.field(Location.$.name, Subject.getCurrent().getName())).get(0);
			actions.add(new PageAction(new DanceEventLocationTablePage(location)));
		} else {
			actions.add(new EventsPage());
			actions.add(new LocationMapPage());
			actions.add(new InfoPage());
			// noch zu wenig in einzelnen Regionen
			// ActionGroup regions = actions.addGroup("Regionen");
			// for (Region region : Region.values()) {
			// regions.add(new EventsPage(region), region.name());
			// }
		}
		return actions.getItems();
	}
	
	@Override
	public Page createSearchPage(String query) {
		return new EventsPage(query);
	}
	
	@Override
	public Routing createRouting() {
		return new DancerRouting();
	}

	@Override
	public Class<?>[] getEntityClasses() {
		return new Class<?>[] { DanceEvent.class };
	}

//	@Override
//	public ResourceBundle getResourceBundle(Locale locale) {
//		ResourceBundle my = super.getResourceBundle(locale);
//		return new MultiResourceBundle(my, ResourceBundle.getBundle("MjModel"));
//	}

	public static void main(String[] args) {
		Configuration.set("MjRepository", DancerRepository.class.getName());
		Configuration.set("MjAuthentication", DancerAuthentication.class.getName());
		Configuration.set("MjInit", DancerInitTransaction.class.getName());
		Application application = new DancerApplication();
		// Swing.start(application);
		//Lanterna.start(application);
		// RestServer.start(application);
		WebServer.start(application);
		// RestServer.start(application);
		// MjVaadinSpringbootApplication.start(application);
	}

}
