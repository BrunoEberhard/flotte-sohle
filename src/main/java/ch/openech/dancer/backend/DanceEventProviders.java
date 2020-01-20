package ch.openech.dancer.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import ch.openech.dancer.backend.provider.AnlikerTanzRule;
import ch.openech.dancer.backend.provider.BadenerTanzCenterCrawler;
import ch.openech.dancer.backend.provider.BallroomDancingImport;
import ch.openech.dancer.backend.provider.BanditsRule;
import ch.openech.dancer.backend.provider.BlueboxConsumer;
import ch.openech.dancer.backend.provider.DanceCubeImport;
import ch.openech.dancer.backend.provider.DanceInnCrawler;
import ch.openech.dancer.backend.provider.DancePassionCrawler;
import ch.openech.dancer.backend.provider.DanceToDanceImport;
import ch.openech.dancer.backend.provider.DanceVisionCrawler;
import ch.openech.dancer.backend.provider.DanceoramaCrawler;
import ch.openech.dancer.backend.provider.DancersRule;
import ch.openech.dancer.backend.provider.DancersWorldImport;
import ch.openech.dancer.backend.provider.DieTanzHalleImport;
import ch.openech.dancer.backend.provider.DukesRule;
import ch.openech.dancer.backend.provider.ElSocialRule;
import ch.openech.dancer.backend.provider.GalacticCrawler;
import ch.openech.dancer.backend.provider.GaswerkEventbarRule;
import ch.openech.dancer.backend.provider.HappyAndMadCrawler;
import ch.openech.dancer.backend.provider.HappyDanceRule;
import ch.openech.dancer.backend.provider.HasenstrickRule;
import ch.openech.dancer.backend.provider.HomeOfDanceRule;
import ch.openech.dancer.backend.provider.Meet2DanceRule;
import ch.openech.dancer.backend.provider.PasadenaRule;
import ch.openech.dancer.backend.provider.PilatusKellerRule;
import ch.openech.dancer.backend.provider.RyvaCrawler;
import ch.openech.dancer.backend.provider.SchuetzenhausRule;
import ch.openech.dancer.backend.provider.SummerDanceConsumer;
import ch.openech.dancer.backend.provider.TanzArtImport;
import ch.openech.dancer.backend.provider.TanzSalonCrawler;
import ch.openech.dancer.backend.provider.TanzZentrumImport;
import ch.openech.dancer.backend.provider.TanzbarBinningenRule;
import ch.openech.dancer.backend.provider.TanzcenterRule;
import ch.openech.dancer.backend.provider.TanzclubWinterthurConsumer;
import ch.openech.dancer.backend.provider.TanzenMitHerzCrawler;
import ch.openech.dancer.backend.provider.TanzlokalSurseeCrawler;
import ch.openech.dancer.backend.provider.TanzschuleBayerCrawler;
import ch.openech.dancer.backend.provider.Tanzwerk101Rule;
import ch.openech.dancer.backend.provider.TanzwerkShCrawler;
import ch.openech.dancer.backend.provider.Time2DanceCrawler;
import ch.openech.dancer.backend.provider.VerschiedeneImport.DanceAndDineImport;
import ch.openech.dancer.backend.provider.Werk1Rule;
import ch.openech.dancer.backend.provider.ZinneSargansRule;

public class DanceEventProviders {

	public static final Map<String, DanceEventProvider> PROVIDERS = new HashMap<>();
	public static final TreeSet<String> PROVIDER_NAMES = new TreeSet<>();

	static {
		addProvider(new DanceCubeImport());
		addProvider(new DanceInnCrawler());
		addProvider(new DancersRule());
		addProvider(new ElSocialRule());
		addProvider(new TanzenMitHerzCrawler());
		addProvider(new Tanzwerk101Rule());
		addProvider(new Time2DanceCrawler());
		addProvider(new AnlikerTanzRule());
		addProvider(new BanditsRule());
		addProvider(new Werk1Rule());
		addProvider(new TanzZentrumImport());
		addProvider(new GalacticCrawler());
		addProvider(new ZinneSargansRule());
		addProvider(new BadenerTanzCenterCrawler());
		addProvider(new SchuetzenhausRule());
		addProvider(new BlueboxConsumer());
		addProvider(new TanzSalonCrawler());
		addProvider(new DanceToDanceImport());
		addProvider(new DukesRule());
		addProvider(new TanzlokalSurseeCrawler());
		addProvider(new TanzArtImport());
		addProvider(new TanzwerkShCrawler());
		addProvider(new RyvaCrawler());
		addProvider(new DancePassionCrawler());
		addProvider(new TanzschuleBayerCrawler());
		addProvider(new HappyDanceRule());
		addProvider(new SummerDanceConsumer());
		addProvider(new HappyAndMadCrawler());
		addProvider(new TanzclubWinterthurConsumer());
		addProvider(new DanceVisionCrawler());
		addProvider(new TanzbarBinningenRule());
		addProvider(new DanceoramaCrawler());
		addProvider(new GaswerkEventbarRule());
		addProvider(new PilatusKellerRule());
		addProvider(new DanceAndDineImport());
		addProvider(new Meet2DanceRule());
		addProvider(new HasenstrickRule());
		addProvider(new BallroomDancingImport());
		addProvider(new DieTanzHalleImport());
		addProvider(new HomeOfDanceRule());
		addProvider(new DancersWorldImport());
		addProvider(new PasadenaRule());
		addProvider(new TanzcenterRule());
	}

	private static void addProvider(DanceEventProvider provider) {
		String name = provider.getName();
		PROVIDER_NAMES.add(name);
		PROVIDERS.put(name, provider);
	}

}
