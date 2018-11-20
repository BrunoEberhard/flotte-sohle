package ch.openech.dancer.frontend;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.minimalj.frontend.form.element.FormatFormElement;
import org.minimalj.model.properties.PropertyInterface;
import org.minimalj.util.DateUtils;

import ch.openech.dancer.model.DanceEventPeriod;

public class DanceEventPeriodFormElement extends FormatFormElement<DanceEventPeriod> {
	
	private final DateTimeFormatter formatter;

	public DanceEventPeriodFormElement(PropertyInterface property, boolean editable) {
		super(property, editable);
		formatter = DateUtils.getTimeFormatter(property);
	}

	@Override
	public void mock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getAllowedCharacters(PropertyInterface property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getAllowedSize(PropertyInterface property) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected DanceEventPeriod parse(String text) {
		if (text != null) {
			String[] splitted = text.split("-");
			if (splitted.length == 2) {
				DanceEventPeriod danceEventPeriod = new DanceEventPeriod();
				danceEventPeriod.from = LocalTime.parse(splitted[0], formatter);
				danceEventPeriod.until = LocalTime.parse(splitted[1], formatter);
				return danceEventPeriod;
			}
		}
		return null;
	}

	@Override
	protected String render(DanceEventPeriod value) {
		if (value != null) {
			return formatter.format(value.from) + "-" + formatter.format(value.until);
		}
		return null;
	}

	

}
