package ch.openech.dancer.model;

import java.time.LocalDate;

import org.minimalj.model.Keys;

public class AccessCounter {
	public static final AccessCounter $ = Keys.of(AccessCounter.class);

	public Object id;

	public LocalDate date;

	public Integer count;

}
