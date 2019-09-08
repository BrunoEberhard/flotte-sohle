package ch.openech.dancer.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import ch.openech.dancer.backend.VerschiedeneImport.DanceAndDineImport;

public class DanceEventProviders {

	public static final Map<String, DanceEventProvider> PROVIDERS = new HashMap<>();
	public static final TreeSet<String> PROVIDER_NAMES = new TreeSet<>();

	static {
		addProvider(new DanceCubeImport());
		addProvider(new DanceInnCrawler());
		addProvider(new DancersRule());
		addProvider(new ElSocialRule());
		addProvider(new PasadenaCrawler());
		addProvider(new TanzenMitHerzCrawler());
		addProvider(new Tanzwerk101Rule());
		addProvider(new Time2DanceCrawler());
		addProvider(new AnlikerTanzRule());
		addProvider(new BanditsRule());
		addProvider(new Werk1Rule());
		addProvider(new TanzZentrumImport());
		addProvider(new TanzcenterImport());
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
	}

	private static void addProvider(DanceEventProvider provider) {
		String name = provider.getName();
		PROVIDER_NAMES.add(name);
		PROVIDERS.put(name, provider);
	}

}
