package ch.openech.dancer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.minimalj.application.Application;
import org.minimalj.application.Configuration;
import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.action.ActionGroup;
import org.minimalj.frontend.impl.web.MjHttpHandler;
import org.minimalj.frontend.impl.web.ResourcesHttpHandler;
import org.minimalj.frontend.impl.web.WebApplication;
import org.minimalj.frontend.impl.web.WebServer;
import org.minimalj.frontend.page.ExternalPage;
import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.PageAction;
import org.minimalj.frontend.page.Routing;
import org.minimalj.repository.query.By;
import org.minimalj.security.Subject;
import org.minimalj.thymeleaf.page.ThymePage;
import org.minimalj.util.resources.Resources;

import ch.openech.dancer.backend.DanceEventProviders;
import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.backend.EventsUpdateTransaction;
import ch.openech.dancer.frontend.AccessPage;
import ch.openech.dancer.frontend.AdminLogPage;
import ch.openech.dancer.frontend.DanceEventAdminTablePage;
import ch.openech.dancer.frontend.DanceEventLocationTablePage;
import ch.openech.dancer.frontend.DancerRouting;
import ch.openech.dancer.frontend.DeeJayTablePage;
import ch.openech.dancer.frontend.EventHousekeepingAction;
import ch.openech.dancer.frontend.EventUpdateAction;
import ch.openech.dancer.frontend.EventsPage;
import ch.openech.dancer.frontend.InfoPage;
import ch.openech.dancer.frontend.LocationAdminTablePage;
import ch.openech.dancer.frontend.LocationMapPage;
import ch.openech.dancer.frontend.LocationsPage;
import ch.openech.dancer.model.AccessCounter;
import ch.openech.dancer.model.AdminLog;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;

public class ThyDancerApplication extends WebApplication {

	@Override
	public List<MjHttpHandler> createHttpHandlers() {
		return Arrays.asList(new ThyDancerHandler(), new ResourcesHttpHandler());
	}
	
	public String getMjHandlerPath() {
		return "/admin/";
	}

	@Override
	public Page createDefaultPage() {
		return new ExternalPage("../events.html");
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		String resourceBundleName = DancerApplication.class.getName();
		return ResourceBundle.getBundle(resourceBundleName, locale, Control.getNoFallbackControl(Control.FORMAT_PROPERTIES));
	}

	@Override
	public List<Action> getNavigation() {
		ActionGroup actions = new ActionGroup("");

		// actions.add(new EntityTablePage());

		if (Subject.currentHasRole(DancerRoles.admin.name())) {
			ActionGroup pub = actions.addGroup(Resources.getString("Navigation.public"));
			pub.add(new ThymePage("/events.html"));
//			pub.add(new EventsPage());
//			pub.add(new LocationMapPage());
//			pub.add(new LocationsPage());
//			pub.add(new InfoPage());
			ActionGroup events = actions.addGroup(Resources.getString("Navigation.events"));
			events.add(new DanceEventAdminTablePage());
			events.add(new EventUpdateAction());
			events.add(new EventHousekeepingAction());
			ActionGroup base = actions.addGroup(Resources.getString("Navigation.base"));
			base.add(new LocationAdminTablePage());
			base.add(new DeeJayTablePage());
			ActionGroup admin = actions.addGroup(Resources.getString("Navigation.admin"));
			admin.add(new AccessPage());
			admin.add(new AdminLogPage());
			// admin.add(new UserTablePage());
		} else if (Subject.currentHasRole(DancerRoles.location.name())) {
			Location location = Backend.find(Location.class, By.field(Location.$.name, Subject.getCurrent().getName())).get(0);
			actions.add(new PageAction(new DanceEventLocationTablePage(location)));
		} else {
			actions.add(new EventsPage());
			actions.add(new LocationMapPage());
			actions.add(new LocationsPage());
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
		return new Class<?>[] { DanceEvent.class, AccessCounter.class, AdminLog.class };
	}

//	@Override
//	public ResourceBundle getResourceBundle(Locale locale) {
//		ResourceBundle my = super.getResourceBundle(locale);
//		return new MultiResourceBundle(my, ResourceBundle.getBundle("MjModel"));
//	}

	public static void main(String[] args) {
		Configuration.set("MjRepository", DancerRepository.class.getName());
		Configuration.set("MjAuthentication", DancerAuthentication.class.getName());
		Configuration.set("MjLoginAtStart", "true");
		Configuration.set("MjInit", DancerInitTransaction.class.getName());
		Application application = new ThyDancerApplication();
		// Swing.start(application);
		// Lanterna.start(application);
		// RestServer.start(application);
		WebServer.start(application);
		// RestServer.start(application);
		// MjVaadinSpringbootApplication.start(application);
		Backend.execute(new EventsUpdateTransaction(DanceEventProviders.PROVIDER_NAMES));
	}

}
