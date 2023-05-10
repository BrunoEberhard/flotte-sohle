package ch.flottesohle.backend.provider;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.minimalj.application.Application;

import ch.flottesohle.FlotteSohle;
import ch.flottesohle.backend.provider.Time2DanceCrawler;

public class Time2DanceCrawlerTest {

	@BeforeClass
	public static void setupRepository() {
		Application.setInstance(new FlotteSohle());
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
