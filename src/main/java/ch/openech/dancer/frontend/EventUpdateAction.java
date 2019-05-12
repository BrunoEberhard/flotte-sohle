package ch.openech.dancer.frontend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.editor.Editor;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.CheckBoxFormElement;
import org.minimalj.frontend.form.element.CheckBoxFormElement.SetElementFormElementProperty;

import ch.openech.dancer.backend.AnlikerTanzRule;
import ch.openech.dancer.backend.BadenerTanzCenterCrawler;
import ch.openech.dancer.backend.BanditsRule;
import ch.openech.dancer.backend.BlueboxCrawler;
import ch.openech.dancer.backend.DanceCubeImport;
import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.DanceInnCrawler;
import ch.openech.dancer.backend.DancePassionCrawler;
import ch.openech.dancer.backend.DanceToDanceImport;
import ch.openech.dancer.backend.DancersRule;
import ch.openech.dancer.backend.DukesRule;
import ch.openech.dancer.backend.ElSocialRule;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.backend.GalacticCrawler;
import ch.openech.dancer.backend.PasadenaCrawler;
import ch.openech.dancer.backend.RyvaCrawler;
import ch.openech.dancer.backend.SchuetzenhausRule;
import ch.openech.dancer.backend.TanzArtImport;
import ch.openech.dancer.backend.TanzSalonCrawler;
import ch.openech.dancer.backend.TanzZentrumImport;
import ch.openech.dancer.backend.TanzcenterImport;
import ch.openech.dancer.backend.TanzenMitHerzCrawler;
import ch.openech.dancer.backend.TanzlokalSurseeCrawler;
import ch.openech.dancer.backend.Tanzwerk101Rule;
import ch.openech.dancer.backend.TanzwerkShCrawler;
import ch.openech.dancer.backend.Time2DanceCrawler;
import ch.openech.dancer.backend.Werk1Rule;
import ch.openech.dancer.backend.ZinneSargansRule;

public class EventUpdateAction extends Editor<Set<DanceEventProvider>, List<EventUpdateCounter>> {

	public static final List<DanceEventProvider> providers = new ArrayList<>();

	static {
		providers.add(new DanceCubeImport());
		providers.add(new DanceInnCrawler());
		providers.add(new DancersRule());
		providers.add(new ElSocialRule());
		providers.add(new PasadenaCrawler());
		providers.add(new TanzenMitHerzCrawler());
		providers.add(new Tanzwerk101Rule());
		providers.add(new Time2DanceCrawler());
		providers.add(new AnlikerTanzRule());
		providers.add(new BanditsRule());
		providers.add(new Werk1Rule());
		providers.add(new TanzZentrumImport());
		providers.add(new TanzcenterImport());
		providers.add(new GalacticCrawler());
		providers.add(new ZinneSargansRule());
		providers.add(new BadenerTanzCenterCrawler());
		providers.add(new SchuetzenhausRule());
		providers.add(new BlueboxCrawler());
		providers.add(new TanzSalonCrawler());
		providers.add(new DanceToDanceImport());
		providers.add(new DukesRule());
		providers.add(new TanzlokalSurseeCrawler());
		providers.add(new TanzArtImport());
		providers.add(new TanzwerkShCrawler());
		providers.add(new RyvaCrawler());
		providers.add(new DancePassionCrawler());

		Collections.sort(providers, Comparator.comparing(DanceEventProvider::getName));
	}

	@Override
	protected List<Action> createAdditionalActions() {
		return Collections.singletonList(new AllNoneAction());
	}

	@Override
	public Set<DanceEventProvider> createObject() {
		return new HashSet<DanceEventProvider>(providers);
	}

	protected Class<?> getEditedClass() {
		return DanceEventProvider.class;
	}

	@Override
	public Form<Set<DanceEventProvider>> createForm() {
		Form<Set<DanceEventProvider>> form = new Form<>(Form.EDITABLE, 2);
		org.minimalj.frontend.form.element.FormElement<?> leftElement = null;
		for (DanceEventProvider object : providers) {
			String caption = object.getName();
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
			Set<DanceEventProvider> set = getObject();
			if (set.size() < providers.size() / 2) {
				set.addAll(providers);
			} else {
				set.clear();
			}
			objectChanged();
		}
	}

	@Override
	protected List<EventUpdateCounter> save(Set<DanceEventProvider> selected) {
		List<EventUpdateCounter> counters = new ArrayList<EventUpdateCounter>();
		for (DanceEventProvider provider : selected) {
			EventUpdateCounter counter = Backend.execute(provider);
			counter.provider = provider.getName();
			counters.add(counter);
		}
		Collections.sort(counters, Comparator.comparing(counter -> counter.provider));
		return counters;
	}

	@Override
	protected void finished(List<EventUpdateCounter> result) {
		Frontend.show(new EventUpdateTable(result));
	}
}
