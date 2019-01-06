package ch.openech.dancer;

import java.util.List;

import org.minimalj.application.Application;
import org.minimalj.application.Configuration;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.action.ActionGroup;
import org.minimalj.frontend.impl.web.WebServer;
import org.minimalj.frontend.page.PageAction;
import org.minimalj.security.RepositoryAuthentication;
import org.minimalj.security.Subject;
import org.minimalj.security.model.User;

import ch.openech.dancer.frontend.DanceCubeAction;
import ch.openech.dancer.frontend.DanceEventAdminTablePage;
import ch.openech.dancer.frontend.DanceEventTablePage;
import ch.openech.dancer.frontend.ElSocialRuleAction;
import ch.openech.dancer.frontend.LocationTablePage;
import ch.openech.dancer.frontend.OrganizerTablePage;
import ch.openech.dancer.frontend.PasadenaCrawlerAction;
import ch.openech.dancer.frontend.Time2DanceCrawlerAction;
import ch.openech.dancer.frontend.UserTablePage;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.UserDeeJay;
import ch.openech.dancer.model.UserOrganizer;

public class DancerApplication extends Application {
	
	

	@Override
	public List<Action> getNavigation() {
		ActionGroup actions = new ActionGroup("");

		if (Subject.currentHasRole(DancerRoles.admin.name())) {
			actions.add(new PageAction(new DanceEventAdminTablePage()));

			ActionGroup admin = actions.addGroup("Admin");
			admin.add(new PageAction(new OrganizerTablePage()));
			admin.add(new PageAction(new LocationTablePage()));
			admin.add(new PageAction(new UserTablePage()));
			ActionGroup crawler = actions.addGroup("Crawler");
			crawler.add(new PasadenaCrawlerAction());
			crawler.add(new Time2DanceCrawlerAction());
			crawler.add(new ElSocialRuleAction());
			crawler.add(new DanceCubeAction());
		} else if (Subject.currentHasRole(DancerRoles.organizer.name())) {

		} else {
			actions.add(new PageAction(new DanceEventTablePage()));
		}
		return actions.getItems();
	}
	
	@Override
	public Class<?>[] getEntityClasses() {
		return new Class<?>[] { DanceEvent.class, User.class, UserDeeJay.class, UserOrganizer.class };
	}

	public static void main(String[] args) {
		Configuration.set("MjAuthentication", RepositoryAuthentication.class.getName());
		Configuration.set("MjInit", DancerInitTransaction.class.getName());
		// Application.main(args);
		Application application = new DancerApplication();
		// Swing.start(application);
		// Lanterna.start(application);
		// Swing.start(application);
		//Lanterna.start(application);
		// RestServer.start(application);
		WebServer.start(application);
	}

}
