package ch.flottesohle.backend;

import java.util.HashMap;
import java.util.Map;

import ch.flottesohle.backend.provider.AnlikerTanzRule;
import ch.flottesohle.backend.provider.BadenerTanzCenterCrawler;
import ch.flottesohle.backend.provider.BallroomDancingImport;
import ch.flottesohle.backend.provider.BanditsRule;
import ch.flottesohle.backend.provider.BlueboxConsumer;
import ch.flottesohle.backend.provider.ChesselhuusCrawler;
import ch.flottesohle.backend.provider.ChezGeorgesRule;
import ch.flottesohle.backend.provider.DanceCubeImport;
import ch.flottesohle.backend.provider.DanceInnCrawler;
import ch.flottesohle.backend.provider.DancePassionCrawler;
import ch.flottesohle.backend.provider.DanceVisionImport;
import ch.flottesohle.backend.provider.DanceoramaCrawler;
import ch.flottesohle.backend.provider.DancersRule;
import ch.flottesohle.backend.provider.DancersWorldImport;
import ch.flottesohle.backend.provider.DieTanzHalleImport;
import ch.flottesohle.backend.provider.DukesRule;
import ch.flottesohle.backend.provider.GaswerkEventbarRule;
import ch.flottesohle.backend.provider.HappyAndMadRule;
import ch.flottesohle.backend.provider.HappyDanceDuedingenImport;
import ch.flottesohle.backend.provider.HasenstrickRule;
import ch.flottesohle.backend.provider.HomeOfDanceRule;
import ch.flottesohle.backend.provider.Meet2DanceRule;
import ch.flottesohle.backend.provider.PilatusKellerRule;
import ch.flottesohle.backend.provider.PrimaLocationCrawler;
import ch.flottesohle.backend.provider.RyvaCrawler;
import ch.flottesohle.backend.provider.SaborLatinoImport;
import ch.flottesohle.backend.provider.SchuetzenhausRule;
import ch.flottesohle.backend.provider.TanzArtImport;
import ch.flottesohle.backend.provider.TanzSalonCrawler;
import ch.flottesohle.backend.provider.TanzZentrumImport;
import ch.flottesohle.backend.provider.TanzbarBinningenRule;
import ch.flottesohle.backend.provider.TanzcenterRule;
import ch.flottesohle.backend.provider.TanzclubAcademiaRule;
import ch.flottesohle.backend.provider.TanzclubWinterthurConsumer;
import ch.flottesohle.backend.provider.TanzenMitHerzImport;
import ch.flottesohle.backend.provider.TanzschuleBayerCrawler;
import ch.flottesohle.backend.provider.TanzschuleLaederachImport;
import ch.flottesohle.backend.provider.Tanzwerk101Rule;
import ch.flottesohle.backend.provider.TanzwerkShCrawler;
import ch.flottesohle.backend.provider.Time2DanceCrawler;
import ch.flottesohle.backend.provider.WirTanzenRule;
import ch.flottesohle.backend.provider.ZinneSargansRule;

public class DanceEventProviders {

	public static final Map<Object, DanceEventProvider> PROVIDERS_BY_LOCATION_ID = new HashMap<>();

	static {
		// crawler
		addProvider(new DanceInnCrawler(), true);
		addProvider(new BlueboxConsumer(), true);
		addProvider(new TanzclubWinterthurConsumer(), true);
		addProvider(new ChesselhuusCrawler(), true);
		addProvider(new TanzSalonCrawler());
		addProvider(new PrimaLocationCrawler());
		addProvider(new DancePassionCrawler(), true);
		addProvider(new TanzschuleBayerCrawler());
		addProvider(new TanzwerkShCrawler());

		// crawler (tot)
		addProvider(new Time2DanceCrawler());
		addProvider(new BadenerTanzCenterCrawler()); // (Nur noch So Nachmittag events)

		// funktionierende Rules
		addProvider(new DancersRule(), true);
		addProvider(new Tanzwerk101Rule(), true);
		addProvider(new BanditsRule(), true);
		addProvider(new TanzcenterRule(), true);
		addProvider(new DukesRule(), true);
		addProvider(new SchuetzenhausRule());
		addProvider(new TanzclubAcademiaRule());
		addProvider(new Meet2DanceRule());
		addProvider(new HomeOfDanceRule());
		addProvider(new HappyAndMadRule());
		addProvider(new ChezGeorgesRule());

		// abgeschaltete Rules
		addProvider(new AnlikerTanzRule());
		addProvider(new TanzbarBinningenRule()); // (scheint nicht mehr zu stimmen)
		addProvider(new WirTanzenRule());
		addProvider(new ZinneSargansRule());

		// imports
		addProvider(new DanceCubeImport());
		addProvider(new TanzenMitHerzImport());
		addProvider(new TanzZentrumImport());
		addProvider(new TanzArtImport());
		addProvider(new BallroomDancingImport());
		addProvider(new DanceVisionImport());
		addProvider(new HappyDanceDuedingenImport());
		addProvider(new DancersWorldImport());

		// imports (veraltet)
		addProvider(new DieTanzHalleImport());
		addProvider(new SaborLatinoImport());
		addProvider(new TanzschuleLaederachImport());

		// TODO
		addProvider(new RyvaCrawler()); // (da stimmt noch was nicht mit den request)
		// addProvider(new SummerDanceCrawler()); // Momentan der einzige der mehrere Location hatte
		addProvider(new DanceoramaCrawler());
		addProvider(new GaswerkEventbarRule());
		addProvider(new PilatusKellerRule());
		addProvider(new HasenstrickRule());
	}

	private static void addProvider(LocationProvider provider) {
		addProvider(provider, false);
	}

	private static void addProvider(LocationProvider provider, boolean active) {
		String name = provider.getName();
		provider.install(active);
	}

}
