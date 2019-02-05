package ch.openech.dancer.backend;

import org.junit.BeforeClass;
import org.junit.Test;
import org.minimalj.application.Application;
import org.minimalj.application.ThreadLocalApplication;

import ch.openech.dancer.DancerApplication;

public class Time2DanceCrawlerTest {

	@BeforeClass
	public static void setupRepository() {
		Application.setInstance(new ThreadLocalApplication());

		((ThreadLocalApplication) Application.getInstance()).setCurrentApplication(new DancerApplication());
	}

	@Test
	public void test() {
		Time2DanceCrawler crawler = new Time2DanceCrawler();
		crawler.crawlEvents();
	}
}