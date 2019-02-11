package ch.openech.dancer;

import org.minimalj.backend.Backend;
import org.minimalj.transaction.Transaction;
import org.minimalj.util.CsvReader;

import ch.openech.dancer.model.DeeJay;

public class DancerInitTransaction implements Transaction<Void> {

	private static final long serialVersionUID = 1L;

	@Override
	public Void execute() {
		
		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/ch/openech/dancer/data/deejays.csv"));
		for (DeeJay deeJay : reader.readValues(DeeJay.class)) {
			Backend.insert(deeJay);
		}

		return null;
	}

}
