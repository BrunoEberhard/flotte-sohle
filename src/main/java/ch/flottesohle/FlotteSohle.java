package ch.flottesohle;

import java.util.List;
import java.util.logging.Logger;

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
import org.minimalj.repository.Repository;
import org.minimalj.repository.query.By;
import org.minimalj.security.Authentication;
import org.minimalj.security.Subject;
import org.minimalj.util.resources.Resources;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import ch.flottesohle.backend.DanceEventImportJob;
import ch.flottesohle.backend.DanceEventProviders;
import ch.flottesohle.backend.FlotteSohleRepository;
import ch.flottesohle.frontend.AccessPage;
import ch.flottesohle.frontend.AdminLogPage;
import ch.flottesohle.frontend.DanceEventAdminTablePage;
import ch.flottesohle.frontend.DanceEventLocationTablePage;
import ch.flottesohle.frontend.DeeJayTablePage;
import ch.flottesohle.frontend.EventHousekeepingAction;
import ch.flottesohle.frontend.LocationAdminTablePage;
import ch.flottesohle.frontend.LocationEditor;
import ch.flottesohle.frontend.LockdownAction;
import ch.flottesohle.frontend.PasswordEditor;
import ch.flottesohle.frontend.UserTablePage;
import ch.flottesohle.model.AccessCounter;
import ch.flottesohle.model.AdminLog;
import ch.flottesohle.model.DanceEvent;
import ch.flottesohle.model.FlotteSohleUser;
import ch.flottesohle.model.Location;

public class FlotteSohle extends WebApplication {

	private static final Logger LOG = Logger.getLogger(FlotteSohle.class.getName());

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
	public Authentication createAuthentication() {
		return new FlotteSohleAuthentication();
	}

	@Override
	public Page createDefaultPage() {
		if (Subject.currentHasRole(FlotteSohleRoles.admin.name())) {
			return new LocationAdminTablePage();
		} else if (Subject.getCurrent() != null) {
			FlotteSohleUser user = getUser();
			if (user != null) {
				return new DanceEventLocationTablePage(user.locations.get(0));
			}
		}
		return new WebApplicationPage("/events.html").titleResource("EventsPage");
	}

	@Override
	public List<Action> getNavigation() {
		ActionGroup actions = new ActionGroup("");

		// actions.add(new EntityTablePage());

		ActionGroup pub = actions.addGroup(Resources.getString("Navigation.public"));
		pub.add(new WebApplicationPage("/events.html").titleResource("EventsPage"));
		pub.add(new WebApplicationPage("/location_map.html").titleResource("LocationMapPage"));
		pub.add(new WebApplicationPage("/locations.html").titleResource("LocationsPage"));
		pub.add(new WebApplicationPage("/infos.html").titleResource("InfoPage"));

		if (Subject.currentHasRole(FlotteSohleRoles.admin.name())) {
			
			ActionGroup admin = actions.addGroup(Resources.getString("Navigation.admin"));
			admin.add(new DanceEventAdminTablePage());
			admin.add(new LocationAdminTablePage());
			admin.add(new DeeJayTablePage());
			admin.add(new EventHousekeepingAction());
			admin.add(new LockdownAction());
			
			ActionGroup system = actions.addGroup(Resources.getString("Navigation.system"));
			system.add(new UserTablePage());
			system.add(new AccessPage());
			system.add(new AdminLogPage());
		} else if (Subject.currentHasRole(FlotteSohleRoles.multiLocation.name())) {
			// TODO Anlässe, Locations anbieten

			// actions.add(new PasswordEditor(users.get(0)));
		} else if (Subject.getCurrent() != null) {
			FlotteSohleUser user = getUser();
			if (user != null) {
				Location location = user.locations.get(0);
				actions.add(new DanceEventLocationTablePage(location));
				actions.add(new LocationEditor(location));
				actions.add(new PageAction(new LocationAdminTablePage.LocationClosingTablePage(location)));
				actions.add(new PasswordEditor(user));
			}
		}

		return actions.getItems();
	}

	public FlotteSohleUser getUser() {
		List<FlotteSohleUser> users = Backend.find(FlotteSohleUser.class, By.field(FlotteSohleUser.$.email, Subject.getCurrent().getName()));
		if (users.size() != 1) {
			System.err.println("Exactly one user should be found not " + users.size());
			return null;
		} else if (users.get(0).locations.size() != 1) {
			System.err.println("Exactly one location should be found not " + users.get(0).locations.size());
			return null;
		}
		return users.get(0);
	}
	
	@Override
	public Class<?>[] getEntityClasses() {
		return new Class<?>[] { DanceEvent.class, AccessCounter.class, AdminLog.class, FlotteSohleUser.class };
	}
	
	@Override
	public Repository createRepository() {
		return new FlotteSohleRepository(this);
	}

	@Override
	public void initBackend() {
		Backend.execute(new FlotteSohleInitTransaction());

		LOG.info(DanceEventProviders.PROVIDERS_BY_LOCATION_ID.size() + " Providers installiert");
		
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			
			JobDetail job = JobBuilder.newJob(DanceEventImportJob.class).withIdentity(DanceEventImportJob.JOB_KEY).build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Trigger " + DanceEventImportJob.JOB_KEY.getName()).startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule("0 0 6 * * ?")).build();
//					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10)).build();
			scheduler.scheduleJob(job, trigger);
			
			scheduler.start();
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Override
	public void initFrontend() {
		Configuration.set("MjCss", "material.css");
	}

	public static void main(String[] args) {
		Application application = new FlotteSohle();
		// Swing.start(application);
		// Lanterna.start(application);
		// MinimalTow.start(application);
		org.minimalj.frontend.impl.web.WebServer.start(application);
		// NanoServer.start(application);
		// Vaadin.start(application);
		// Backend.execute(new EventsUpdateTransaction(DanceEventProviders.UPDATED_PROVIDER_NAMES));
	}

}
