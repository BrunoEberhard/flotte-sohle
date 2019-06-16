package ch.openech.dancer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.minimalj.application.Application;
import org.minimalj.application.Configuration;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.Frontend.IContent;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.impl.json.JsonFrontend;
import org.minimalj.frontend.impl.json.JsonHtmlContent;
import org.minimalj.frontend.impl.web.MjWebDaemon;
import org.minimalj.frontend.page.Page;
import org.minimalj.frontend.page.PageAction;
import org.minimalj.frontend.page.Routing;
import org.minimalj.model.test.ModelTest;
import org.minimalj.util.StringUtils;
import org.minimalj.util.resources.Resources;

import ch.openech.dancer.frontend.EventPage;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class DancerWebServer {
	private static final Logger LOG = Logger.getLogger(DancerWebServer.class.getName());
	
	private static final int TIME_OUT = 5 * 60 * 1000;
	
	private static int getPort() {
		String portString = Configuration.get("MjFrontendPort", "8080");
		return !StringUtils.isEmpty(portString) ? Integer.valueOf(portString) : -1 ;
	}
	
	public static void start(Application application) {
		Application.setInstance(application);
		ModelTest.exitIfProblems();
		Frontend.setInstance(new JsonFrontend());
		
		int port = getPort();
		if (port > 0) {
			LOG.info("Start " + Application.getInstance().getClass().getSimpleName() + " web frontend on port " + port);
			NanoHTTPD nanoHTTPD = new DancerWebDaemon(port);
			try {
				nanoHTTPD.start(TIME_OUT, false); // false -> this will not start a 'java' daemon, but a normal thread which keeps JVM alive
			} catch (IOException x) {
				throw new RuntimeException(x);
			}
		}
	}
	
	public static class DancerWebDaemon extends MjWebDaemon {

		public DancerWebDaemon(int port) {
			super(port, false);
		}
		
		@Override
		public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms,
				Map<String, String> files) {
			if (uri.equals("/")) {
				return newFixedLengthResponse(Status.OK, "text/html", getStatic(Application.getInstance().createDefaultPage(), uri, headers));
			}
			if (uri.startsWith("/") && uri.endsWith(".html")) {
				String path = uri.substring(1, uri.length() - 5);
				return newFixedLengthResponse(Status.OK, "text/html", getStatic(Routing.createPageSafe(path), path, headers));
			}
			if (uri.equals("/query")) {
				Page page = Application.getInstance().createSearchPage(parms.get("query"));
				return newFixedLengthResponse(Status.OK, "text/html", getStatic(page, uri, headers));
			}
			if (uri.startsWith("/event/")) {
				String id = uri.substring(7);
				Page page = new EventPage(id);
				return newFixedLengthResponse(Status.OK, "text/html", getStatic(page, uri, headers));
			}
			return super.serve(uri, method, headers, parms, files);
		}
		
	}
	
	private static String getStatic(Page page, String path, Map<String, String> headers) {
		String htmlTemplate = JsonFrontend.readStream(JsonFrontend.class.getResourceAsStream("/static.html"));
		Locale locale = MjWebDaemon.getLocale(headers.get("accept-language"));
		String html = fillPlaceHolder(htmlTemplate, locale, page, path);
		return html;
	}
	
	private static String fillPlaceHolder(String html, Locale locale, Page page, String path) {
		String result = html.replace("$NAVIGATION", createNavigation());
		String content = "";
		if (page != null) {
			IContent c = page.getContent();
			if (c instanceof JsonHtmlContent) {
				content = (String) ((JsonHtmlContent) c).get("htmlOrUrl");
			}
		}
		result = result.replace("$PAGE", content);
		return JsonFrontend.fillPlaceHolder(result, locale, path);
	}

	private static CharSequence createNavigation() {
		StringBuilder s = new StringBuilder();
		s.append("<ul>");
		List<Action> actions = Application.getInstance().getNavigation();
		for (Action action : actions) {
			if (action instanceof PageAction) {
				PageAction pageAction = (PageAction) action;
				Page page = pageAction.getPage();
				String url = Routing.getRouteSafe(page);
				if (url != null) {
					s.append("<li><a href=\"/").append(url).append(".html\">");
					s.append(page.getTitle()).append("</a></li>\n");
				}
			}
		}
		s.append("</ul>");
		return s;
	}
	
}