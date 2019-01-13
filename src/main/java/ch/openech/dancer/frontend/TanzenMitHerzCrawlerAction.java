package ch.openech.dancer.frontend;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;

import ch.openech.dancer.backend.TanzenMitHerzCrawler;

public class TanzenMitHerzCrawlerAction extends Action {

	@Override
	public void action() {
		System.out.println(Backend.execute(new TanzenMitHerzCrawler()));
	}

}
