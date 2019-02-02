package ch.openech.dancer.frontend;

import java.util.HashSet;
import java.util.Set;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.editor.Editor.NewObjectEditor;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.CheckBoxFormElement;
import org.minimalj.frontend.form.element.CheckBoxFormElement.SetElementFormElementProperty;

import ch.openech.dancer.backend.DanceCubeImport;
import ch.openech.dancer.backend.DanceEventCrawler;
import ch.openech.dancer.backend.DanceInnCrawler;
import ch.openech.dancer.backend.DancersRule;
import ch.openech.dancer.backend.ElSocialRule;
import ch.openech.dancer.backend.PasadenaCrawler;
import ch.openech.dancer.backend.TanzenMitHerzCrawler;
import ch.openech.dancer.backend.Tanzwerk101Rule;
import ch.openech.dancer.backend.Time2DanceCrawler;

public class StartCrawlerAction extends NewObjectEditor<Set<DanceEventCrawler>> {

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
		Form<Set<DanceEventCrawler>> form = new Form<>(true);
		for (Object object : crawlers) {
			form.lineWithoutCaption(new CheckBoxFormElement(new SetElementFormElementProperty(object), object.toString(), true));
		}
		return form;
	}

	@Override
	protected Set<DanceEventCrawler> save(Set<DanceEventCrawler> selected) {
		for (DanceEventCrawler crawler : selected) {
			Backend.execute(crawler);
		}
		return selected;
	}
}
