package ch.openech.flottesohle.model;

import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Searched;
import org.minimalj.model.annotation.Size;

public class DeeJay implements Rendering {
	public static final DeeJay $ = Keys.of(DeeJay.class);
	
	public Object id;
	
	@Size(255)
	@NotEmpty
	@Searched
	public String name;
	
	@Size(1000)
	public String description;
	
	@Size(255)
	public String url;

	@Override
	public CharSequence render() {
		return name;
	}
}
