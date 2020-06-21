package ch.openech.dancer.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import ch.openech.dancer.backend.provider.AllmendhofBrochImport;
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
import ch.openech.dancer.backend.provider.HappyAndMadRule;
import ch.openech.dancer.backend.provider.HappyDanceRule;
import ch.openech.dancer.backend.provider.HasenstrickRule;
import ch.openech.dancer.backend.provider.HomeOfDanceRule;
import ch.openech.dancer.backend.provider.Meet2DanceRule;
import ch.openech.dancer.backend.provider.PilatusKellerRule;
import ch.openech.dancer.backend.provider.RyvaCrawler;
import ch.openech.dancer.backend.provider.SchuetzenhausRule;
import ch.openech.dancer.backend.provider.SummerDanceConsumer;
import ch.openech.dancer.backend.provider.TanzArtImport;
import ch.openech.dancer.backend.provider.TanzSalonCrawler;
import ch.openech.dancer.backend.provider.TanzZentrumImport;
import ch.openech.dancer.backend.provider.TanzbarBinningenRule;
import ch.openech.dancer.backend.provider.TanzcenterRule;
import ch.openech.dancer.backend.provider.TanzclubAcademiaRule;
import ch.openech.dancer.backend.provider.TanzclubWinterthurConsumer;
import ch.openech.dancer.backend.provider.TanzenMitHerzCrawler;
import ch.openech.dancer.backend.provider.TanzlokalSurseeCrawler;
import ch.openech.dancer.backend.provider.TanzschuleBayerCrawler;
import ch.openech.dancer.backend.provider.TanzschuleLaederachImport;
import ch.openech.dancer.backend.provider.Tanzwerk101Rule;
import ch.openech.dancer.backend.provider.TanzwerkShCrawler;
import ch.openech.dancer.backend.provider.Time2DanceCrawler;
import ch.openech.dancer.backend.provider.VerschiedeneImport.DanceAndDineImport;
import ch.openech.dancer.backend.provider.Werk1Rule;
import ch.openech.dancer.backend.provider.WirTanzenRule;
import ch.openech.dancer.backend.provider.ZinneSargansRule;

public class DanceEventProviders {

	public static final Map<String, DanceEventProvider> PROVIDERS = new HashMap<>();
	public static final TreeSet<String> PROVIDER_NAMES = new TreeSet<>();
	public static final TreeSet<String> UPDATED_PROVIDER_NAMES = new TreeSet<>();

	static {
		addProvider(new DanceCubeImport());
		addProvider(new DanceInnCrawler(), true);
		addProvider(new DancersRule(), true);
		addProvider(new ElSocialRule());
		addProvider(new TanzenMitHerzCrawler());
		addProvider(new Tanzwerk101Rule());
		addProvider(new Time2DanceCrawler());
		addProvider(new AnlikerTanzRule(), true);
		addProvider(new BanditsRule(), true);
		addProvider(new Werk1Rule(), true);
		addProvider(new TanzZentrumImport());
		addProvider(new GalacticCrawler());
		addProvider(new ZinneSargansRule());
		addProvider(new BadenerTanzCenterCrawler());
		addProvider(new SchuetzenhausRule());
		addProvider(new BlueboxConsumer(), true);
		addProvider(new TanzSalonCrawler());
		addProvider(new DanceToDanceImport());
		addProvider(new DukesRule(), true);
		addProvider(new TanzlokalSurseeCrawler());
		addProvider(new TanzArtImport());
		addProvider(new TanzwerkShCrawler());
		addProvider(new RyvaCrawler());
		addProvider(new DancePassionCrawler(), true);
		addProvider(new TanzschuleBayerCrawler());
		addProvider(new HappyDanceRule());
		addProvider(new SummerDanceConsumer(), true);
		addProvider(new HappyAndMadRule(), true);
		addProvider(new TanzclubWinterthurConsumer());
		addProvider(new DanceVisionCrawler(), true);
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
		addProvider(new DancersWorldImport(), true);
		addProvider(new TanzcenterRule(), true);
		addProvider(new TanzclubAcademiaRule());
		addProvider(new AllmendhofBrochImport());
		addProvider(new WirTanzenRule(), true); // bis 12.8.2020
		addProvider(new TanzschuleLaederachImport(), true);
	}

	private static void addProvider(DanceEventProvider provider) {
		addProvider(provider, false);
	}
	
	private static void addProvider(DanceEventProvider provider, boolean updated) {
		String name = provider.getName();
		PROVIDER_NAMES.add(name);
		PROVIDERS.put(name, provider);
		if (updated) {
			UPDATED_PROVIDER_NAMES.add(name);
		}
	}

}
