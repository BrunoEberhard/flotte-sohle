package ch.openech.dancer.frontend;

import org.minimalj.frontend.impl.web.WebApplicationPage;
import org.minimalj.transaction.Role;

@Role("admin")
public class DancerWebApplicationPage extends WebApplicationPage {

	public DancerWebApplicationPage(String route) {
		super(route);
	}

}
