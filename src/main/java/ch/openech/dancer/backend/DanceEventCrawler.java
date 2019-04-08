package ch.openech.dancer.backend;

import java.util.Optional;

import org.minimalj.backend.Backend;
import org.minimalj.backend.repository.ReadCriteriaTransaction;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldCriteria;
import org.minimalj.repository.query.Query;
import org.minimalj.transaction.Transaction;

import ch.openech.dancer.model.DeeJay;
import ch.openech.dancer.model.Location;

public abstract class DanceEventCrawler implements Transaction<Integer> {
	private static final long serialVersionUID = 1L;

	protected static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0";

	protected transient Location location;

	public static <T> Optional<T> findOne(Class<T> clazz, Query query) {
		return Backend.execute(new ReadCriteriaTransaction<T>(clazz, query)).stream().findAny();
	}

	protected void initData() {
		location = createLocation();
		location.password.setPassword(location.name.toCharArray());

		Optional<Location> existingLocation = findOne(Location.class, new FieldCriteria(Location.$.name, location.name));
		location = existingLocation.orElseGet(() -> Backend.save(location));
	}

	@Override
	public Integer execute() {
		initData();
		return crawlEvents();
	}
	
	public abstract int crawlEvents();

	protected abstract Location createLocation();

	public String getName() {
		String className = getClass().getSimpleName();
		if (className.endsWith("Crawler")) {
			return addSpaces(className.substring(0, className.length() - 7));
		} else if (className.endsWith("Import")) {
			return addSpaces(className.substring(0, className.length() - 6));
		} else if (className.endsWith("Rule")) {
			return addSpaces(className.substring(0, className.length() - 4));
		} else {
			throw new IllegalArgumentException();
		}
	}

	private String addSpaces(String s) {
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (Character.isUpperCase(c) && sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(c);
		}
		return sb.toString();
	}

	protected DeeJay getDeeJay(String djText) {
		Optional<DeeJay> deeJay = findOne(DeeJay.class, By.field(DeeJay.$.name, djText));
		if (deeJay.isPresent()) {
			return deeJay.get();
		} else {
			DeeJay newDeeJay = new DeeJay();
			newDeeJay.name = djText;
			return Backend.save(newDeeJay);
		}
	}
}