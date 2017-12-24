package org.test.thread;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

public class WaitXSecond implements Runnable{

	
	private org.zkoss.zk.ui.event.EventQueue<Event> xSecondEvtQue;
	Desktop desktop;
	
	public WaitXSecond(EventQueue<Event> xSecondEvtQue){
		
		//this.desktop = null;
		this.xSecondEvtQue = xSecondEvtQue;
	}
	
	public void run() {		
		//que2 = EventQueues.lookup("count to x second", EventQueues.APPLICATION,true);
		try {
			//Executions.activate(this.desktop); 
			System.out.println("x second event start to sleep");
			Thread.sleep(5000);			
			System.out.println("x second event start to publish");
			xSecondEvtQue.publish(new Event("x second",null));
			System.out.println("x second event publish ok");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
