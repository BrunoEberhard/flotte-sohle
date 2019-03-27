package ch.openech.dancer.frontend;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.editor.Editor.NewObjectEditor;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.CheckBoxFormElement;
import org.minimalj.frontend.form.element.CheckBoxFormElement.SetElementFormElementProperty;
import org.minimalj.util.resources.Resources;

import ch.openech.dancer.backend.AnlikerTanzRule;
import ch.openech.dancer.backend.BanditsRule;
import ch.openech.dancer.backend.DanceCubeImport;
import ch.openech.dancer.backend.DanceEventCrawler;
import ch.openech.dancer.backend.DanceInnCrawler;
import ch.openech.dancer.backend.DancersRule;
import ch.openech.dancer.backend.ElSocialRule;
import ch.openech.dancer.backend.GalacticCrawler;
import ch.openech.dancer.backend.PasadenaCrawler;
import ch.openech.dancer.backend.TanzZentrumImport;
import ch.openech.dancer.backend.TanzcenterImport;
import ch.openech.dancer.backend.TanzenMitHerzCrawler;
import ch.openech.dancer.backend.Tanzwerk101Rule;
import ch.openech.dancer.backend.Time2DanceCrawler;
import ch.openech.dancer.backend.Werk1Rule;

public class EventCreationAction extends NewObjectEditor<Set<DanceEventCrawler>> {

	public static final Set<DanceEventCrawler> crawlers = new HashSet<>();

	static {
		crawlers.add(new DanceCubeImport());
		crawlers.add(new DanceInnCrawler());
		crawlers.add(new DancersRule());
		crawlers.add(new ElSocialRule());
		crawlers.add(new PasadenaCrawler());
		crawlers.add(new TanzenMitHerzCrawler());
		crawlers.add(new Tanzwerk101Rule());
		crawlers.add(new Time2DanceCrawler());
		crawlers.add(new AnlikerTanzRule());
		crawlers.add(new BanditsRule());
		crawlers.add(new Werk1Rule());
		crawlers.add(new TanzZentrumImport());
		crawlers.add(new TanzcenterImport());
		crawlers.add(new GalacticCrawler());
	}

	@Override
	protected List<Action> createAdditionalActions() {
		return Collections.singletonList(new AllNoneAction());
	}

	@Override
	public Set<DanceEventCrawler> createObject() {
		return new HashSet<DanceEventCrawler>(crawlers);
	}

	protected Class<?> getEditedClass() {
		return DanceEventCrawler.class;
	}

	@Override
	public Form<Set<DanceEventCrawler>> createForm() {
		Form<Set<DanceEventCrawler>> form = new Form<>(Form.EDITABLE, 2);
		org.minimalj.frontend.form.element.FormElement<?> leftElement = null;
		for (Object object : crawlers) {
			String text = Resources.getString(object.getClass().getSimpleName(), Resources.OPTIONAL);
			String caption = text != null ? text : object.toString();
			if (leftElement != null) {
				form.line(leftElement, new CheckBoxFormElement(new SetElementFormElementProperty(object), caption, true, false));
				leftElement = null;
			} else {
				leftElement = new CheckBoxFormElement(new SetElementFormElementProperty(object), caption, true, false);
			}
		}
		if (leftElement != null) {
			form.line(leftElement);
		}
		return form;
	}

	private class AllNoneAction extends Action {
		@Override
		public void action() {
			Set<DanceEventCrawler> set = getObject();
			if (set.size() < crawlers.size() / 2) {
				set.addAll(crawlers);
			} else {
				set.clear();
			}
			objectChanged();
		}
	}

	@Override
	protected Set<DanceEventCrawler> save(Set<DanceEventCrawler> selected) {
		for (DanceEventCrawler crawler : selected) {
			Backend.execute(crawler);
		}
		return selected;
	}
}
