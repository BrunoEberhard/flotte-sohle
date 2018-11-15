package ch.openech.dancer;

import java.util.List;

import org.minimalj.application.Application;
import org.minimalj.application.Configuration;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.action.ActionGroup;
import org.minimalj.frontend.impl.swing.Swing;
import org.minimalj.frontend.page.PageAction;
import org.minimalj.rest.RestServer;
import org.minimalj.security.RepositoryAuthentication;
import org.minimalj.security.Subject;
import org.minimalj.security.model.User;

import ch.openech.dancer.frontend.DanceEventTablePage;
import ch.openech.dancer.frontend.LocationTablePage;
import ch.openech.dancer.frontend.OrganizerTablePage;
import ch.openech.dancer.frontend.UserTablePage;
import ch.openech.dancer.model.DanceEvent;

public class DancerApplication extends Application {
	
	@Override
	public List<Action> getNavigation() {
		ActionGroup actions = new ActionGroup("");
		actions.add(new PageAction(new DanceEventTablePage()));
		actions.add(new PageAction(new OrganizerTablePage()));
		actions.add(new PageAction(new LocationTablePage()));
		if (Subject.currentHasRole("admin")) {
			actions.add(new PageAction(new UserTablePage()));
		}
		return actions.getItems();
	}
	
	@Override
	public Class<?>[] getEntityClasses() {
		return new Class<?>[] { DanceEvent.class, User.class };
	}

	public static void main(String[] args) {
		Configuration.set("MjAuthentication", RepositoryAuthentication.class.getName());
		Configuration.set("MjInit", DancerInitTransaction.class.getName());
		// Application.main(args);
		Application application = new DancerApplication();
		Swing.start(application);
		RestServer.start(application);
	}

}
