package ch.flottesohle.backend.provider;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class SilkkCrawlerTest {

	@Test
	public void testDate() {
		Assert.assertEquals(LocalDate.of(2023, 8, 1), SilkkCrawler.parseDate("01", "AUG", "2023"));
		Assert.assertEquals(LocalDate.of(2023, 1, 11), SilkkCrawler.parseDate("11", "JAN", "2023"));
		Assert.assertEquals(LocalDate.of(2023, 3, 31), SilkkCrawler.parseDate("31", "MAR", "23"));
	}
	
}
