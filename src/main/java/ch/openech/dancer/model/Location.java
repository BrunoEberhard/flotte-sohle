package ch.openech.dancer.model;

import java.math.BigDecimal;

import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Size;

public class Location implements Rendering {
	public static final Location $ = Keys.of(Location.class);
	
	public Object id;
	
	@Size(255) @NotEmpty
	public String name;
	
	@Size(60)
	public String address, city, country;
	
	public BigDecimal latitude, longitude;

	@Size(255)
	public String url;

	@Override
	public CharSequence render() {
		return name;
	}
}
