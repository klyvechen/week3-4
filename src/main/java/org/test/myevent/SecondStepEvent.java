package org.test.myevent;

import org.zkoss.zk.ui.event.Event;

public class SecondStepEvent extends Event {

	public final String upperCaseResult;

	public SecondStepEvent(String upperCaseResult) {
		super("onSecondStepCompleted", null);
		this.upperCaseResult = upperCaseResult;
	}

}