package ch.openech.dancer;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.minimalj.application.Application;
import org.minimalj.application.Configuration;
import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.action.ActionGroup;
import org.minimalj.frontend.impl.web.MjHttpHandler;
import org.minimalj.frontend.impl.web.WebApplication;
import org.minimalj.frontend.impl.web.WebApplicationPage;
import org.minimalj.security.Authentication.LoginListener;
import org.minimalj.security.Subject;
import org.minimalj.security.UserPasswordAuthentication.UserPasswordLoginAction;
import org.minimalj.util.resources.Resources;

import ch.openech.dancer.backend.DanceEventProviders;
import ch.openech.dancer.backend.DancerRepository;
import ch.openech.dancer.backend.EventsUpdateTransaction;
import ch.openech.dancer.frontend.AccessPage;
import ch.openech.dancer.frontend.AdminLogPage;
import ch.openech.dancer.frontend.DanceEventAdminTablePage;
import ch.openech.dancer.frontend.DeeJayTablePage;
import ch.openech.dancer.frontend.EventHousekeepingAction;
import ch.openech.dancer.frontend.EventUpdateAction;
import ch.openech.dancer.frontend.LocationAdminTablePage;
import ch.openech.dancer.model.AccessCounter;
import ch.openech.dancer.model.AdminLog;
import ch.openech.dancer.model.DanceEvent;

public class DancerApplication extends WebApplication {

	@Override
	public MjHttpHandler createHttpHandler() {
		return new ThymeDancerHandler();
	}

	@Override
	public String getMjPath() {
		return "/admin/";
	}

	private class SohleLoginListener implements LoginListener {
		@Override
		public void loginSucceded(Subject subject) {
			Frontend.show(new DanceEventAdminTablePage());
		}
		
		@Override
		public void loginCancelled() {
			Frontend.show(new WebApplicationPage("/events.html").titleResource("EventsPage"));
		}
	}
	
	@Override
	public void init() {
		new UserPasswordLoginAction(new SohleLoginListener()).run();
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

		//		if (Subject.currentHasRole(DancerRoles.location.name())) {
		//			Location location = Backend.find(Location.class, By.field(Location.$.name, Subject.getCurrent().getName())).get(0);
		//			actions.add(new PageAction(new DanceEventLocationTablePage(location)));

		ActionGroup pub = actions.addGroup(Resources.getString("Navigation.public"));
		pub.add(new WebApplicationPage("/events.html").titleResource("EventsPage"));
		pub.add(new WebApplicationPage("/location_map.html").titleResource("LocationMapPage"));
		pub.add(new WebApplicationPage("/locations.html").titleResource("LocationsPage"));
		pub.add(new WebApplicationPage("/infos.html").titleResource("InfoPage"));

		if (Subject.currentHasRole(DancerRoles.admin.name())) {
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
		} else {
			actions.add(new UserPasswordLoginAction(new SohleLoginListener()));
		}
		
		if (Subject.getCurrent() != null) {
			actions.add(Backend.getInstance().getAuthentication().getLogoutAction());
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
	
	@Override
	public void initBackend() {
		Backend.execute(new DancerInitTransaction());
	}

	public static void main(String[] args) {
		Configuration.set("MjRepository", DancerRepository.class.getName());
		Configuration.set("MjAuthentication", DancerAuthentication.class.getName());
		Configuration.set("MjLoginAtStart", "true");
		Application application = new DancerApplication();
		// Swing.start(application);
		// Lanterna.start(application);
		// MinimalTow.start(application);
		org.minimalj.frontend.impl.web.WebServer.start(application);
		// NanoServer.start(application);
		// Vaadin.start(application);
		Backend.execute(new EventsUpdateTransaction(DanceEventProviders.UPDATED_PROVIDER_NAMES));
	}

}
