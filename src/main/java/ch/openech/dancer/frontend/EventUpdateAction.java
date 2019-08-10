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
import org.minimalj.frontend.form.element.FormElement;

import ch.openech.dancer.backend.AnlikerTanzRule;
import ch.openech.dancer.backend.BadenerTanzCenterCrawler;
import ch.openech.dancer.backend.BanditsRule;
import ch.openech.dancer.backend.BlueboxConsumer;
import ch.openech.dancer.backend.DanceCubeImport;
import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.DanceInnCrawler;
import ch.openech.dancer.backend.DancePassionCrawler;
import ch.openech.dancer.backend.DanceToDanceImport;
import ch.openech.dancer.backend.DanceVisionCrawler;
import ch.openech.dancer.backend.DanceoramaCrawler;
import ch.openech.dancer.backend.DancersRule;
import ch.openech.dancer.backend.DukesRule;
import ch.openech.dancer.backend.ElSocialRule;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.backend.GalacticCrawler;
import ch.openech.dancer.backend.GaswerkEventbarRule;
import ch.openech.dancer.backend.HappyAndMadCrawler;
import ch.openech.dancer.backend.HappyDanceRule;
import ch.openech.dancer.backend.PasadenaCrawler;
import ch.openech.dancer.backend.PilatusKellerRule;
import ch.openech.dancer.backend.RyvaCrawler;
import ch.openech.dancer.backend.SchuetzenhausRule;
import ch.openech.dancer.backend.SummerDanceConsumer;
import ch.openech.dancer.backend.TanzArtImport;
import ch.openech.dancer.backend.TanzSalonCrawler;
import ch.openech.dancer.backend.TanzZentrumImport;
import ch.openech.dancer.backend.TanzbarBinningenRule;
import ch.openech.dancer.backend.TanzcenterImport;
import ch.openech.dancer.backend.TanzclubWinterthurConsumer;
import ch.openech.dancer.backend.TanzenMitHerzCrawler;
import ch.openech.dancer.backend.TanzlokalSurseeCrawler;
import ch.openech.dancer.backend.TanzschuleBayerCrawler;
import ch.openech.dancer.backend.Tanzwerk101Rule;
import ch.openech.dancer.backend.TanzwerkShCrawler;
import ch.openech.dancer.backend.Time2DanceCrawler;
import ch.openech.dancer.backend.VerschiedeneImport.DanceAndDineImport;
import ch.openech.dancer.backend.Werk1Rule;
import ch.openech.dancer.backend.ZinneSargansRule;
import ch.openech.dancer.model.AdminLog;
import ch.openech.dancer.model.AdminLog.AdminLogType;

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
		providers.add(new BlueboxConsumer());
		providers.add(new TanzSalonCrawler());
		providers.add(new DanceToDanceImport());
		providers.add(new DukesRule());
		providers.add(new TanzlokalSurseeCrawler());
		providers.add(new TanzArtImport());
		providers.add(new TanzwerkShCrawler());
		providers.add(new RyvaCrawler());
		providers.add(new DancePassionCrawler());
		providers.add(new TanzschuleBayerCrawler());
		providers.add(new HappyDanceRule());
		providers.add(new SummerDanceConsumer());
		providers.add(new HappyAndMadCrawler());
		providers.add(new TanzclubWinterthurConsumer());
		providers.add(new DanceVisionCrawler());
		providers.add(new TanzbarBinningenRule());
		providers.add(new DanceoramaCrawler());
		providers.add(new GaswerkEventbarRule());
		providers.add(new PilatusKellerRule());
		providers.add(new DanceAndDineImport());

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

	@SuppressWarnings("rawtypes")
	@Override
	public Form<Set<DanceEventProvider>> createForm() {
		int columns = 3;
		Form<Set<DanceEventProvider>> form = new Form<>(Form.EDITABLE, columns);
		FormElement[] row = new FormElement[columns];
		int pos = 0;
		for (DanceEventProvider object : providers) {
			String caption = object.getName();
			row[pos++] = new CheckBoxFormElement(new SetElementFormElementProperty(object), caption, true, false);
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
		Integer inserted = counters.stream().mapToInt(c -> c.newEvents).sum();
		Integer updated = counters.stream().mapToInt(c -> c.updatedEvents).sum();
		Backend.insert(new AdminLog(AdminLogType.IMPORT, "Import: " + inserted + " neue, " + updated + " aktualisierte Anlässe"));
		return counters;
	}

	@Override
	protected void finished(List<EventUpdateCounter> result) {
		Frontend.show(new EventUpdateTable(result));
	}
}
