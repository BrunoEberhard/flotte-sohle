package ch.flottesohle.model;

import java.time.LocalDate;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;

public class AccessCounter {
	public static final AccessCounter $ = Keys.of(AccessCounter.class);

	public Object id;

	@NotEmpty
	public LocalDate date;

	@NotEmpty
	public Integer count;

}
