package ch.openech.dancer.frontend;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.editor.Editor;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.CheckBoxFormElement;
import org.minimalj.frontend.form.element.CheckBoxFormElement.SetElementFormElementProperty;
import org.minimalj.frontend.form.element.FormElement;
import org.minimalj.transaction.Role;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.DanceEventProviders;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.backend.EventsUpdateTransaction;

@Role("admin")
public class EventUpdateAction extends Editor<TreeSet<String>, List<EventUpdateCounter>> {

	@Override
	protected List<Action> createAdditionalActions() {
		return Collections.singletonList(new AllNoneAction());
	}

	@Override
	public TreeSet<String> createObject() {
		return new TreeSet<>(DanceEventProviders.PROVIDER_NAMES);
	}

	protected Class<?> getEditedClass() {
		return DanceEventProvider.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Form<TreeSet<String>> createForm() {
		int columns = 3;
		Form<TreeSet<String>> form = new Form<>(Form.EDITABLE, columns);
		FormElement[] row = new FormElement[columns];
		int pos = 0;
		for (String object : DanceEventProviders.PROVIDER_NAMES) {
			row[pos++] = new CheckBoxFormElement(new SetElementFormElementProperty(object), object, true, false);
			if (pos == columns) {
				form.line(row);
				pos = 0;
			}
		}
		if (pos > 0) {
			FormElement[] rest = new FormElement[pos];
			System.arraycopy(row, 0, rest, 0, pos);
			form.line(rest);
		}
		return form;
	}

	private class AllNoneAction extends Action {
		@Override
		public void run() {
			TreeSet<String> set = getObject();
			if (set.size() < DanceEventProviders.PROVIDER_NAMES.size() / 2) {
				set.addAll(DanceEventProviders.PROVIDER_NAMES);
			} else {
				set.clear();
			}
			objectChanged();
		}
	}

	@Override
	protected List<EventUpdateCounter> save(TreeSet<String> selected) {
		return Backend.execute(new EventsUpdateTransaction(selected));
	}

	@Override
	protected void finished(List<EventUpdateCounter> result) {
		Frontend.show(new EventUpdateTable(result));
	}
}
