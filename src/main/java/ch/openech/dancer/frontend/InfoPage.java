package ch.openech.dancer.frontend;

import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.page.HtmlPage;

public class InfoPage extends HtmlPage {

	private static String template;
	static {
		template = JsonFrontend.readStream(InfoPage.class.getResourceAsStream("/ch/openech/dancer/infos.html"));
	}

	public InfoPage() {
		super(template, "Infos");
	}

}
