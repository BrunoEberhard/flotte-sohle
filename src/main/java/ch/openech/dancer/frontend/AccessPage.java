package ch.openech.dancer.frontend;

import java.time.LocalDate;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.page.TablePage;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;

import ch.openech.dancer.model.AccessCounter;

public class AccessPage extends TablePage<AccessCounter> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { AccessCounter.$.date, AccessCounter.$.count };
	}

	@Override
	protected List<AccessCounter> load() {
		return Backend.find(AccessCounter.class, //
				By.field(AccessCounter.$.date, FieldOperator.greater, LocalDate.now().minusDays(50)) //
						.order(AccessCounter.$.date, false));
	}

}
