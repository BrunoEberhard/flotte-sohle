package ch.openech.flottesohle.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import ch.openech.flottesohle.backend.provider.AllmendhofBrochImport;
import ch.openech.flottesohle.backend.provider.AnlikerTanzRule;
import ch.openech.flottesohle.backend.provider.BadenerTanzCenterCrawler;
import ch.openech.flottesohle.backend.provider.BallroomDancingRule;
import ch.openech.flottesohle.backend.provider.BanditsRule;
import ch.openech.flottesohle.backend.provider.BlueboxConsumer;
import ch.openech.flottesohle.backend.provider.DanceCubeImport;
import ch.openech.flottesohle.backend.provider.DanceInnCrawler;
import ch.openech.flottesohle.backend.provider.DancePassionCrawler;
import ch.openech.flottesohle.backend.provider.DanceToDanceImport;
import ch.openech.flottesohle.backend.provider.DanceVisionCrawler;
import ch.openech.flottesohle.backend.provider.DanceoramaCrawler;
import ch.openech.flottesohle.backend.provider.DancersRule;
import ch.openech.flottesohle.backend.provider.DancersWorldImport;
import ch.openech.flottesohle.backend.provider.DieTanzHalleImport;
import ch.openech.flottesohle.backend.provider.DukesRule;
import ch.openech.flottesohle.backend.provider.ElSocialRule;
import ch.openech.flottesohle.backend.provider.GalacticCrawler;
import ch.openech.flottesohle.backend.provider.GaswerkEventbarRule;
import ch.openech.flottesohle.backend.provider.HappyAndMadRule;
import ch.openech.flottesohle.backend.provider.HappyDanceRule;
import ch.openech.flottesohle.backend.provider.HasenstrickRule;
import ch.openech.flottesohle.backend.provider.HomeOfDanceRule;
import ch.openech.flottesohle.backend.provider.Meet2DanceRule;
import ch.openech.flottesohle.backend.provider.PilatusKellerRule;
import ch.openech.flottesohle.backend.provider.PrimaLocationCrawler;
import ch.openech.flottesohle.backend.provider.RyvaCrawler;
import ch.openech.flottesohle.backend.provider.SchuetzenhausRule;
import ch.openech.flottesohle.backend.provider.SummerDanceCrawler;
import ch.openech.flottesohle.backend.provider.TanzArtImport;
import ch.openech.flottesohle.backend.provider.TanzSalonCrawler;
import ch.openech.flottesohle.backend.provider.TanzZentrumImport;
import ch.openech.flottesohle.backend.provider.TanzbarBinningenRule;
import ch.openech.flottesohle.backend.provider.TanzcenterRule;
import ch.openech.flottesohle.backend.provider.TanzclubAcademiaRule;
import ch.openech.flottesohle.backend.provider.TanzclubWinterthurConsumer;
import ch.openech.flottesohle.backend.provider.TanzenMitHerzCrawler;
import ch.openech.flottesohle.backend.provider.TanzschuleBayerCrawler;
import ch.openech.flottesohle.backend.provider.TanzschuleLaederachImport;
import ch.openech.flottesohle.backend.provider.Tanzwerk101Rule;
import ch.openech.flottesohle.backend.provider.TanzwerkShCrawler;
import ch.openech.flottesohle.backend.provider.Time2DanceCrawler;
import ch.openech.flottesohle.backend.provider.VerschiedeneImport.DanceAndDineImport;
import ch.openech.flottesohle.backend.provider.Werk1Rule;
import ch.openech.flottesohle.backend.provider.WirTanzenRule;
import ch.openech.flottesohle.backend.provider.ZinneSargansRule;

public class DanceEventProviders {

	public static final Map<String, DanceEventProvider> PROVIDERS = new HashMap<>();
	public static final TreeSet<String> PROVIDER_NAMES = new TreeSet<>();
	public static final TreeSet<String> UPDATED_PROVIDER_NAMES = new TreeSet<>();

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
		addProvider(new PrimaLocationCrawler());
		addProvider(new TanzArtImport());
		addProvider(new TanzwerkShCrawler());
		addProvider(new RyvaCrawler());
		addProvider(new DancePassionCrawler());
		addProvider(new TanzschuleBayerCrawler());
		addProvider(new HappyDanceRule());
		addProvider(new SummerDanceCrawler());
		addProvider(new HappyAndMadRule());
		addProvider(new TanzclubWinterthurConsumer());
		addProvider(new DanceVisionCrawler());
		addProvider(new TanzbarBinningenRule());
		addProvider(new DanceoramaCrawler());
		addProvider(new GaswerkEventbarRule());
		addProvider(new PilatusKellerRule());
		addProvider(new DanceAndDineImport());
		addProvider(new Meet2DanceRule());
		addProvider(new HasenstrickRule());
		addProvider(new BallroomDancingRule());
		addProvider(new DieTanzHalleImport());
		addProvider(new HomeOfDanceRule());
		addProvider(new DancersWorldImport());
		addProvider(new TanzcenterRule());
		addProvider(new TanzclubAcademiaRule());
		addProvider(new AllmendhofBrochImport());
		addProvider(new WirTanzenRule());
		addProvider(new TanzschuleLaederachImport());
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
