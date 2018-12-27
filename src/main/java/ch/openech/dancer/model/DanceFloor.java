package ch.openech.dancer.model;

import org.minimalj.model.annotation.Size;

public class DanceFloor {

	@Size(1000)
	public String description;

	public Boolean main;
	public Boolean mixed;
}
