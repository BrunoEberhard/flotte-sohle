package ch.openech.dancer.frontend;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;

import ch.openech.dancer.backend.ElSocialRule;

public class ElSocialRuleAction extends Action {

	@Override
	public void action() {
		System.out.println(Backend.execute(new ElSocialRule()));
	}

}
