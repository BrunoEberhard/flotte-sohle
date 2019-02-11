package ch.openech.dancer.model;

import java.time.LocalDate;
import java.util.List;

import org.minimalj.model.Keys;
import org.minimalj.model.validation.Validation;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.util.resources.Resources;

public class Recur implements Validation {
	public static final Recur $ = Keys.of(Recur.class);
	
	public RecurFrequency recurFrequency;
	public Integer interval;
	
	public LocalDate until;
	public Integer count;
	
	@Override
	public List<ValidationMessage> validate() {
		if (until != null && count != null) {
			return Validation.message($.count, Resources.getString("Recur.validation.error"));
		}
		return null;
	}

}
