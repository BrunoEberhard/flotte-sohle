package ch.openech.dancer.frontend;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.minimalj.application.Application;
import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.Routing;
import org.minimalj.util.StringUtils;

public class DancerRouting extends Routing {
	private static final Logger logger = Logger.getLogger(Routing.class.getName());

	@Override
	protected Page createPage(String route) {
		try {
			if (route != null && route.startsWith("event/")) {
				String id = route.substring("event/".length());
				return new EventPage(id);
			} else if (StringUtils.equals(route, "map")) {
				return new LocationMapPage();
			}
		} catch (Exception x) {
			logger.log(Level.WARNING, x.getClass().getSimpleName() + ": " + route);

		}
		return Application.getInstance().createDefaultPage();
	}

	@Override
	protected String getRoute(Page page) {
		if (page instanceof EventPage) {
			return "event/" + ((EventPage) page).getId();
		}
		return null;
	}

}
