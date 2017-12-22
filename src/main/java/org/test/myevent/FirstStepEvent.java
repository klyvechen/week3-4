package org.test.myevent;

import org.zkoss.zk.ui.event.Event;

public class FirstStepEvent extends Event {

	public final int amountOfCharacters;

	public FirstStepEvent(int amountOfCharacters) {
		super("onFirstStepCompleted", null); 
		this.amountOfCharacters = amountOfCharacters; 
		}

}