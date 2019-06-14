package ch.openech.dancer.frontend;

import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.Routing;
import org.minimalj.util.StringUtils;

public class DancerRouting extends Routing {

	@Override
	protected Page createPage(String route) {
		if (route != null && route.startsWith("event/")) {
			String id = route.substring("event/".length());
			return new EventPage(id);
		} else if (StringUtils.equals(route, "map")) {
			return new LocationMapPage();
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
