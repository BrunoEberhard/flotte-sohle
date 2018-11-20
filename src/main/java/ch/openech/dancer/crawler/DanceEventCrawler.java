package ch.openech.dancer.crawler;

import java.util.List;

import ch.openech.dancer.model.DanceEvent;

public interface DanceEventCrawler {
	
	public List<DanceEvent> crawlEvents();

}
