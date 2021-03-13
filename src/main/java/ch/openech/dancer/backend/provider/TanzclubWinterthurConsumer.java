package ch.openech.dancer.backend.provider;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.minimalj.backend.Backend;
import org.minimalj.util.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ch.openech.dancer.backend.DanceEventProvider;
import ch.openech.dancer.backend.EventUpdateCounter;
import ch.openech.dancer.model.DanceEvent;
import ch.openech.dancer.model.EventStatus;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Region;

public class TanzclubWinterthurConsumer extends DanceEventProvider {
	private static final long serialVersionUID = 1L;

	private static final String AGENDA_URL = "https://tanzclub-winterthur.clubdesk.com/clubdesk/ical/9997/1000003/TUN3Q0ZBaDEvZDJkN09JUWZYR1h2L1YvelhtYlVLcEVBaFFYUlpPYS9sTmFxbGIxSXB1ZmRsTlg5UnhmaGc9PQ/basic.xml?timeshift=-120&uid=1561095559070";

	@Override
	public EventUpdateCounter updateEvents() throws Exception {
		EventUpdateCounter counter = new EventUpdateCounter();

		URL url = new URL(AGENDA_URL);
		HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		if (responseCode == HttpsURLConnection.HTTP_OK) {

			try (InputStream inputStream = httpConn.getInputStream()) {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				TwEventHandler userhandler = new TwEventHandler(counter);
				saxParser.parse(inputStream, userhandler);

			}
		}
		httpConn.disconnect();

		return counter;
	}

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	private class TwEventHandler extends DefaultHandler {

		private final EventUpdateCounter counter;
		
		public TwEventHandler(EventUpdateCounter counter) {
			this.counter = counter;
		}

		public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes)
				throws SAXException {

			if (qName.equals("event")) {
				String text = attributes.getValue("text");
				if (!StringUtils.equals("Tanzabend", text)) {
					return;
				}
				
				LocalDateTime startDate = LocalDateTime.parse(attributes.getValue("start_date"), FORMATTER);
				LocalDateTime endDate = LocalDateTime.parse(attributes.getValue("end_date"), FORMATTER);
				if (startDate.toLocalDate().isBefore(LocalDate.now())) {
					return;
				}
				
				int i = Integer.valueOf(attributes.getValue("id"));
				byte[] idBytes = new byte[] { (byte) i, (byte) (i >> 8), (byte) (i >> 16), (byte) (i >> 24)};
				String id = UUID.nameUUIDFromBytes(idBytes).toString();

				DanceEvent danceEvent = Backend.read(DanceEvent.class, id);
				boolean newEvent = danceEvent == null;
				if (newEvent) {
					danceEvent = new DanceEvent();
					danceEvent.id = id;
					danceEvent.status = EventStatus.generated;
				} else if (danceEvent.status == EventStatus.blocked || danceEvent.status == EventStatus.edited) {
					return;
				}
				
				danceEvent.date = startDate.toLocalDate();
				
				danceEvent.line = text;
				danceEvent.description = "Unsere erfahrenen DJs stehen für euch an den Plattentellern und verwandeln den Saal in eine Tanzparty.\n" + 
						"Alle Tanzbegeisterten sind herzlich eingeladen und dazu aufgefordert die Stimmung aufzuheizen.\n\nMusikrichtung: Standard, Latein, Gesellschaftstanz, Discofox";
				danceEvent.price = BigDecimal.valueOf(15);
				danceEvent.priceReduced = BigDecimal.valueOf(0);

				danceEvent.from = startDate.toLocalTime();
				danceEvent.until = endDate.toLocalTime();

				danceEvent.location = location;
				
				save(danceEvent, counter);
			}
		}

	}
	  
	@Override
	public Location createLocation() {
		Location location = new Location();
		location.country = "Schweiz";
		location.address = "Marktgasse 53";
		location.city = "8400 Winterthur";
		location.region.add(Region.ZH);
		location.name = "Altes Stadthaus";
		location.url = "https://tanzclub-winterthur.ch/";
		return location;
	}

}