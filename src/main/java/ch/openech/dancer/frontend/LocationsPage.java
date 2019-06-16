package ch.openech.dancer.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.page.HtmlPage;
import org.minimalj.repository.query.By;

import ch.openech.dancer.model.Location;

public class LocationsPage extends HtmlPage {

	public LocationsPage() {
		super(createHtml());
	}

	private static String createHtml() {
		List<Location> locations = Backend.find(Location.class, By.ALL.order(Location.$.name));
		StringBuilder s = new StringBuilder();
		s.append("<html><body><h1>Veranstalter</h1><ul>");
		for (Location location : locations) {
			s.append("<li>").append(location.name).append(" ").append(location.address).append(" ").append(location.city);
			if (!"Schweiz".equals(location.country)) {
				s.append(" ").append(location.country);
			}
			s.append(" <a href=\"").append(location.url).append("\"/>").append(location.url).append("</a>");
			s.append("</li>");
		}
		s.append("</ul></body></html>");
		return s.toString();
	}

}
