package ch.openech.flottesohle;

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
import org.minimalj.frontend.impl.web.WebApplication;
import org.minimalj.frontend.impl.web.WebApplicationPage;
import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.PageAction;
import org.minimalj.repository.query.By;
import org.minimalj.security.Subject;
import org.minimalj.util.resources.Resources;

import ch.openech.flottesohle.backend.DanceEventProviders;
import ch.openech.flottesohle.backend.FlotteSohleRepository;
import ch.openech.flottesohle.backend.EventsUpdateTransaction;
import ch.openech.flottesohle.frontend.AccessPage;
import ch.openech.flottesohle.frontend.AdminLogPage;
import ch.openech.flottesohle.frontend.DanceEventAdminTablePage;
import ch.openech.flottesohle.frontend.DanceEventLocationTablePage;
import ch.openech.flottesohle.frontend.DeeJayTablePage;
import ch.openech.flottesohle.frontend.EventHousekeepingAction;
import ch.openech.flottesohle.frontend.EventUpdateAction;
import ch.openech.flottesohle.frontend.LocationAdminTablePage;
import ch.openech.flottesohle.frontend.LocationEditor;
import ch.openech.flottesohle.frontend.LockdownAction;
import ch.openech.flottesohle.frontend.PasswordEditor;
import ch.openech.flottesohle.frontend.UserTablePage;
import ch.openech.flottesohle.model.AccessCounter;
import ch.openech.flottesohle.model.AdminLog;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.FlotteSohleUser;

public class FlotteSohle extends WebApplication {

	@Override
	public MjHttpHandler createHttpHandler() {
		return new FlotteSohleThymeHandler();
	}

	@Override
	public String getMjPath() {
		return "/admin/";
	}
	
	@Override
	public AuthenticatonMode getAuthenticatonMode() {
		return AuthenticatonMode.REQUIRED;
	}

	@Override
	public Page createDefaultPage() {
		if (Subject.currentHasRole(FlotteSohleRoles.admin.name())) {
			return new DanceEventAdminTablePage();
		} else {
			return new WebApplicationPage("/events.html").titleResource("EventsPage");
		}
	}
	
	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		String resourceBundleName = FlotteSohle.class.getName();
		return ResourceBundle.getBundle(resourceBundleName, locale, Control.getNoFallbackControl(Control.FORMAT_PROPERTIES));
	}

	@Override
	public List<Action> getNavigation() {
		ActionGroup actions = new ActionGroup("");

		// actions.add(new EntityTablePage());

		if (Subject.currentHasRole(FlotteSohleRoles.admin.name())) {
			ActionGroup pub = actions.addGroup(Resources.getString("Navigation.public"));
			pub.add(new WebApplicationPage("/events.html").titleResource("EventsPage"));
			pub.add(new WebApplicationPage("/location_map.html").titleResource("LocationMapPage"));
			pub.add(new WebApplicationPage("/locations.html").titleResource("LocationsPage"));
			pub.add(new WebApplicationPage("/infos.html").titleResource("InfoPage"));
			
			ActionGroup events = actions.addGroup(Resources.getString("Navigation.events"));
			events.add(new DanceEventAdminTablePage());
			events.add(new EventUpdateAction());
			events.add(new EventHousekeepingAction());
			events.add(new LockdownAction());
			
			ActionGroup base = actions.addGroup(Resources.getString("Navigation.base"));
			base.add(new LocationAdminTablePage());
			base.add(new DeeJayTablePage());
			
			ActionGroup admin = actions.addGroup(Resources.getString("Navigation.admin"));
			admin.add(new UserTablePage());
			
			ActionGroup stats = actions.addGroup(Resources.getString("Navigation.stats"));
			stats.add(new AccessPage());
			stats.add(new AdminLogPage());
		} else if (Subject.currentHasRole(FlotteSohleRoles.multiLocation.name())) {
			// TODO Anlässe, Locations anbieten

			// actions.add(new PasswordEditor(users.get(0)));
		} else if (Subject.getCurrent() != null) {
			List<FlotteSohleUser> users = Backend.find(FlotteSohleUser.class, By.field(FlotteSohleUser.$.email, Subject.getCurrent().getName()));
			if (users.size() != 1) {
				System.err.println("Exactly one user should be found not " + users.size());
			} else if (users.get(0).locations.size() != 1) {
				System.err.println("Exactly one location should be found not " + users.get(0).locations.size());
			} else {
				actions.add(new DanceEventLocationTablePage(users.get(0).locations.get(0)));
				actions.add(new LocationEditor(users.get(0).locations.get(0)));
				actions.add(new PageAction(new LocationAdminTablePage.LocationClosingTablePage(users.get(0).locations.get(0))));
				actions.add(new PasswordEditor(users.get(0)));
				// TODO ev löschen anbieten
			}
		} else {
			ActionGroup pub = actions.addGroup(Resources.getString("Navigation.public"));
			pub.add(new WebApplicationPage("/events.html").titleResource("EventsPage"));
			pub.add(new WebApplicationPage("/location_map.html").titleResource("LocationMapPage"));
			pub.add(new WebApplicationPage("/locations.html").titleResource("LocationsPage"));
			pub.add(new WebApplicationPage("/infos.html").titleResource("InfoPage"));
		}

		return actions.getItems();
	}

	@Override
	public Class<?>[] getEntityClasses() {
		return new Class<?>[] { DanceEvent.class, AccessCounter.class, AdminLog.class, FlotteSohleUser.class };
	}

//	@Override
//	public ResourceBundle getResourceBundle(Locale locale) {
//		ResourceBundle my = super.getResourceBundle(locale);
//		return new MultiResourceBundle(my, ResourceBundle.getBundle("MjModel"));
//	}
	
	@Override
	public void initBackend() {
		Backend.execute(new FlotteSohleInitTransaction());
	}

	public static void main(String[] args) {
		Configuration.set("MjRepository", FlotteSohleRepository.class.getName());
		Configuration.set("MjAuthentication", FlotteSohleAuthentication.class.getName());
		Configuration.set("MjLoginAtStart", "true");
		Application application = new FlotteSohle();
		// Swing.start(application);
		// Lanterna.start(application);
		// MinimalTow.start(application);
		org.minimalj.frontend.impl.web.WebServer.start(application);
		// NanoServer.start(application);
		// Vaadin.start(application);
		Backend.execute(new EventsUpdateTransaction(DanceEventProviders.UPDATED_PROVIDER_NAMES));
	}

}