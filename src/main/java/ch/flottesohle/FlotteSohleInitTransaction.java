package ch.flottesohle;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;
import org.minimalj.transaction.Transaction;
import org.minimalj.util.CsvReader;

import ch.flottesohle.model.DeeJay;

public class FlotteSohleInitTransaction implements Transaction<Void> {

	private static final long serialVersionUID = 1L;

	@Override
	public Void execute() {
		
		CsvReader reader = new CsvReader(getClass().getResourceAsStream("/data/deejays.csv"));
		for (DeeJay deeJay : reader.readValues(DeeJay.class)) {
			List<DeeJay> existingDeeJay = Backend.find(DeeJay.class, By.field(DeeJay.$.name, deeJay.name));
			if (existingDeeJay.isEmpty()) {
				Backend.insert(deeJay);
			} else {
				DeeJay updateDeeJay = existingDeeJay.get(0);
				updateDeeJay.description = deeJay.description;
				updateDeeJay.url = deeJay.url;
				Backend.update(updateDeeJay);
			}
		}

		return null;
	}

}
