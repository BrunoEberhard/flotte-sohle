package ch.openech.flottesohle.model;

import java.time.LocalDate;

import org.minimalj.model.Code;
import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Size;

public class SpecialDay implements Code {
	public static final SpecialDay $ = Keys.of(SpecialDay.class);

	public Integer id;

	public enum SpecialDayGroup {
		Ostern, Pfingsten, Nationalfeiertag, Weihnachten;
	}

	@NotEmpty
	public SpecialDayGroup group;

	@Size(255)
	public String name;

	public LocalDate date;

}
