package ch.openech.dancer.crawler;

import java.util.List;

import ch.openech.dancer.model.DanceEvent;

public interface DanceEventCrawler {
	
	List<DanceEvent> crawlEvents();

}
