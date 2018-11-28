package ch.openech.dancer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.minimalj.backend.Backend;
import org.minimalj.repository.query.FieldCriteria;
import org.minimalj.transaction.Transaction;

import ch.openech.dancer.crawler.PasadenaCrawler;
import ch.openech.dancer.model.Location;
import ch.openech.dancer.model.Organizer;

public class DancerInitTransaction implements Transaction<Void> {

	private static final long serialVersionUID = 1L;
	
	private final PasadenaCrawler pasadenaCrawler = new PasadenaCrawler();

	@Override
	public Void execute() {
		
		Organizer organizer = initialize(PasadenaCrawler::createOrganizer, Organizer.class, Organizer.$.name, "Pasadena",(Organizer o, Object id) -> o.id = id);
		Location location = initialize(PasadenaCrawler::createLocation, Location.class, Location.$.name, "Pasadena",(Location o, Object id) -> o.id = id);
		
		pasadenaCrawler.crawlEvents().forEach(danceEvent -> {
			
			danceEvent.location = location;
			danceEvent.organizer = organizer;
			Backend.insert(danceEvent);
			
		});
		
		return null;
	}
	
	
	private <T> T initialize (Supplier<T> supplier, Class<T> clazz, Object searchKey, String searchValue, BiConsumer<T, Object> biConsumer) {
		List<T> resultList = Backend.find(clazz, new FieldCriteria(searchKey, searchValue));
		if (resultList.isEmpty()) {
			T newObject = supplier.get();
			biConsumer.accept(newObject, Backend.insert(newObject));
			return newObject;
		}
		return resultList.get(0);
	}

}
