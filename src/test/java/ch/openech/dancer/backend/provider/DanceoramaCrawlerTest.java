package ch.openech.dancer.backend.provider;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.minimalj.application.Application;
import org.minimalj.application.ThreadLocalApplication;

import ch.openech.dancer.DancerApplication;
import ch.openech.dancer.backend.provider.DanceoramaCrawler;

public class DanceoramaCrawlerTest {

	@BeforeClass
	public static void setupRepository() {
		Application.setInstance(new ThreadLocalApplication());

		((ThreadLocalApplication) Application.getInstance()).setCurrentApplication(new DancerApplication());
	}

	@Test
	public void test() throws IOException {
		DanceoramaCrawler crawler = new DanceoramaCrawler();
		InputStream s = getClass().getResourceAsStream("danceorama.html");
		Document doc = Jsoup.parse(s, "UTF-8", DanceoramaCrawler.AGENDA_URL);
		crawler.updateEvents(doc);
	}
}
