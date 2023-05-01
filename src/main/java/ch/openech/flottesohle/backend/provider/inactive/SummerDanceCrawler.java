package ch.openech.flottesohle.backend.provider.inactive;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;

import ch.openech.flottesohle.backend.DanceEventProvider;
import ch.openech.flottesohle.backend.EventUpdateCounter;
import ch.openech.flottesohle.model.DanceEvent;
import ch.openech.flottesohle.model.EventStatus;
import ch.openech.flottesohle.model.Location;

public class SummerDanceCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://www.tanzagenda.ch/_info/customDataLoader/eventsData.php?get=data";
	private LocalDate date = null; // hack: updateEvents not thread safe
	
	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();
		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		// Leider existieren 2 Element mit der ID eventsList
		Elements listItems = doc.getElementsByTag("li");
		listItems.forEach(item -> {
			try {
				if (item.classNames().contains("date")) {
					String datumText = item.text();
					int pos = 0;
					while (pos < datumText.length() && !Character.isDigit(datumText.charAt(pos))) {
						pos++;
					}
					if (pos < datumText.length()) {
						datumText = datumText.substring(pos);
						date = LocalDate.parse(datumText, LONG_DATE_FORMAT);
					}
				} else {
					String text = item.text();
					if (text.toLowerCase().contains("summerdance")) {
						Location location = null;
						BigDecimal price = null;
						for (Map.Entry<String, Location> entry : locations.entrySet()) {
							if (text.contains(entry.getKey())) {
								location = entry.getValue();
								price = prices.get(entry.getKey());
								break;
							}
						}

						if (location != null) {
							Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

							DanceEvent danceEvent = danceEventOptional.orElseGet(() -> new DanceEvent());
							if (text.contains("19:00")) {
								danceEvent.from = LocalTime.of(19, 0);
							} else {
								danceEvent.from = LocalTime.of(19, 30);
							}
							danceEvent.location = location;
							danceEvent.price = price;
							danceEvent.status = EventStatus.generated;
							danceEvent.date = date;
							danceEvent.line = "Summerdance";

							save(danceEvent, result);
						} else {
							// System.out.println("Not found: " + text);
						}
					}
				}
			} catch (Exception x) {
				result.failedEvents++;
			}
		});
		return result;
	}
	
	private Map<String, Location> locations = new HashMap<>();
	private Map<String, BigDecimal> prices = new HashMap<>();
	
//	@Override
//	protected void initData() {
//		Location location = new Location();
//		location.url = "https://www.summerdance.ch/";
//		location.country = "Schweiz";
//		
//		location.name = "Hasenstrick";
//		location.address = "Höhenstrasse 15";
//		location.city = "8635 Dürnten";
//		location.region.add(Region.ZH);
//		locations.put(location.name, save(location));
//		prices.put(location.name, BigDecimal.valueOf(12));
//		
//		location.name = "Bananenreiferei";
//		location.address = "Pfingstweidstrasse 101";
//		location.city = "8005 Zürich";
//		locations.put(location.name, save(location));
//		prices.put(location.name, BigDecimal.valueOf(12));
//
//		location.name = "Hangar";
//		location.address = "Flugplatzstrasse";
//		location.city = "5632 Buttwil";
//		location.region.clear();
//		location.region.add(Region.AG);
//		locations.put(location.name, save(location));
//		prices.put(location.name, BigDecimal.valueOf(16));
//		
//		location.name = "Bad Gutenburg";
//		location.address = "Huttwilstrasse 108";
//		location.city = "4932 Lotzwil";
//		location.region.clear();
//		location.region.add(Region.BE);
//		locations.put(location.name, save(location));
//		prices.put(location.name, BigDecimal.valueOf(16));
//
//		location.name = "Soho";
//		location.address = "Wangenstrasse 45";
//		location.city = "4537 Wiedlisbach";
//		locations.put(location.name, save(location));
//		prices.put(location.name, BigDecimal.valueOf(12));
//
//		location.name = "Neptun";
//		location.address = "Kasernenstrasse 10";
//		location.city = "8880 Walenstadt";
//		location.region.clear();
//		location.region.add(Region.SG);
//		locations.put(location.name, save(location));
//		prices.put(location.name, BigDecimal.valueOf(16));
//	}

	@Override
	public Location createLocation() {
		// nicht verwendet, Summer dance findet an verschiedenen Orten statt
		return null;
	}

}