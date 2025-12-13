package ch.flottesohle.backend.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.junit.Test;

public class DanceLoungeCrawlerTest {

	@Test
	public void extractFrom() {
		assertEquals(LocalTime.of(21, 01), DanceLoungeCrawler.extractFrom("13. Februar 2026 @ 21:01 - 1:02"));
		assertEquals(LocalTime.of(18, 30), DanceLoungeCrawler.extractFrom("20. Dezember @ 18:30"));
	}

	@Test
	public void extractUntil() {
		assertEquals(LocalTime.of(1, 2), DanceLoungeCrawler.extractUntil("13. Februar 2026 @ 21:01 - 1:02 "));
		assertNull(DanceLoungeCrawler.extractUntil(" 20. Dezember @ 18:30"));
	}
	
	@Test
	public void extractPrice() {
		assertEquals(0, DanceLoungeCrawler.extractPrice(" CHF 10 ").compareTo(BigDecimal.valueOf(10)));
		assertEquals(0, DanceLoungeCrawler.extractPrice("CHF 20.5").compareTo(BigDecimal.valueOf(20.5)));
	}

}
