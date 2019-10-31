package ch.openech.dancer.model;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.Decimal;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Searched;
import org.minimalj.model.annotation.Size;
import org.minimalj.security.model.Password;

public class Location implements Rendering {
	public static final Location $ = Keys.of(Location.class);
	
	public Object id;
	
	@Size(255)
	@NotEmpty
	@Searched
	public String name;
	
	@Size(60)
	public String address, city, country;
	
	@Decimal(7)
	public BigDecimal latitude, longitude;

	public DanceFloorSize danceFloorSize;

	public Boolean school;

	@Size(255)
	public String url;

	public final Set<Region> region = new TreeSet<>();

	public final Password password = new Password();

	public SpecialDays specialDays = new SpecialDays();

	@Override
	public CharSequence render() {
		return name;
	}
}
