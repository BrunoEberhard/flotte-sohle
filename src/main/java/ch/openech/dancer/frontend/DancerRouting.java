package ch.openech.dancer.frontend;

import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.Routing;

public class DancerRouting extends Routing {

	@Override
	protected Page createPage(String route) {
		if (route != null && route.startsWith("event/")) {
			String id = route.substring("event/".length());
			return new EventPage(id);
		}
		return null;
	}

	@Override
	protected String getRoute(Page page) {
		if (page instanceof EventPage) {
			return "event/" + ((EventPage) page).getId();
		}
		return null;
	}

}
