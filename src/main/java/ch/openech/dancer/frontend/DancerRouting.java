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
		} else if (StringUtils.equals(route, "events")) {
			return new EventsPage();
		} else if (StringUtils.equals(route, "map")) {
			return new LocationMapPage();
		} else if (StringUtils.equals(route, "locations")) {
			return new LocationsPage();
		} else if (StringUtils.equals(route, "infos")) {
			return new InfoPage();
		}
		return null;
	}

	@Override
	protected String getRoute(Page page) {
		if (page instanceof EventPage) {
			return "event/" + ((EventPage) page).getId();
		} else if (page instanceof EventsPage) {
			return "events";
		} else if (page instanceof LocationMapPage) {
			return "map";
		} else if (page instanceof LocationsPage) {
			return "locations";
		} else if (page instanceof InfoPage) {
			return "infos";
		}
		return null;
	}

}
