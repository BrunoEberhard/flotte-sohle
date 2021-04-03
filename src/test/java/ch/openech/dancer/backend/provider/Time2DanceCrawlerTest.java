package ch.openech.dancer.backend.provider;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.minimalj.application.Application;

import ch.openech.dancer.DancerApplication;

public class Time2DanceCrawlerTest {

	@BeforeClass
	public static void setupRepository() {
		Application.setInstance(new DancerApplication());
	}
	
	@AfterClass
	public static void afterClass() {
		TestUtil.shutdown();
	}

	@Test
	@Ignore
	public void test() throws IOException {
		Time2DanceCrawler crawler = new Time2DanceCrawler();
		crawler.updateEvents();
	}
}
