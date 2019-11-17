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
import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.PageAction;
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
import ch.openech.dancer.frontend.DeeJayTablePage;
import ch.openech.dancer.frontend.EventHousekeepingAction;
import ch.openech.dancer.frontend.EventUpdateAction;
import ch.openech.dancer.frontend.LocationAdminTablePage;
import ch.openech.dancer.model.AccessCounter;
import ch.openech.dancer.model.AdminLog;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;

public class DancerApplication extends WebApplication {

	@Override
	public List<MjHttpHandler> createHttpHandlers() {
		return Arrays.asList(new ThyDancerHandler(), new ResourcesHttpHandler());
	}
	
	public String getMjHandlerPath() {
		return "/admin/";
	}

	@Override
	public Page createDefaultPage() {
		return new ThymePage("/events.html", Resources.getString("EventsPage"));
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
			pub.add(new ThymePage("/events.html", Resources.getString("EventsPage")));
			pub.add(new ThymePage("/location_map.html", Resources.getString("LocationMapPage")));
			pub.add(new ThymePage("/locations.html", Resources.getString("LocationsPage")));
			pub.add(new ThymePage("/infos.html", Resources.getString("InfoPage")));
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
			actions.add(new ThymePage("/events.html", Resources.getString("EventsPage")));
			actions.add(new ThymePage("/location_map.html", Resources.getString("LocationMapPage")));
			actions.add(new ThymePage("/locations.html", Resources.getString("LocationsPage")));
			actions.add(new ThymePage("/infos.html", Resources.getString("InfoPage")));
		}
		return actions.getItems();
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
		Application application = new DancerApplication();
		// Swing.start(application);
		// Lanterna.start(application);
		// RestServer.start(application);
		WebServer.start(application);
		// RestServer.start(application);
		// MjVaadinSpringbootApplication.start(application);
		Backend.execute(new EventsUpdateTransaction(DanceEventProviders.PROVIDER_NAMES));
	}

}
