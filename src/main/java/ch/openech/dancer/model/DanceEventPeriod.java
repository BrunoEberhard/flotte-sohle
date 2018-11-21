package ch.openech.dancer.model;

import java.time.LocalTime;
import java.util.List;

import org.minimalj.model.Keys;
import org.minimalj.model.validation.Validation;
import org.minimalj.model.validation.ValidationMessage;

public class DanceEventPeriod implements Validation {
	
	public static final DanceEventPeriod $ = Keys.of(DanceEventPeriod.class);
	
	public LocalTime from;
	public LocalTime until;
	
	@Override
	public List<ValidationMessage> validate() {
		// TODO Auto-generated method stub
		return null;
	}

}
