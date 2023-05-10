package ch.flottesohle.model;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import ch.flottesohle.model.Location.Closing;

public class ClosingTest {

	@Test
	public void testClosing() {
		Closing closing = new Closing();
		closing.from = LocalDate.of(2020, 10, 9);
		closing.until = LocalDate.of(2020, 10, 10);
		Assert.assertFalse(closing.isClosed(LocalDate.of(2020, 10, 8)));
		Assert.assertTrue(closing.isClosed(LocalDate.of(2020, 10, 9)));
		Assert.assertTrue(closing.isClosed(LocalDate.of(2020, 10, 10)));
		Assert.assertFalse(closing.isClosed(LocalDate.of(2020, 10, 11)));
	}

	@Test
	public void testClosingWithoutFrom() {
		Closing closing = new Closing();
		closing.until = LocalDate.of(2020, 10, 10);
		Assert.assertTrue(closing.isClosed(LocalDate.of(2020, 10, 9)));
		Assert.assertTrue(closing.isClosed(LocalDate.of(2020, 10, 10)));
		Assert.assertFalse(closing.isClosed(LocalDate.of(2020, 10, 11)));
	}

	@Test
	public void testClosingWithoutUntil() {
		Closing closing = new Closing();
		closing.from = LocalDate.of(2020, 10, 10);
		Assert.assertFalse(closing.isClosed(LocalDate.of(2020, 10, 9)));
		Assert.assertTrue(closing.isClosed(LocalDate.of(2020, 10, 10)));
		Assert.assertTrue(closing.isClosed(LocalDate.of(2020, 10, 11)));
	}

}
