package ch.openech.dancer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.minimalj.application.Application;
import org.minimalj.backend.Backend;
import org.minimalj.repository.query.By;

import ch.openech.dancer.backend.provider.Tanzwerk101Rule;
import ch.openech.dancer.model.DanceEvent;

public class DancerCalendar {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

	public String getCalendar(List<DanceEvent> events) {
		StringBuilder s = new StringBuilder(20000);
		s.append("BEGIN:VCALENDAR\n");
		s.append("METHOD:PUBLISH\n");
		s.append("VERSION:2.0\n");
		for (DanceEvent event : events) {
			s.append("BEGIN:VEVENT\n");
			s.append("DTSTART:" + formatter.format(LocalDateTime.of(event.date, event.from)) + "\n");
			if (event.until != null) {
				s.append("DTEND:" + formatter.format(LocalDateTime.of(event.date.plusDays(event.from.isAfter(event.until) ? 1 : 0), event.until)) + "\n");
			}
			s.append("SUMMARY:" + event.header + "\n");
			s.append("DESCRIPTION:" + event.description + "\n");
			s.append("ORGANIZER:" + event.location.name + "\n");
			s.append("LOCATION:" + event.location.address + ", " + event.location.city + ", " + event.location.country + "\n");
			s.append("UID:" + event.id + "\n");
			s.append("END:VEVENT\n");
		}
		s.append("END:VCALENDAR\n");

		return s.toString();
	}

	public static void main(String[] args) {
		DancerApplication application = new DancerApplication();
		Application.setInstance(application);
		Backend.execute(new Tanzwerk101Rule());
		DancerCalendar calendar = new DancerCalendar();
		System.out.println(calendar.getCalendar(Backend.find(DanceEvent.class, By.all())));
	}
}
