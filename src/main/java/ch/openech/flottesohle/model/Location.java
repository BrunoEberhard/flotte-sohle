package ch.openech.flottesohle.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.Decimal;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Searched;
import org.minimalj.model.annotation.Size;
import org.minimalj.model.annotation.Width;
import org.minimalj.model.validation.Validation;
import org.minimalj.model.validation.ValidationMessage;
import org.minimalj.util.DateUtils;

public class Location implements Rendering {
	public static final Location $ = Keys.of(Location.class);
	
	public Object id;
	
	@Size(255)
	@NotEmpty
	@Searched
	@Width(Width.LARGER)
	public String name;
	
	@Size(60)
	public String address, city, country;
	
	public final Set<Region> region = new TreeSet<>();

	@Decimal(7)
	public BigDecimal latitude, longitude;

	public Boolean school;

	@Size(255)
	public String url;
	
	public ImportStatus importStatus;

	public final List<Closing> closings = new ArrayList<>();

	@Size(1000) @Width(350)
	public String comment;
	
	@Width(Width.SMALLEST)
	public transient Integer events;
	
	public transient LocalDate maxDate;
	
	@Override
	public CharSequence render() {
		return name;
	}

	public static class Closing implements Validation {
		public static final Closing $ = Keys.of(Closing.class);
		
		public LocalDate from, until;

		@Size(255)
		public String reason;
		
		public boolean isClosed(LocalDate date) {
			return (from == null || !from.isAfter(date)) && (until == null || !until.isBefore(date));
		}

		public boolean overlaps(Closing c) {
			return (from == null || c.until == null || !from.isAfter(c.until)) &&
					(until == null || c.from == null || !until.isBefore(c.from));
		}
		
		@Override
		public List<ValidationMessage> validate() {
			if (from != null && until != null && from.isAfter(until)) {
				return Validation.message($.until, "Darf nicht früher sein als Beginn");
			}
			return null;
		}
	}

	public boolean isClosed(LocalDate date) {
		return closings.stream().anyMatch(c -> c.isClosed(date));
	}
	
	public String getClosings() {
		if (Keys.isKeyObject(this)) {
			return Keys.methodOf(this, "closings", $.closings);
		}
		StringBuilder s = new StringBuilder();
		for (Closing closing : closings) {
			if (closing.until != null && closing.until.isBefore(LocalDate.now())) {
				continue;
			}
			if (closing.from == null && closing.until == null) {
				return "Geschlossen";
			}
			if (closing.from != null) {
				if (closing.until == null) {
					s.append("Ab ");
				}
				s.append(DateUtils.format(closing.from));
				if (closing.until != null) {
					s.append(" ");
				}
			}
			if (closing.until != null) {
				s.append("bis ").append(DateUtils.format(closing.until));
			}
		}
		return s.toString();
	}
}
