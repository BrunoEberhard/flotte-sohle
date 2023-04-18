package ch.openech.flottesohle.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import ch.openech.flottesohle.backend.provider.AllmendhofBrochImport;
import ch.openech.flottesohle.backend.provider.AnlikerTanzRule;
import ch.openech.flottesohle.backend.provider.BadenerTanzCenterCrawler;
import ch.openech.flottesohle.backend.provider.BallroomDancingImport;
import ch.openech.flottesohle.backend.provider.BananenreifereiRule;
import ch.openech.flottesohle.backend.provider.BanditsRule;
import ch.openech.flottesohle.backend.provider.BlueboxConsumer;
import ch.openech.flottesohle.backend.provider.ChesselhuusCrawler;
import ch.openech.flottesohle.backend.provider.ChezGeorgesRule;
import ch.openech.flottesohle.backend.provider.DanceCubeImport;
import ch.openech.flottesohle.backend.provider.DanceInnCrawler;
import ch.openech.flottesohle.backend.provider.DancePassionCrawler;
import ch.openech.flottesohle.backend.provider.DanceVisionImport;
import ch.openech.flottesohle.backend.provider.DanceoramaCrawler;
import ch.openech.flottesohle.backend.provider.DancersRule;
import ch.openech.flottesohle.backend.provider.DancersWorldImport;
import ch.openech.flottesohle.backend.provider.DieTanzHalleImport;
import ch.openech.flottesohle.backend.provider.DorfnaeschtRule;
import ch.openech.flottesohle.backend.provider.DukesRule;
import ch.openech.flottesohle.backend.provider.FelsenbarChurImport;
import ch.openech.flottesohle.backend.provider.GalacticCrawler;
import ch.openech.flottesohle.backend.provider.GaswerkEventbarRule;
import ch.openech.flottesohle.backend.provider.HappyAndMadRule;
import ch.openech.flottesohle.backend.provider.HappyDanceDuedingenImport;
import ch.openech.flottesohle.backend.provider.HappyDanceRule;
import ch.openech.flottesohle.backend.provider.HasenstrickRule;
import ch.openech.flottesohle.backend.provider.HomeOfDanceRule;
import ch.openech.flottesohle.backend.provider.Meet2DanceRule;
import ch.openech.flottesohle.backend.provider.PilatusKellerRule;
import ch.openech.flottesohle.backend.provider.PrimaLocationCrawler;
import ch.openech.flottesohle.backend.provider.RyvaCrawler;
import ch.openech.flottesohle.backend.provider.SaborLatinoImport;
import ch.openech.flottesohle.backend.provider.SchuetzenhausRule;
import ch.openech.flottesohle.backend.provider.SummerDanceCrawler;
import ch.openech.flottesohle.backend.provider.TanzArtImport;
import ch.openech.flottesohle.backend.provider.TanzSalonCrawler;
import ch.openech.flottesohle.backend.provider.TanzZentrumImport;
import ch.openech.flottesohle.backend.provider.TanzbarBinningenRule;
import ch.openech.flottesohle.backend.provider.TanzcenterRule;
import ch.openech.flottesohle.backend.provider.TanzclubAcademiaRule;
import ch.openech.flottesohle.backend.provider.TanzclubWinterthurConsumer;
import ch.openech.flottesohle.backend.provider.TanzenMitHerzImport;
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

	static {
		// crawler
 		addProvider(new DanceInnCrawler());
		addProvider(new BlueboxConsumer());
		addProvider(new TanzclubWinterthurConsumer());
		addProvider(new ChesselhuusCrawler());
		addProvider(new TanzSalonCrawler());
		addProvider(new PrimaLocationCrawler());
		addProvider(new DancePassionCrawler());
		addProvider(new TanzschuleBayerCrawler());
		addProvider(new TanzwerkShCrawler());

		// crawler (tot)
		addProvider(new Time2DanceCrawler());
		addProvider(new GalacticCrawler()); // (keine Events)
		addProvider(new BadenerTanzCenterCrawler());  // (Nur noch So Nachmittag events)		
		
		
		// funktionierende Rules
		addProvider(new DancersRule());
		addProvider(new Tanzwerk101Rule());
		addProvider(new BanditsRule());
		addProvider(new SchuetzenhausRule());
		addProvider(new TanzclubAcademiaRule());
		addProvider(new Meet2DanceRule());
		addProvider(new HomeOfDanceRule());
		addProvider(new HappyAndMadRule());
		addProvider(new BananenreifereiRule());
		addProvider(new ChezGeorgesRule());
		addProvider(new DorfnaeschtRule());
		
		// abgeschaltete Rules
		addProvider(new AnlikerTanzRule());
		addProvider(new Werk1Rule());
		addProvider(new TanzbarBinningenRule()); // (scheint nicht mehr zu stimmen)
		addProvider(new WirTanzenRule());
		addProvider(new ZinneSargansRule());
		addProvider(new HappyDanceRule()); // (tod?)

		
		// imports	
		addProvider(new DanceCubeImport());
		addProvider(new TanzenMitHerzImport());
		addProvider(new TanzZentrumImport());
		addProvider(new TanzArtImport());
		addProvider(new BallroomDancingImport());
		addProvider(new TanzcenterRule());
		addProvider(new DanceVisionImport());
		addProvider(new DanceAndDineImport());
		addProvider(new HappyDanceDuedingenImport());
		addProvider(new DancersWorldImport());
		addProvider(new FelsenbarChurImport());

		// imports (veraltet)
		addProvider(new AllmendhofBrochImport());
		addProvider(new DieTanzHalleImport());
		addProvider(new SaborLatinoImport());
		addProvider(new TanzschuleLaederachImport());


		// TODO
		addProvider(new DukesRule());
		addProvider(new RyvaCrawler()); // (da stimmt noch was nicht mit den request)
		addProvider(new SummerDanceCrawler());
		addProvider(new DanceoramaCrawler());
		addProvider(new GaswerkEventbarRule());
		addProvider(new PilatusKellerRule());
		addProvider(new HasenstrickRule());
	}

	private static void addProvider(DanceEventProvider provider) {
		String name = provider.getName();
		PROVIDER_NAMES.add(name);
		PROVIDERS.put(name, provider);
	}
	/*
	 * public Set<Class> findAllClassesUsingReflectionsLibrary(String packageName) {
	 * Reflections reflections = new Reflections(packageName, new
	 * SubTypesScanner(false)); return reflections.getSubTypesOf(Object.class)
	 * .stream() .collect(Collectors.toSet()); }
	 */

}
