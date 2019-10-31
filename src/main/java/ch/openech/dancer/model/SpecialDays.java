package ch.openech.dancer.model;

import org.minimalj.model.Keys;

public class SpecialDays {
	public static final SpecialDays $ = Keys.of(SpecialDays.class);

	public Object id;

	public SpecialDay dec23, dec24, dec25, dec26, dec27, dec28, dec29, dec30, dec31, jan1, jan2;

	public SpecialDay karfreitag, ostersamstag, ostersonntag, ostermontag;

	public SpecialDay pfingstmontag;

	public SpecialDay aug1;

}
