package ch.openech.flottesohle.backend;

import java.time.LocalDate;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.transaction.Transaction;

import ch.openech.flottesohle.model.DanceEvent;

public class EventsDeleteTransaction implements Transaction<Integer> {
	private static final long serialVersionUID = 1L;

	private final LocalDate date;

	public EventsDeleteTransaction(LocalDate localDate) {
		this.date = localDate;
	}

	@Override
	public Integer execute() {
		return Backend.delete(DanceEvent.class, By.field(DanceEvent.$.date, FieldOperator.lessOrEqual, date));
	}

}
