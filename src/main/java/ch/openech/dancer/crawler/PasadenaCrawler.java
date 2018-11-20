package ch.openech.dancer.crawler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Organizer;

public class PasadenaCrawler implements DanceEventCrawler {

	private static final String PASADENA_AGENDA_URL = "http://www.pasadena.ch/m/agenda/";

	@Override
	public List<DanceEvent> crawlEvents() {
		List<DanceEvent> danceEvents = new ArrayList<>();
		Document doc;
		try {
			doc = Jsoup.connect(PASADENA_AGENDA_URL).get();
			Elements elements = doc.select("p[style]");
			elements.stream().forEach(element -> {
				DanceEvent danceEvent = new DanceEvent();
				Element dateTag = element.child(0);
				if (dateTag != null) {
					danceEvent.start = extractLocalDate(dateTag.ownText());
				}
				danceEvent.title = extractDanceEventTitle(element);
				danceEvent.description = "Desciption";
				danceEvents.add(danceEvent);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return danceEvents;
	}
	
	
	private LocalDate extractLocalDate(String monthDayStr) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd. MMMM", Locale.GERMAN);
		final int currentYear = LocalDate.now().getYear();
		return MonthDay.parse(monthDayStr, formatter).atYear(currentYear);
	}
	
	private String extractDanceEventTitle(Element paragraphElement) {
		Elements elements = paragraphElement.nextElementSibling().getElementsByTag("h3");
		if (elements != null && !elements.isEmpty()) {
			return elements.get(0).ownText();
		}
		return null;
	}
	
	public static Organizer createOrganizer() {
		Organizer organizer = new Organizer();
		organizer.country = "Schweiz";
		organizer.address = "Chriesbaumstrasse 2";
		organizer.city = "8604 Volketswil";
		organizer.name = "Pasadena";
		organizer.url = "http://www.pasadena.ch";
		return organizer;
	}
	
	public static Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Chriesbaumstrasse 2";
		location.city = "8604 Volketswil";
		location.name = "Pasadena";
		location.url = "http://www.pasadena.ch";
		return location;
	}
	
	
	
	
	public static void main(String[] args) {
		List<DanceEvent> events = new PasadenaCrawler().crawlEvents();
		System.out.println(events);
	}
	

}
