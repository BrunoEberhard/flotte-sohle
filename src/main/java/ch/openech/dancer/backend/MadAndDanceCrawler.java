package ch.openech.dancer.backend;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.minimalj.repository.query.By;
import org.minimalj.util.StringUtils;

import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;

public class MadAndDanceCrawler extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://badenertanzcentrum.ch/events/tanz-partys/?art=2";

	@Override
	public EventUpdateCounter updateEvents() throws IOException {
		EventUpdateCounter result = new EventUpdateCounter();

		Document doc = Jsoup.connect(AGENDA_URL).userAgent(USER_AGENT).get();

		List<Integer> years = new ArrayList<>();
		years.add(LocalDate.now().getYear());
		if (LocalDate.now().getMonth() == Month.NOVEMBER || LocalDate.now().getMonth() == Month.DECEMBER) {
			years.add(LocalDate.now().getYear() + 1);
		}

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMAN);

		for (Integer year : years) {
			Elements elements = doc.getElementsContainingOwnText(year + ":");

			for (Element element : elements) {
				while (!element.parent().className().equals("paragraph")) {
					element = element.parent();
				}
				Node node = element;
				List<String> strings = new ArrayList<>();
				while (node != null) {
					if (node instanceof TextNode) {
						String s = ((TextNode) node).text();
						if (!StringUtils.isBlank(s)) {
							strings.add(s);
						}
					} else if (node instanceof Element) {
						Element e = (Element) node;
						String s = e.text();
						if (!StringUtils.isBlank(s)) {
							strings.add(s);
						}
					}
					node = node.nextSibling();
				}
				if (!strings.stream().filter(s -> s.contains("Paartanz")).findAny().isPresent())
					continue;

				String s1 = strings.get(0);
				int posColon = s1.indexOf(":");
				int posTil = s1.indexOf("-");
				String dateString = s1.substring(s1.indexOf(". ") + 2, posColon);
				LocalDate date = LocalDate.parse(dateString, dateFormatter);

				Optional<DanceEvent> danceEventOptional = findOne(DanceEvent.class, By.field(DanceEvent.$.location, location).and(By.field(DanceEvent.$.date, date)));

				DanceEvent danceEvent = danceEventOptional.orElse(new DanceEvent());

				danceEvent.header = "Happy and Mad";
				danceEvent.title = location.name;
				danceEvent.description = strings.get(2);

				danceEvent.from = LocalTime.parse(s1.substring(posColon + 1, posTil).trim().replace(".", ":"));
				danceEvent.until = LocalTime.parse(s1.substring(posTil + 1).trim().replace(".", ":"));

				danceEvent.status = EventStatus.generated;
				danceEvent.date = date;
				danceEvent.location = location;

				danceEvent.deeJay = getDeeJay(strings.get(1));

				save(danceEvent, result);
			}

		}
		return result;
	}

	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Lindenhagstrasse 3";
		location.city = "​4622 Egerkingen";
		location.name = "Happy and Mad Disco";
		location.url = "https://www.happyandmad.ch/";
		return location;
	}

}
