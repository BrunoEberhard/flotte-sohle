package ch.openech.dancer.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend.FormContent;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.ImageFormElement;
import org.minimalj.frontend.form.element.TextFormElement;
import org.minimalj.frontend.page.TableFormPage;
import org.minimalj.model.Keys;
import org.minimalj.model.annotation.Size;
import org.minimalj.repository.query.By;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.model.DanceEvent;

public class DanceEventTablePage extends TableFormPage<DanceEvent> {

	private static final Object[] KEYS = new Object[] { DanceEvent.$.date, DanceEvent.$.getFromUntil(), DanceEvent.$.title, DanceEvent.$.location.name };

	public DanceEventTablePage() {
		super(KEYS);
	}

	private Form<DanceEventTableFilter> filterForm;
	private DanceEventTableFilter filter = new DanceEventTableFilter();

	public static class DanceEventTableFilter {
		public static final DanceEventTableFilter $ = Keys.of(DanceEventTableFilter.class);
		@Size(100)
		public String filter;
	}

	@Override
	protected FormContent getOverview() {
		if (filterForm == null) {
			filterForm = new Form<>();
			filterForm.line(DanceEventTableFilter.$.filter);
			filterForm.setChangeListener(form -> refresh());
			filterForm.setObject(filter);
		}
		return filterForm.getContent();
	}

	@Override
	protected List<DanceEvent> load() {
		if (!StringUtils.isEmpty(filter.filter)) {
			return Backend.find(DanceEvent.class, By.search(filter.filter));
		} else {
			return Backend.find(DanceEvent.class, By.ALL);
		}
	}

	protected Form<DanceEvent> createForm() {
		Form<DanceEvent> form = new Form<>(Form.READ_ONLY, 2);
		form.line(DanceEvent.$.date);
		form.line(DanceEvent.$.from, DanceEvent.$.until);
		form.line(DanceEvent.$.title);
		form.line(DanceEvent.$.description);
		form.line(new ImageFormElement(DanceEvent.$.flyer, Form.READ_ONLY, 3));
		form.line(new TextFormElement(DanceEvent.$.location));
		form.line(DanceEvent.$.tags);
		form.line(DanceEvent.$.status);
		return form;
	}

}